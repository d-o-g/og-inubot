package builder.map;


import builder.map.cache.*;
import builder.map.cache.io.FileReader;
import builder.map.cache.io.RSInputStream;
import builder.map.render.RenderModel;
import builder.map.render.plotters.DynamicLetterPlotter;
import builder.map.render.plotters.LetterPlotter;
import builder.map.render.sprites.LandscapeEntity;
import builder.map.render.sprites.Sprite;
import builder.map.thread.MapScheduler;
import builder.map.util.Class3;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.methods.traversal.graph.DijkstraPathfinder;
import com.inubot.api.methods.traversal.graph.WorldGraph;
import com.inubot.api.methods.traversal.graph.data.ObjectVertex;
import com.inubot.api.methods.traversal.graph.data.WebVertex;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.List;

public class Map extends MapScheduler {

    //  World world;

    public static final int WIDTH = 950;
    public static final int HEIGHT = 680;

    private static final long serialVersionUID = 0x4ecd9f921bb4ccd5L;
    public static boolean drawRegions = false;
    public static int mapBaseX;

    //public static final CacheSuccessorMap map = new CacheSuccessorMap();
    public static int mapBaseY;
    public static int mapWith;
    public static int mapHeight;
    public static boolean drawAreas = false;
    public static int tileViewX;
    public static int tileViewY;
    public LinkedHashSet<MapNode> waypoints = new LinkedHashSet<MapNode>();
    public LinkedHashSet<RouteNode> routes = new LinkedHashSet<RouteNode>();
    public int nonSelectedButtonUpperShadowColor;
    public int nonSelectedButtonBackgroundColor;
    public int nonSelectedButtonLowerShadowColor;
    public int selectedButtonUpperShadowColor;
    public int selectedButtonBackgroundColor;
    public int selectedButtonLowerShadowColor;
    public boolean force_repaint;
    public int renderCycle;
    public int floorTypeHashes[];
    public int floorColors[];
    public int underlayColors[][];
    public int overlayColors[][];
    public byte mapRenderRules[][];
    public byte boundaryData[][];
    public byte landmarkData[][];
    public byte entityData[][];
    public LandscapeEntity landscapeEntities[];
    public Sprite[] landmarks;
    public LetterPlotter letterPlotter;
    public DynamicLetterPlotter font11Plotter;
    public DynamicLetterPlotter font12Plotter;
    public DynamicLetterPlotter font14Plotter;
    public DynamicLetterPlotter font17Plotter;
    public DynamicLetterPlotter font19Plotter;
    public DynamicLetterPlotter font22Plotter;
    public DynamicLetterPlotter font26Plotter;
    public DynamicLetterPlotter font30Plotter;
    public int landmarkXRenderPos[];
    public int landmarkYRenderPos[];
    public int landmarkTypes[];
    public int num_ploted_landmarks;
    public int labelXPositions[];
    public int labelYPositions[];
    public int ladmarkIDs[];
    public int keyButtonBaseX;
    public int keyButtonBaseY;
    public int keyButtonWith;
    public int keyPanelHeight;
    public int keyIndexBaseFlux;
    public int DestinationIndexBase;
    public boolean keyPanelOpen;
    public int hoverKeyRealIndex;
    public int anInt138;
    public int selectedLandmarkID;
    public int flash_cycle;
    public int miniMapScale;
    public int miniMapWith;
    public int miniMapLocX;
    public int miniMapLocY;
    public boolean minimapDisplayed;
    public Sprite minimap;
    public int anInt147;
    public int anInt148;
    public int anInt149;
    public int anInt150;
    public int num_labels;
    public int labelBuffer;
    public String lableTexts[];
    public int lableXPositions[];
    public int lableYPositions[];
    public int lableTypes[];
    public double fluxScale;
    public double destinationScale;
    public String landmarkNames[] = {
            DataArchive.getValue(1), DataArchive.getValue(15), DataArchive.getValue(26), DataArchive.getValue(37), DataArchive.getValue(46), DataArchive.getValue(58), DataArchive.getValue(63), DataArchive.getValue(75), DataArchive.getValue(87), DataArchive.getValue(99),
            DataArchive.getValue(107), DataArchive.getValue(113), DataArchive.getValue(129), DataArchive.getValue(137), DataArchive.getValue(148), DataArchive.getValue(163), DataArchive.getValue(178), DataArchive.getValue(192), DataArchive.getValue(205), DataArchive.getValue(217),
            DataArchive.getValue(223), DataArchive.getValue(233), DataArchive.getValue(242), DataArchive.getValue(251), DataArchive.getValue(265), DataArchive.getValue(277), DataArchive.getValue(290), DataArchive.getValue(303), DataArchive.getValue(316), DataArchive.getValue(327),
            DataArchive.getValue(339), DataArchive.getValue(352), DataArchive.getValue(360), DataArchive.getValue(370), DataArchive.getValue(378), DataArchive.getValue(389), DataArchive.getValue(404), DataArchive.getValue(414), DataArchive.getValue(427), DataArchive.getValue(437),
            DataArchive.getValue(450), DataArchive.getValue(464), DataArchive.getValue(475), DataArchive.getValue(489), DataArchive.getValue(498), DataArchive.getValue(510), DataArchive.getValue(525), DataArchive.getValue(537), DataArchive.getValue(548), DataArchive.getValue(559),
            DataArchive.getValue(576), DataArchive.getValue(592), DataArchive.getValue(606), DataArchive.getValue(620), DataArchive.getValue(634), DataArchive.getValue(648), DataArchive.getValue(654), DataArchive.getValue(669), DataArchive.getValue(673), DataArchive.getValue(686),
            DataArchive.getValue(691)
    };
    int mtx, mty;
    boolean drawHoveredTile = true;
    boolean drawRegionData = true;
    boolean drawLandmarks = true;
    boolean drawCollision = true;
    boolean drawingOnMM = true;

    private List<WebVertex> vertices = new ArrayList<>();

    public Map() {

        nonSelectedButtonUpperShadowColor = 0x887755;
        nonSelectedButtonBackgroundColor = 0x776644;
        nonSelectedButtonLowerShadowColor = 0x665533;
        selectedButtonUpperShadowColor = 0xaa0000;
        selectedButtonBackgroundColor = 0x990000;
        selectedButtonLowerShadowColor = 0x880000;
        force_repaint = true;
        landscapeEntities = new LandscapeEntity[100];
        landmarks = new Sprite[100];
        landmarkXRenderPos = new int[2000];
        landmarkYRenderPos = new int[2000];
        landmarkTypes = new int[2000];
        labelXPositions = new int[2000];
        labelYPositions = new int[2000];
        ladmarkIDs = new int[2000];
        keyButtonBaseX = 5;
        keyButtonBaseY = 13;
        keyButtonWith = 140;
        keyPanelHeight = 470;
        keyPanelOpen = false;
        hoverKeyRealIndex = -1;
        anInt138 = -1;
        selectedLandmarkID = -1;
        minimapDisplayed = false;
        labelBuffer = 1000;
        lableTexts = new String[labelBuffer];
        lableXPositions = new int[labelBuffer];
        lableYPositions = new int[labelBuffer];
        lableTypes = new int[labelBuffer];
        fluxScale = 4D;
        destinationScale = 4D;

        loadWorld();
        load(new Mainframe(this, WIDTH, HEIGHT), WIDTH, HEIGHT);

    }

    public final void init() {
        System.out.println("init..");
        loadWorld();
        start(WIDTH, HEIGHT);
    }

    public void loadWorld() {
        if (waypoints != null) waypoints.clear();
        if (routes != null) routes.clear();

        try {
            InputStream in = new FileInputStream("./web.txt");
            Scanner s = new Scanner(in);
            while (s.hasNextLine()) {
                String line = s.nextLine();
                String[] kek = line.split(" ");
                int index = Integer.parseInt(kek[0]);
                String type = kek[1];
                int x = Integer.parseInt(kek[2]), y = Integer.parseInt(kek[3]), z = Integer.parseInt(kek[4]);
                int[] edges = new int[kek.length - 5];
                for (int i = 0; i < kek.length - 5; i++)
                    edges[i] = Integer.parseInt(kek[i + 5]);
                String name = null, action = null;
                vertices.add(type.equals("object")
                        ? new ObjectVertex(index, x, y, z, edges, name, action)
                        : new WebVertex(index, x, y, z, edges));
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //  world = new World();
        /*Route[] routes = world.getRoutes();
        System.out.println("Loading " + routes.length + " routes...");
        for( Route route : routes) {
            waypoints.add( new MapNode( route.getStart()) );
            waypoints.add( new MapNode( route.getEnd()) );
        }
        out:
        for(Route route : routes) {
            MapNode A = null, B = null;
            for(MapNode node : waypoints) {
                if(node.hash == route.getStart().hashCode()) { A = node; }
                if(node.hash == route.getEnd().hashCode()) { B = node; }
                if(A != null && B != null) {
                    this.routes.add( new RouteNode( A, B, 0 ) );
                    continue out;
                }
            }
            throw new Error( "Missing waypoint..." );

        }*/
        System.out.println("Loaded graph of size " + vertices.size());
        for (WebVertex vertex : vertices) {
            for (WebVertex edge : vertices) {
                for (int edgeI : vertex.getEdgeIndexes()) {
                    if (edge.getIndex() == edgeI) {
                        vertex.getEdges().add(edge);
                    }
                }
            }
            if (vertex.getEdges().size() != vertex.getEdgeIndexes().length) {
                System.out.println("Failed to find edges for " + vertex.getIndex());
            }
        }
        System.out.println("Routes loaded!");
    }

    public final void loadMap() {

        MapDataStore map_data = getMapData();
        renderLoadingBar(100, DataArchive.getValue(ArchiveKeys.LOADING_TEXT));
        RSInputStream dataStream = new RSInputStream(map_data.getEntry(DataArchive.getValue(1145), null));  //Size Data
        mapBaseX = dataStream.readShort();
        mapBaseY = dataStream.readShort();
        mapWith = dataStream.readShort();
        mapHeight = dataStream.readShort();
        tileViewX = 3200 - mapBaseX;    //The tile in the center of the visible map.
        tileViewY = (mapBaseY + mapHeight) - 3200;
        miniMapScale = 180;
        miniMapWith = (mapWith * miniMapScale) / mapHeight;
        miniMapLocX = WIDTH - miniMapWith - 65;
        miniMapLocY = HEIGHT - miniMapScale - 20;

        dataStream = new RSInputStream(map_data.getEntry(DataArchive.getValue(1154), null)); //Labels

        num_labels = dataStream.readShort();
        int i;
        for (i = 0; i < num_labels; i++) {
            lableTexts[i] = dataStream.readString();
            lableXPositions[i] = dataStream.readShort();
            lableYPositions[i] = dataStream.readShort();
            lableTypes[i] = dataStream.readUnsignedByte();
        }

        dataStream = new RSInputStream(map_data.getEntry(DataArchive.getValue(1165), null)); //Floor Colors
        i = dataStream.readShort();
        floorTypeHashes = new int[i + 1];
        floorColors = new int[i + 1];
        for (int i_0_ = 0; i_0_ < i; i_0_++) {
            floorTypeHashes[i_0_ + 1] = dataStream.readInt();
            floorColors[i_0_ + 1] = dataStream.readInt();
        }


        byte[] underlayData = map_data.getEntry(DataArchive.getValue(1178), null);
        byte[][] underlay = new byte[mapWith][mapHeight];
        unpackUnderlay(underlayData, underlay);
        byte[] overlayData = map_data.getEntry(DataArchive.getValue(1191), null);
        overlayColors = new int[mapWith][mapHeight];
        mapRenderRules = new byte[mapWith][mapHeight];
        mapRenderRules = new byte[mapWith][mapHeight];
        unpackOverlay(overlayData, overlayColors, mapRenderRules);
        byte flags[] = map_data.getEntry(DataArchive.getValue(1203), null);
        boundaryData = new byte[mapWith][mapHeight];
        entityData = new byte[mapWith][mapHeight];
        landmarkData = new byte[mapWith][mapHeight];
        unpackFlags(flags, boundaryData, entityData, landmarkData);
        try {
            for (int i_4_ = 0; i_4_ < 100; i_4_++)
                landscapeEntities[i_4_] = new LandscapeEntity(map_data, DataArchive.getValue(1211), i_4_);

        } catch (Exception exception) {
        }
        try {
            for (int i_5_ = 0; i_5_ < 100; i_5_++)
                landmarks[i_5_] = new Sprite(map_data, DataArchive.getValue(1220), i_5_);
        } catch (Exception exception1) {
        }
        letterPlotter = new LetterPlotter(map_data, DataArchive.getValue(1232), false);
        font11Plotter = new DynamicLetterPlotter(11, true, this);
        font12Plotter = new DynamicLetterPlotter(12, true, this);
        font14Plotter = new DynamicLetterPlotter(14, true, this);
        font17Plotter = new DynamicLetterPlotter(17, true, this);
        font19Plotter = new DynamicLetterPlotter(19, true, this);
        font22Plotter = new DynamicLetterPlotter(22, true, this);
        font26Plotter = new DynamicLetterPlotter(26, true, this);
        font30Plotter = new DynamicLetterPlotter(30, true, this);
        underlayColors = new int[mapWith][mapHeight];
        equals(underlay, underlayColors);
        minimap = new Sprite(miniMapWith, miniMapScale);
        minimap.attach();
        drawingOnMM = true;
        copyMap(0, 0, mapWith, mapHeight, 0, 0, miniMapWith, miniMapScale); //copy the map to the minimap overlayColors.
        drawingOnMM = false;
        //       copyMap(1500, 1500, mapWith, mapHeight, 0, 0, miniMapWith, miniMapScale);
        RenderModel.drawRectangle(0, 0, miniMapWith, miniMapScale, 0); //Outer Minimap Border
        RenderModel.drawRectangle(1, 1, miniMapWith - 2, miniMapScale - 2, nonSelectedButtonUpperShadowColor); //Inner Minimap Border
        main_overlay.attach();

    }

    public final void unpackFlags(byte[] flags, byte[][] boundaryData, byte[][] entityData, byte[][] landmarkData) {
        for (int i = 0; i < flags.length; ) {
            int map_x = (flags[i++] & 0xff) * 64 - mapBaseX;
            int map_y = (flags[i++] & 0xff) * 64 - mapBaseY;
            if (map_x > 0 && map_y > 0 && map_x + 64 < mapWith && map_y + 64 < mapHeight) {
                for (int x0 = 0; x0 < 64; x0++) {
                    byte boundary_data[] = boundaryData[x0 + map_x];
                    byte entity_data[] = entityData[x0 + map_x];
                    byte landmark_data[] = landmarkData[x0 + map_x];
                    int plot_y = mapHeight - map_y - 1;
                    for (int y0 = -64; y0 < 0; y0++) {
                        do {
                            int flag = flags[i++] & 0xff;
                            if (flag == 0) break;
                            if (flag < 29) boundary_data[plot_y] = (byte) flag;
                            else if (flag < 160) entity_data[plot_y] = (byte) (flag - 28);
                            else {
                                landmark_data[plot_y] = (byte) (flag - 159);
                                labelXPositions[num_ploted_landmarks] = x0 + map_x;
                                labelYPositions[num_ploted_landmarks] = plot_y;
                                ladmarkIDs[num_ploted_landmarks] = flag - 160;
                                num_ploted_landmarks++;
                            }
                        } while (true);
                        plot_y--;
                    }

                }
            } else {
                for (int i_18_ = 0; i_18_ < 64; i_18_++) {
                    for (int i_19_ = -64; i_19_ < 0; i_19_++) {
                        int i_20_;
                        do
                            i_20_ = flags[i++];
                        while (i_20_ != 0);
                    }

                }
            }
        }
    }

    public final void unpackUnderlay(byte src[], byte dest[][]) {
        //      System.out.println(mapBaseX + "," + mapBaseY);
        for (int i = 0; i < src.length; ) {
            int gx = (src[i++] & 0xff) * 64;
            int gy = (src[i++] & 0xff) * 64;
            int dx = gx - mapBaseX;
            int dy = gy - mapBaseY;
            if (dx > 0 && dy > 0 && dx + 64 < mapWith && dy + 64 < mapHeight) {
                for (int y0 = 0; y0 < 64; y0++) {
                    byte is_25_[] = dest[y0 + dx];
                    int i_26_ = mapHeight - dy - 1;
                    for (int x0 = -64; x0 < 0; x0++)
                        is_25_[i_26_--] = src[i++];
                }
            } else {
                i += 4096;
            }
        }

    }

    public final void unpackOverlay(byte data[], int overlay[][], byte is_29_[][]) {
        for (int i = 0; i < data.length; ) {
            int i_30_ = (data[i++] & 0xff) * 64 - mapBaseX;
            int i_31_ = (data[i++] & 0xff) * 64 - mapBaseY;
            if (i_30_ > 0 && i_31_ > 0 && i_30_ + 64 < mapWith && i_31_ + 64 < mapHeight) {
                for (int i_32_ = 0; i_32_ < 64; i_32_++) {
                    int is_33_[] = overlay[i_32_ + i_30_];
                    byte is_34_[] = is_29_[i_32_ + i_30_];
                    int i_35_ = mapHeight - i_31_ - 1;
                    for (int i_36_ = -64; i_36_ < 0; i_36_++) {
                        int floor_type = data[i++];
                        if (floor_type != 0) {
                            is_34_[i_35_] = data[i++];
                            int ovverlay_rgb = 0;
                            if (floor_type > 0)
                                ovverlay_rgb = floorColors[floor_type];
                            is_33_[i_35_--] = ovverlay_rgb;
                        } else {
                            is_33_[i_35_--] = 0;
                        }
                    }

                }

            } else {
                for (int i_39_ = -4096; i_39_ < 0; i_39_++) {
                    int i_40_ = data[i++];
                    if (i_40_ != 0)
                        i++;
                }

            }
        }

    }

    public final void equals(byte is[][], int is_41_[][]) {
        int i = mapWith;
        int i_42_ = mapHeight;
        int is_43_[] = new int[i_42_];
        for (int i_44_ = 0; i_44_ < i_42_; i_44_++)
            is_43_[i_44_] = 0;

        for (int i_45_ = 5; i_45_ < i - 5; i_45_++) {
            byte is_46_[] = is[i_45_ + 5];
            byte is_47_[] = is[i_45_ - 5];
            for (int i_48_ = 0; i_48_ < i_42_; i_48_++)
                is_43_[i_48_] += floorTypeHashes[is_46_[i_48_] & 0xff] - floorTypeHashes[is_47_[i_48_] & 0xff];

            if (i_45_ > 10 && i_45_ < i - 10) {
                int i_49_ = 0;
                int i_50_ = 0;
                int i_51_ = 0;
                int is_52_[] = is_41_[i_45_];
                for (int i_53_ = 5; i_53_ < i_42_ - 5; i_53_++) {
                    int i_54_ = is_43_[i_53_ - 5];
                    int i_55_ = is_43_[i_53_ + 5];
                    i_49_ += (i_55_ >> 20) - (i_54_ >> 20);
                    i_50_ += (i_55_ >> 10 & 0x3ff) - (i_54_ >> 10 & 0x3ff);
                    i_51_ += (i_55_ & 0x3ff) - (i_54_ & 0x3ff);
                    if (i_51_ > 0)
                        is_52_[i_53_] = exists((double) i_49_ / 8533D, (double) i_50_ / 8533D, (double) i_51_ / 8533D);
                }

            }
        }

    }

    public final int exists(double d, double d_56_, double d_57_) {
        double d_58_ = d_57_;
        double d_59_ = d_57_;
        double d_60_ = d_57_;
        if (d_56_ != 0.0D) {
            double d_61_;
            if (d_57_ < 0.5D)
                d_61_ = d_57_ * (1.0D + d_56_);
            else
                d_61_ = (d_57_ + d_56_) - d_57_ * d_56_;
            double d_62_ = 2D * d_57_ - d_61_;
            double d_63_ = d + 0.33333333333333331D;
            if (d_63_ > 1.0D)
                d_63_--;
            double d_64_ = d;
            double d_65_ = d - 0.33333333333333331D;
            if (d_65_ < 0.0D)
                d_65_++;
            if (6D * d_63_ < 1.0D)
                d_58_ = d_62_ + (d_61_ - d_62_) * 6D * d_63_;
            else if (2D * d_63_ < 1.0D)
                d_58_ = d_61_;
            else if (3D * d_63_ < 2D)
                d_58_ = d_62_ + (d_61_ - d_62_) * (0.66666666666666663D - d_63_) * 6D;
            else
                d_58_ = d_62_;
            if (6D * d_64_ < 1.0D)
                d_59_ = d_62_ + (d_61_ - d_62_) * 6D * d_64_;
            else if (2D * d_64_ < 1.0D)
                d_59_ = d_61_;
            else if (3D * d_64_ < 2D)
                d_59_ = d_62_ + (d_61_ - d_62_) * (0.66666666666666663D - d_64_) * 6D;
            else
                d_59_ = d_62_;
            if (6D * d_65_ < 1.0D)
                d_60_ = d_62_ + (d_61_ - d_62_) * 6D * d_65_;
            else if (2D * d_65_ < 1.0D)
                d_60_ = d_61_;
            else if (3D * d_65_ < 2D)
                d_60_ = d_62_ + (d_61_ - d_62_) * (0.66666666666666663D - d_65_) * 6D;
            else
                d_60_ = d_62_;
        }
        int i = (int) (d_58_ * 256D);
        int i_66_ = (int) (d_59_ * 256D);
        int i_67_ = (int) (d_60_ * 256D);
        int i_68_ = (i << 16) + (i_66_ << 8) + i_67_;
        return i_68_;
    }

    public final void dispose() {
        try {
            floorTypeHashes = null;
            floorColors = null;
            underlayColors = null;
            overlayColors = null;
            mapRenderRules = null;
            boundaryData = null;
            landmarkData = null;
            entityData = null;
            landscapeEntities = null;
            landmarks = null;
            letterPlotter = null;
            landmarkXRenderPos = null;
            landmarkYRenderPos = null;
            landmarkTypes = null;
            labelXPositions = null;
            labelYPositions = null;
            ladmarkIDs = null;
            minimap = null;
            lableTexts = null;
            lableXPositions = null;
            lableYPositions = null;
            lableTypes = null;
            landmarkNames = null;
            System.gc();
        } catch (Throwable throwable) {
        }
    }

    public final void processEvents() {
        // System.out.println((new StringBuilder()).apply(DataArchive.getValue(1100)).apply(mapWith * 2).apply(DataArchive.getValue(1114)).apply(mapHeight * 2).underlayColors());
        if (anIntArray105[1] == 1) {
            tileViewX = (int) ((double) tileViewX - 16D / fluxScale);
            force_repaint = true;
        }
        if (anIntArray105[2] == 1) {
            tileViewX = (int) ((double) tileViewX + 16D / fluxScale);
            force_repaint = true;
        }
        if (anIntArray105[3] == 1) {
            tileViewY = (int) ((double) tileViewY - 16D / fluxScale);
            force_repaint = true;
        }
        if (anIntArray105[4] == 1) {
            tileViewY = (int) ((double) tileViewY + 16D / fluxScale);
            force_repaint = true;
        }
        int i;
        for (i = 1; i > 0; ) {
            i = method42();
            if (i == 49) {
                destinationScale = 3D;
                force_repaint = true;
            }
            if (i == 50) {
                destinationScale = 4D;
                force_repaint = true;
            }
            if (i == 51) {
                destinationScale = 10D;
                force_repaint = true;
            }
            if (i == 52) {
                destinationScale = 8D;
                force_repaint = true;
            }
            if (i == 107 || i == 75) {
                keyPanelOpen = !keyPanelOpen;
                force_repaint = true;
            }
            if (i == 111 || i == 79) {
                minimapDisplayed = !minimapDisplayed;
                force_repaint = true;
            }
            if (mainFrame != null && i == 101) {

                System.out.println(DataArchive.getValue(1050));
                Sprite class9_sub1_sub1_sub3 = new Sprite(mapWith * 2, mapHeight * 2);
                class9_sub1_sub1_sub3.attach();
                copyMap(0, 0, mapWith, mapHeight, 0, 0, mapWith * 2, mapHeight * 2);
                main_overlay.attach();
                int i_69_ = class9_sub1_sub1_sub3.colorModel.length;
                byte is[] = new byte[i_69_ * 3];
                int i_70_ = 0;
                for (int i_71_ = 0; i_71_ < i_69_; i_71_++) {
                    int i_72_ = class9_sub1_sub1_sub3.colorModel[i_71_];
                    is[i_70_++] = (byte) (i_72_ >> 16);
                    is[i_70_++] = (byte) (i_72_ >> 8);
                    is[i_70_++] = (byte) i_72_;
                }

                //            System.out.println(DataArchive.getValue(1069));
                try {
                    BufferedOutputStream bufferedoutputstream = new BufferedOutputStream(
                            new FileOutputStream((new StringBuilder()).append(DataArchive.getValue(1084)).
                                    append(mapWith * 2).append(DataArchive.getValue(1089)).append(mapHeight * 2).
                                    append(DataArchive.getValue(1091)).toString()));
                    bufferedoutputstream.write(is);
                    bufferedoutputstream.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                System.out.println((new StringBuilder()).append(DataArchive.getValue(1100)).append(mapWith * 2).append(DataArchive.getValue(1114)).append(mapHeight * 2).toString());
            }
        }

        if (pressKeyID == 1) {

            anInt147 = mousePressLocX;
            anInt148 = mousePressLocY;
            anInt149 = tileViewX;
            anInt150 = tileViewY;
            if (mousePressLocX > 170 && mousePressLocX < 220 && mousePressLocY > 471 && mousePressLocY < 503) {
                destinationScale = 2D;
                anInt147 = -1;
            }
       /*     if (mousePressLocX > 230 && mousePressLocX < 280 && mousePressLocY > 471 && mousePressLocY < 503) {
                destinationScale = 4D;
                anInt147 = -1;
            }*/
            if (mousePressLocX > 230 && mousePressLocX < 280 && mousePressLocY > 471 && mousePressLocY < 503) {
                destinationScale = 6D;
                anInt147 = -1;
            }
            if (mousePressLocX > 290 && mousePressLocX < 340 && mousePressLocY > 471 && mousePressLocY < 5033) {
                destinationScale = 24D;
                anInt147 = -1;
            }
            if (mousePressLocX > keyButtonBaseX && mousePressLocY > keyButtonBaseY + keyPanelHeight && mousePressLocX < keyButtonBaseX + keyButtonWith && mousePressLocY < 503) {
                keyPanelOpen = !keyPanelOpen;
                anInt147 = -1;
            }
            if (mousePressLocX > miniMapLocX && mousePressLocY > miniMapLocY + miniMapScale && mousePressLocX < miniMapLocX + miniMapWith && mousePressLocY < 503) {
                minimapDisplayed = !minimapDisplayed;
                anInt147 = -1;
            }
            if (keyPanelOpen) {
                if (mousePressLocX > keyButtonBaseX && mousePressLocY > keyButtonBaseY && mousePressLocX < keyButtonBaseX + keyButtonWith && mousePressLocY < keyButtonBaseY + keyPanelHeight)
                    anInt147 = -1;
                if (mousePressLocX > keyButtonBaseX && mousePressLocY > keyButtonBaseY && mousePressLocX < keyButtonBaseX + keyButtonWith && mousePressLocY < keyButtonBaseY + 18 && DestinationIndexBase > 0)
                    DestinationIndexBase -= 25;
                if (mousePressLocX > keyButtonBaseX && mousePressLocY > (keyButtonBaseY + keyPanelHeight) - 18 && mousePressLocX < keyButtonBaseX + keyButtonWith && mousePressLocY < keyButtonBaseY + keyPanelHeight && DestinationIndexBase < 50)
                    DestinationIndexBase += 25;
            }
            force_repaint = true;
        }
        if (keyPanelOpen) {
            hoverKeyRealIndex = -1;
            if (mouse_x > keyButtonBaseX && mouse_x < keyButtonBaseX + keyButtonWith) {
                i = keyButtonBaseY + 21 + 5;
                for (int sub_index = 0; sub_index < 25; sub_index++)
                    if (sub_index + keyIndexBaseFlux >= landmarkNames.length ||
                            !landmarkNames[sub_index + keyIndexBaseFlux].equals(DataArchive.getValue(669))) {
                        if (mouse_y >= i && mouse_y < i + 17) {
                            hoverKeyRealIndex = sub_index + keyIndexBaseFlux;
                            if (pressKeyID == 1) {
                                selectedLandmarkID = sub_index + keyIndexBaseFlux;
                                flash_cycle = 50;
                            }
                        }
                        i += 17;
                    }

            }
            if (hoverKeyRealIndex != anInt138) {
                anInt138 = hoverKeyRealIndex;
                force_repaint = true;
            }
        }
        if ((anInt99 == 1 || pressKeyID == 1) && minimapDisplayed) {
            i = mousePressLocX;
            int i_74_ = mousePressLocY;
            if (anInt99 == 1) {
                i = mouse_x;
                i_74_ = mouse_y;
            }
            if (i > miniMapLocX && i_74_ > miniMapLocY && i < miniMapLocX + miniMapWith && i_74_ < miniMapLocY + miniMapScale) {
                tileViewX = ((i - miniMapLocX) * mapWith) / miniMapWith;
                tileViewY = ((i_74_ - miniMapLocY) * mapHeight) / miniMapScale;
                anInt147 = -1;
                force_repaint = true;
            }
        }
        if (anInt99 == 1 && anInt147 != -1) {
            tileViewX = anInt149 + (int) (((double) (anInt147 - mouse_x) * 2D) / destinationScale);
            tileViewY = anInt150 + (int) (((double) (anInt148 - mouse_y) * 2D) / destinationScale);
            force_repaint = true;
        }
        if (fluxScale < destinationScale) {
            force_repaint = true;
            fluxScale += fluxScale / 30D;
            if (fluxScale > destinationScale)
                fluxScale = destinationScale;
        }
        if (fluxScale > destinationScale) {
            force_repaint = true;
            fluxScale -= fluxScale / 30D;
            if (fluxScale < destinationScale)
                fluxScale = destinationScale;
        }

        if (keyIndexBaseFlux < DestinationIndexBase) {
            force_repaint = true;
            keyIndexBaseFlux++;
        }
        if (keyIndexBaseFlux > DestinationIndexBase) {
            force_repaint = true;
            keyIndexBaseFlux--;
        }
        if (flash_cycle > 0) {
            force_repaint = true;
            flash_cycle--;
        }
        i = tileViewX - (int) (WIDTH / fluxScale);
        int i_75_ = tileViewY - (int) (HEIGHT / fluxScale);
        int i_76_ = tileViewX + (int) (WIDTH / fluxScale);
        int i_77_ = tileViewY + (int) (HEIGHT / fluxScale);
        if (i < 48)
            tileViewX = 48 + (int) (WIDTH / fluxScale);
        if (i_75_ < 48)
            tileViewY = 48 + (int) (HEIGHT / fluxScale);
        if (i_76_ > mapWith - 48)
            tileViewX = mapWith - 48 - (int) (WIDTH / fluxScale);
        if (i_77_ > mapHeight - 48)
            tileViewY = mapHeight - 48 - (int) (HEIGHT / fluxScale);
    }

    public final void paint() {

        if (force_repaint) {
            force_repaint = false;
            renderCycle = 0;
            RenderModel.clear();
            int startX = tileViewX - (int) (WIDTH / fluxScale);
            int startY = tileViewY - (int) (HEIGHT / fluxScale);
            int endX = tileViewX + (int) (WIDTH / fluxScale);
            int endY = tileViewY + (int) (HEIGHT / fluxScale);
            //    System.out.println(startX + "," + startY + "," + endX + "," + i_80_);
            copyMap(startX, startY, endX, endY, 0, 0, WIDTH, HEIGHT);
            if (minimapDisplayed) {
                minimap.render0(miniMapLocX, miniMapLocY);

                RenderModel.fillRectangle(miniMapLocX + (miniMapWith * startX) / mapWith, miniMapLocY + (miniMapScale * startY) / mapHeight, ((endX - startX) * miniMapWith) / mapWith, ((endY - startY) * miniMapScale) / mapHeight, 0xff0000, 128);
                RenderModel.drawRectangle(miniMapLocX + (miniMapWith * startX) / mapWith, miniMapLocY + (miniMapScale * startY) / mapHeight, ((endX - startX) * miniMapWith) / mapWith, ((endY - startY) * miniMapScale) / mapHeight, 0xff0000);

                if (flash_cycle > 0 && flash_cycle % 10 < 5) { //Every 5 cycles, it'll draw 5

                    for (int i = 0; i < num_ploted_landmarks; i++)
                        if (ladmarkIDs[i] == selectedLandmarkID) {
                            int minimap_x = miniMapLocX + (miniMapWith * labelXPositions[i]) / mapWith;
                            int minimap_y = miniMapLocY + (miniMapScale * labelYPositions[i]) / mapHeight;
                            RenderModel.fillCircle(minimap_x, minimap_y, 2, 0xffff00, 256);
                        }

                }
            }
            if (keyPanelOpen) {
                renderButton(keyButtonBaseX, keyButtonBaseY, keyButtonWith, 18, 0x999999, 0x777777, 0x555555, DataArchive.getValue(814)); //Previous
                renderButton(keyButtonBaseX, keyButtonBaseY + 18, keyButtonWith, keyPanelHeight - 36, 0x999999, 0x777777, 0x555555, ""); //Background
                renderButton(keyButtonBaseX, (keyButtonBaseY + keyPanelHeight) - 18, keyButtonWith, 18, 0x999999, 0x777777, 0x555555, DataArchive.getValue(824)); //Next page
                int keyArrayBaseY = keyButtonBaseY + 3 + 18;
                for (int hoverKeySubIndex = 0; hoverKeySubIndex < 25; hoverKeySubIndex++) {
                    if (hoverKeySubIndex + keyIndexBaseFlux < landmarks.length && hoverKeySubIndex + keyIndexBaseFlux < landmarkNames.length) {
                        if (landmarkNames[hoverKeySubIndex + keyIndexBaseFlux].equals(DataArchive.getValue(669)))
                            continue;
                        landmarks[hoverKeySubIndex + keyIndexBaseFlux].render(keyButtonBaseX + 3, keyArrayBaseY);
                        letterPlotter.drawString(landmarkNames[hoverKeySubIndex + keyIndexBaseFlux], keyButtonBaseX + 21, keyArrayBaseY + 14, 0); //Shadow
                        int keyTextColor = 0xffffff;
                        if (hoverKeyRealIndex == hoverKeySubIndex + keyIndexBaseFlux) { //Your hovering over it
                            keyTextColor = 0xbbaaaa;
                        }
                        if (flash_cycle > 0 && flash_cycle % 10 < 5 && selectedLandmarkID == hoverKeySubIndex + keyIndexBaseFlux) {
                            keyTextColor = 0xffff00;
                        }
                        letterPlotter.drawString(landmarkNames[hoverKeySubIndex + keyIndexBaseFlux], keyButtonBaseX + 20, keyArrayBaseY + 13, keyTextColor);
                    }
                    keyArrayBaseY += 17;
                }
            }
            int s = -2;
            renderButton(miniMapLocX, miniMapLocY + miniMapScale, miniMapWith, 18, nonSelectedButtonUpperShadowColor, nonSelectedButtonBackgroundColor, nonSelectedButtonLowerShadowColor, DataArchive.getValue(834));
            renderButton(keyButtonBaseX, keyButtonBaseY + keyPanelHeight, keyButtonWith, 18, nonSelectedButtonUpperShadowColor, nonSelectedButtonBackgroundColor, nonSelectedButtonLowerShadowColor, DataArchive.getValue(843));
            if (destinationScale == 2D)
                renderButton(170 + s, 471, 50, 30, selectedButtonUpperShadowColor, selectedButtonBackgroundColor, selectedButtonLowerShadowColor, "Min");
            else
                renderButton(170 + s, 471, 50, 30, nonSelectedButtonUpperShadowColor, nonSelectedButtonBackgroundColor, nonSelectedButtonLowerShadowColor, "Min");
          /*  if (destinationScale == 4D)
                renderButton(230+s, 471, 50, 30, selectedButtonUpperShadowColor, selectedButtonBackgroundColor, selectedButtonLowerShadowColor, DataArchive.getValue(851));
            else
                renderButton(230+s, 471, 50, 30, nonSelectedButtonUpperShadowColor, nonSelectedButtonBackgroundColor, nonSelectedButtonLowerShadowColor, DataArchive.getValue(851));*/
            if (destinationScale == 6D)
                renderButton(230 + s, 471, 50, 30, selectedButtonUpperShadowColor, selectedButtonBackgroundColor, selectedButtonLowerShadowColor, "Med");
            else
                renderButton(230 + s, 471, 50, 30, nonSelectedButtonUpperShadowColor, nonSelectedButtonBackgroundColor, nonSelectedButtonLowerShadowColor, "Med");
            if (destinationScale == 24D)
                renderButton(290 + s, 471, 50, 30, selectedButtonUpperShadowColor, selectedButtonBackgroundColor, selectedButtonLowerShadowColor, "Max");
            else
                renderButton(290 + s, 471, 50, 30, nonSelectedButtonUpperShadowColor, nonSelectedButtonBackgroundColor, nonSelectedButtonLowerShadowColor, "Max");
        }

        renderCycle--;
        if (renderCycle <= 0) {
            main_overlay.applyGraphics(graphics, 0, 0);
            renderCycle = 50;
        }

    }

    public final void render() {
        renderCycle = 0;
    }

    public final void renderButton(int x, int y, int with, int height, int upperShadowRGB, int backgroundRGB, int lowerShadowRGB,
                                   String text) {
        //     System.out.mapHeight(x + "," + y + "," + model_with + "," + model_height + "," + upperShadowRGB + "," + backgroundRGB + "," + lowerShadowRGB);
        RenderModel.drawRectangle(x, y, with, height, 0);
        x++;//upper shading
        y++;//uppers shading
        with -= 2; //lower shading
        height -= 2; //lower shading
        RenderModel.fillRectangle(x, y, with, height, backgroundRGB);
        RenderModel.drawHorizontalLine(x, y, with, upperShadowRGB);
        RenderModel.drawVerticalLine(x, y, height, upperShadowRGB);
        RenderModel.drawHorizontalLine(x, (y + height) - 1, with, lowerShadowRGB);
        RenderModel.drawVerticalLine((x + with) - 1, y, height, lowerShadowRGB);
        letterPlotter.drawCenterString(text, x + with / 2 + 1, y + height / 2 + 1 + 4, 0);
        letterPlotter.drawCenterString(text, x + with / 2, y + height / 2 + 4, 0xffffff);
    }

    public final void copyMap(int sourceTopLeftX, int sourceTopLeftY,  //Pixel Cords
                              int sourceBottomRightX, int sourceBottomRightY,
                              int destinationTopLeftX, int destinationTopLeftY,
                              int destinationBottomRightX, int destinationBottomRightY) {
        int source_with = sourceBottomRightX - sourceTopLeftX;
        int source_height = sourceBottomRightY - sourceTopLeftY;
        int dest_with = destinationBottomRightX - destinationTopLeftX;
        int dest_height = destinationBottomRightY - destinationTopLeftY;
        int with_scale = (dest_with << 16) / source_with;
        int height_scale = (dest_height << 16) / source_height;
        for (int px = 0; px < source_with; px++) {
            int x_tail = with_scale * px >> 16;
            int x_tip = with_scale * (px + 1) >> 16;
            int with = x_tip - x_tail;
            if (with > 0) {
                x_tail += destinationTopLeftX;
                x_tip += destinationTopLeftX;
                int[] underlay_colors = underlayColors[px + sourceTopLeftX];
                int[] overlay_colors = overlayColors[px + sourceTopLeftX];
                byte[] render_rules = mapRenderRules[px + sourceTopLeftX];
                for (int py = 0; py < source_height; py++) {
                    int y_tail = height_scale * py >> 16;
                    int y_tip = height_scale * (py + 1) >> 16;
                    int height = y_tip - y_tail;
                    if (height > 0) {
                        y_tail += destinationTopLeftY;
                        y_tip += destinationTopLeftY;
                        int overlay_color = overlay_colors[py + sourceTopLeftY];
                        if (overlay_color == 0) {
                            RenderModel.fillRectangle(x_tail, y_tail, with, height, underlay_colors[py + sourceTopLeftY]);
                        } else {
                            int rule = render_rules[py + sourceTopLeftY];
                            int curvature = rule & 0xfc;
                            if (curvature == 0 || with <= 1 || height <= 1) {
                                RenderModel.fillRectangle(x_tail, y_tail, with, height, overlay_color);
                            } else {
                                //  System.out.println("curve:" + (curvature >> 2));
                                //   System.out.println("rot:" + (rule & 3));
                                drawCurve(RenderModel.cluster, y_tail * RenderModel.map_with + x_tail, underlay_colors[py + sourceTopLeftY], overlay_color, with, height, curvature >> 2, rule & 3);
                            }
                        }
                    }
                }
            }
        }

        if (source_with <= dest_with) {
            int landmark_index = 0;
            for (int x0 = 0; x0 < source_with; x0++) {
                int x_tail = with_scale * x0 >> 16;
                int x_tip = with_scale * (x0 + 1) >> 16;
                int with = x_tip - x_tail;
                if (with > 0) {
                    byte boundary_data[] = boundaryData[x0 + sourceTopLeftX];
                    byte entity_data[] = entityData[x0 + sourceTopLeftX];
                    byte landmark_data[] = landmarkData[x0 + sourceTopLeftX];
                    for (int y0 = 0; y0 < source_height; y0++) {
                        int y_tail = height_scale * y0 >> 16;
                        int y_tip = height_scale * (y0 + 1) >> 16;
                        int height = y_tip - y_tail;
                        if (height > 0) {
                            int boundary_type = boundary_data[y0 + sourceTopLeftY] & 0xff;
                            if (boundary_type != 0) {
                                int x;
                                if (with == 1) x = x_tail;
                                else x = x_tip - 1;
                                int y;
                                if (height == 1) y = y_tail;
                                else y = y_tip - 1;
                                int boundary_color = 0xcccccc;
                                if (boundary_type >= 5 && boundary_type <= 8 ||
                                        boundary_type >= 13 && boundary_type <= 16 ||
                                        boundary_type >= 21 && boundary_type <= 24 ||
                                        boundary_type == 27 || boundary_type == 28) {
                                    boundary_color = 0xcc0000;
                                    boundary_type -= 4;
                                }
                                if (boundary_type == 1)
                                    RenderModel.drawVerticalLine(x_tail, y_tail, height, boundary_color);
                                else if (boundary_type == 2)
                                    RenderModel.drawHorizontalLine(x_tail, y_tail, with, boundary_color);
                                else if (boundary_type == 3)
                                    RenderModel.drawVerticalLine(x, y_tail, height, boundary_color);
                                else if (boundary_type == 4)
                                    RenderModel.drawHorizontalLine(x_tail, y, with, boundary_color);
                                else if (boundary_type == 9) {
                                    RenderModel.drawVerticalLine(x_tail, y_tail, height, 0xffffff);
                                    RenderModel.drawHorizontalLine(x_tail, y_tail, with, boundary_color);
                                } else if (boundary_type == 10) {
                                    RenderModel.drawVerticalLine(x, y_tail, height, 0xffffff);
                                    RenderModel.drawHorizontalLine(x_tail, y_tail, with, boundary_color);
                                } else if (boundary_type == 11) {
                                    RenderModel.drawVerticalLine(x, y_tail, height, 0xffffff);
                                    RenderModel.drawHorizontalLine(x_tail, y, with, boundary_color);
                                } else if (boundary_type == 12) {
                                    RenderModel.drawVerticalLine(x_tail, y_tail, height, 0xffffff);
                                    RenderModel.drawHorizontalLine(x_tail, y, with, boundary_color);
                                } else if (boundary_type == 17)
                                    RenderModel.drawHorizontalLine(x_tail, y_tail, 1, boundary_color);
                                else if (boundary_type == 18)
                                    RenderModel.drawHorizontalLine(x, y_tail, 1, boundary_color);
                                else if (boundary_type == 19)
                                    RenderModel.drawHorizontalLine(x, y, 1, boundary_color);
                                else if (boundary_type == 20)
                                    RenderModel.drawHorizontalLine(x_tail, y, 1, boundary_color);
                                else if (boundary_type == 25) {
                                    for (int rise = 0; rise < height; rise++)
                                        RenderModel.drawHorizontalLine(x_tail + rise, y - rise, 1, boundary_color);
                                } else if (boundary_type == 26) {
                                    for (int i_133_ = 0; i_133_ < height; i_133_++)
                                        RenderModel.drawHorizontalLine(x_tail + i_133_, y_tail + i_133_, 1, boundary_color);
                                }
                            }
                            int entity_type = entity_data[y0 + sourceTopLeftY] & 0xFF;
                            if (entity_type != 0)
                                landscapeEntities[entity_type - 1].render(x_tail - with / 2, y_tail - height / 2, with * 2, height * 2);
                            int landmark_type = landmark_data[y0 + sourceTopLeftY] & 0xff;
                            if (landmark_type != 0) {
                                landmarkTypes[landmark_index] = landmark_type - 1;
                                landmarkXRenderPos[landmark_index] = x_tail + with / 2;
                                landmarkYRenderPos[landmark_index] = y_tail + height / 2;
                                landmark_index++;
                            }
/*                           //  int maxY = destinationTopLeftY + (dest_height * (map_y - sourceTopLeftY)) / source_height;
                            System.out.println("tip "+x_tip);
                            System.out.println(x_tail);
                            System.out.println(y_tip);
                            System.out.println(y_tail);*/
                            if (mouse_x >= x_tail && mouse_x <= x_tip &&
                                    mouse_y >= y_tail && mouse_y <= y_tip) {
                                mtx = ((source_with * (x_tip - destinationTopLeftX)) / dest_with) + mapBaseX + sourceTopLeftX;
                                mty = mapBaseY + mapHeight - sourceTopLeftY - ((source_height * (y_tip - destinationTopLeftY)) / dest_height) - 1;


                            }
                        }
                    }
                }
            }

            if (drawLandmarks) {
                for (int i = 0; i < landmark_index; i++)
                    if (landmarks[landmarkTypes[i]] != null)
                        landmarks[landmarkTypes[i]].render(landmarkXRenderPos[i] - 7, landmarkYRenderPos[i] - 7);

                if (flash_cycle > 0) {
                    for (int i = 0; i < landmark_index; i++) {
                        if (landmarkTypes[i] == selectedLandmarkID) {
                            landmarks[landmarkTypes[i]].render(landmarkXRenderPos[i] - 7, landmarkYRenderPos[i] - 7);
                            if (flash_cycle % 10 < 5) {
                                RenderModel.fillCircle(landmarkXRenderPos[i], landmarkYRenderPos[i], 15, 0xffff00, 128); //Outer Glow
                                RenderModel.fillCircle(landmarkXRenderPos[i], landmarkYRenderPos[i], 7, 0xffffff, 256); //Inner Glow  //Note: all landmarks are 14 pixels wide. (14x14)
                            }
                        }
                    }
                }
            }

            if (fluxScale == destinationScale && drawAreas) {
                for (int label_index = 0; label_index < num_labels; label_index++) {
                    int rx = lableXPositions[label_index];
                    int ry = lableYPositions[label_index];
                    rx -= mapBaseX;
                    ry = (mapBaseY + mapHeight) - ry;
                    int string_x = destinationTopLeftX + (dest_with * (rx - sourceTopLeftX)) / source_with;
                    int string_y = destinationTopLeftY + (dest_height * (ry - sourceTopLeftY)) / source_height;
                    int area_type = lableTypes[label_index];
                    int text_rgb = 0xffffff;
                    DynamicLetterPlotter plotter = null;
                    if (area_type == 0) {  //Normal
                        if (fluxScale == 3D)
                            plotter = font11Plotter;
                        if (fluxScale == 4D)
                            plotter = font12Plotter;
                        if (fluxScale == 6D)
                            plotter = font14Plotter;
                        if (fluxScale == 8D)
                            plotter = font17Plotter;
                    }
                    if (area_type == 1) { //City
                        if (fluxScale == 3D)
                            plotter = font14Plotter;
                        if (fluxScale == 4D)
                            plotter = font17Plotter;
                        if (fluxScale == 6D)
                            plotter = font19Plotter;
                        if (fluxScale == 8D)
                            plotter = font22Plotter;
                    }
                    if (area_type == 2) {   //Landmass
                        text_rgb = 0xffaa00;
                        if (fluxScale == 3D)
                            plotter = font19Plotter;
                        if (fluxScale == 4D)
                            plotter = font22Plotter;
                        if (fluxScale == 6D)
                            plotter = font26Plotter;
                        if (fluxScale == 8D)
                            plotter = font30Plotter;
                    }
                    if (plotter != null) {
                        String full_area_name = lableTexts[label_index];
                        int num_lines = 1;
                        for (int i_146_ = 0; i_146_ < full_area_name.length(); i_146_++)
                            if (full_area_name.charAt(i_146_) == '/') num_lines++; // '/' represents a line break.
                        string_y -= (plotter.getFontHeight() * (num_lines - 1)) / 2;
                        string_y += plotter.getDeltaHeight() / 2;
                        do {

                            int break_index = full_area_name.indexOf(DataArchive.getValue(864));
                            if (break_index == -1) {
                                plotter.drawCenterString(full_area_name, string_x, string_y, text_rgb, true);
                                break;
                            }
                            String cur_line = full_area_name.substring(0, break_index);

                            plotter.drawCenterString(cur_line, string_x, string_y, text_rgb, true);
                            string_y += plotter.getFontHeight();
                            full_area_name = full_area_name.substring(break_index + 1);
                        } while (true);
                    }
                }
            }


        }


        if (drawRegions && !drawingOnMM) {

            for (int rx = mapBaseX / 64; rx < (mapBaseX + mapWith) / 64; rx++) {
                for (int ry = mapBaseY / 64; ry < (mapBaseY + mapHeight) / 64; ry++) {
                    int map_x = rx * 64;
                    int map_y = ry * 64;
                    map_x -= mapBaseX;
                    map_y = (mapBaseY + mapHeight) - map_y;


                    int minX = destinationTopLeftX + (dest_with * (map_x - sourceTopLeftX)) / source_with;
                    int minY = destinationTopLeftY + (dest_height * (map_y - 64 - sourceTopLeftY)) / source_height;
                    int maxX = destinationTopLeftX + (dest_with * ((map_x + 64) - sourceTopLeftX)) / source_with;
                    int maxY = destinationTopLeftY + (dest_height * (map_y - sourceTopLeftY)) / source_height;


                    RenderModel.drawRectangle(minX, minY, maxX - minX, maxY - minY, Color.BLUE.getRGB());


                    letterPlotter.drawTailingString((new StringBuilder()).append(rx).append(DataArchive.getValue(866)).
                            append(ry).toString(), maxX - 5, maxY - 5, Color.BLUE.getRGB());
                    if (rx == 33 && ry >= 71 && ry <= 73)
                        letterPlotter.drawCenterString(DataArchive.getValue(868), (maxX + minX) / 2, (maxY + minY) / 2, Color.BLUE.getRGB());
                }
            }
        }

        letterPlotter.drawString("Mouse: " + mtx + "," + mty, 20, 30, Color.GREEN.getRGB());
/*
        int dx = 3211;
        int dy = 3381;
        Flooder mapper = new Flooder( map );

        int mx = 3221;
        int my = 3218;

        int dest = mtx << 15 | mty;
        int me = mx << 15 | my;


        TileNode[] cords = mapper.compute( me, dest, 0 );
        map.setBounds( 0,0, 0, Short.MAX_VALUE,Short.MAX_VALUE );
        if(cords == null || cords.length <= 1) {
            System.err.println("FUCK");
            return;

        }

        for(TileNode node : cords) {
            int map_x = node.getX() - mapBaseX;
            int map_y = (mapBaseY + mapHeight) - node.getY();


            int minX = destinationTopLeftX + (dest_with * (map_x - sourceTopLeftX)) / source_with;
            int minY = destinationTopLeftY + (dest_height * (map_y - 1 - sourceTopLeftY)) / source_height;
            int maxX = destinationTopLeftX + (dest_with * ((map_x + 1) - sourceTopLeftX)) / source_with;
            int maxY = destinationTopLeftY + (dest_height * (map_y - sourceTopLeftY)) / source_height;


            RenderModel.fillRectangle(minX, minY, maxX - minX, maxY - minY, Color.RED.getRGB());

        }*/


        if (drawHoveredTile) {

            int map_x = mtx - mapBaseX;
            int map_y = (mapBaseY + mapHeight) - mty;


            int minX = destinationTopLeftX + (dest_with * (map_x - sourceTopLeftX)) / source_with;
            int minY = destinationTopLeftY + (dest_height * (map_y - 1 - sourceTopLeftY)) / source_height;
            int maxX = destinationTopLeftX + (dest_with * ((map_x + 1) - sourceTopLeftX)) / source_with;
            int maxY = destinationTopLeftY + (dest_height * (map_y - sourceTopLeftY)) / source_height;


            RenderModel.fillRectangle(minX, minY, maxX - minX, maxY - minY, Color.ORANGE.getRGB());


        }

        for (WebVertex v : vertices) {
            if (v.getPlane() != 0)
                continue;
            int x = v.getX();
            int y = v.getY();
            x -= mapBaseX;
            y = (mapBaseY + mapHeight) - y;
            int minX = destinationTopLeftX + (dest_with * (x - sourceTopLeftX)) / source_with;
            int minY = destinationTopLeftY + (dest_height * (y - 1 - sourceTopLeftY)) / source_height;
            int maxX = destinationTopLeftX + (dest_with * ((x + 1) - sourceTopLeftX)) / source_with;
            int maxY = destinationTopLeftY + (dest_height * (y - sourceTopLeftY)) / source_height;
            Rectangle r = new Rectangle(minX, minY, maxX - minX, maxY - minY);
            RenderModel.fillRectangle(minX, minY, maxX - minX, maxY - minY, Color.GREEN.getRGB());

            for (WebVertex e : v.getEdges()) {
                int ex = e.getX() - mapBaseX;
                int ey = (mapBaseY + mapHeight) - e.getY();
                int eminX = destinationTopLeftX + (dest_with * (ex - sourceTopLeftX)) / source_with;
                int eminY = destinationTopLeftY + (dest_height * (ey - 1 - sourceTopLeftY)) / source_height;
                int emaxX = destinationTopLeftX + (dest_with * ((ex + 1) - sourceTopLeftX)) / source_with;
                int emaxY = destinationTopLeftY + (dest_height * (ey - sourceTopLeftY)) / source_height;

                Rectangle er = new Rectangle(eminX, eminY, emaxX - eminX, emaxY - eminY);

                RenderModel.drawLine(
                        (int) r.getCenterX(), (int) r.getCenterY(),
                        (int) er.getCenterX(), (int) er.getCenterY(),
                        Color.ORANGE.getRGB()
                );
            }
            letterPlotter.drawString(String.valueOf(v.getIndex()), minX, minY, Color.GREEN.getRGB());
        }

        DijkstraPathfinder pathfinder = new DijkstraPathfinder();
        WorldGraph graph = Movement.getWorldGraph();
        WebVertex[] path = pathfinder.generate(graph.getVertex(0), graph.getVertex(737));
        for (int i = 0; i < path.length; i++) {
            WebVertex v = path[i];
            if (v.getPlane() != 0)
                continue;
            int x = v.getX();
            int y = v.getY();
            x -= mapBaseX;
            y = (mapBaseY + mapHeight) - y;
            int minX = destinationTopLeftX + (dest_with * (x - sourceTopLeftX)) / source_with;
            int minY = destinationTopLeftY + (dest_height * (y - 1 - sourceTopLeftY)) / source_height;
            int maxX = destinationTopLeftX + (dest_with * ((x + 1) - sourceTopLeftX)) / source_with;
            int maxY = destinationTopLeftY + (dest_height * (y - sourceTopLeftY)) / source_height;
            Rectangle r = new Rectangle(minX, minY, maxX - minX, maxY - minY);
            RenderModel.fillRectangle(minX, minY, maxX - minX, maxY - minY, Color.PINK.getRGB());
            try {
                WebVertex e = path[i + 1];
                int ex = e.getX() - mapBaseX;
                int ey = (mapBaseY + mapHeight) - e.getY();
                int eminX = destinationTopLeftX + (dest_with * (ex - sourceTopLeftX)) / source_with;
                int eminY = destinationTopLeftY + (dest_height * (ey - 1 - sourceTopLeftY)) / source_height;
                int emaxX = destinationTopLeftX + (dest_with * ((ex + 1) - sourceTopLeftX)) / source_with;
                int emaxY = destinationTopLeftY + (dest_height * (ey - sourceTopLeftY)) / source_height;

                Rectangle er = new Rectangle(eminX, eminY, emaxX - eminX, emaxY - eminY);

                RenderModel.drawLine(
                        (int) r.getCenterX(), (int) r.getCenterY(),
                        (int) er.getCenterX(), (int) er.getCenterY(),
                        Color.BLUE.getRGB()
                );
            } catch (Exception ignore) {}
        }

        for (MapNode node : waypoints) {
            if (node.z != 0) { //TODO this is for controling what plane to view, though u wont get a sexy map, but you'll see...
                node.render = false;
                continue;
            }

            int x = node.x;
            int y = node.y;
            x -= mapBaseX;
            y = (mapBaseY + mapHeight) - y;


            int minX = destinationTopLeftX + (dest_with * (x - sourceTopLeftX)) / source_with;
            int minY = destinationTopLeftY + (dest_height * (y - 1 - sourceTopLeftY)) / source_height;
            int maxX = destinationTopLeftX + (dest_with * ((x + 1) - sourceTopLeftX)) / source_with;
            int maxY = destinationTopLeftY + (dest_height * (y - sourceTopLeftY)) / source_height;


            node.setBounds(minX, minY, maxX - minX, maxY - minY);

        }


        for (MapNode node : waypoints) {
            if (node == null) continue;
            node.render();
        }

        for (RouteNode node : routes) {
            if (node == null) continue;
            node.render();
        }


    }

    public final void drawCurve(int data[], int data_position,
                                int underlay, int overlay,
                                int with, int height,
                                int position, int orientation) {
        int jump = RenderModel.map_with - with;
        if (position == 9) {
            position = 1;
            orientation = orientation + 1 & 3;
        }
        if (position == 10) {
            position = 1;
            orientation = orientation + 3 & 3;
        }
        if (position == 11) {
            position = 8;
            orientation = orientation + 3 & 3;
        }
        if (position == 1) {
            if (orientation == 0) {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < with; x++)
                        if (x <= y) data[data_position++] = overlay;
                        else data[data_position++] = underlay;
                    data_position += jump;
                }
            } else if (orientation == 1) {
                for (int y = height - 1; y >= 0; y--) {
                    for (int x = 0; x < with; x++)
                        if (x <= y) data[data_position++] = overlay;
                        else data[data_position++] = underlay;
                    data_position += jump;
                }
            } else if (orientation == 2) {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < with; x++)
                        if (x >= y) data[data_position++] = overlay;
                        else data[data_position++] = underlay;
                    data_position += jump;
                }
            } else if (orientation == 3) {
                for (int y = height - 1; y >= 0; y--) {
                    for (int x = 0; x < with; x++)
                        if (x >= y) data[data_position++] = overlay;
                        else data[data_position++] = underlay;
                    data_position += jump;
                }
            }
        } else if (position == 2) {
            if (orientation == 0) {
                for (int i_172_ = height - 1; i_172_ >= 0; i_172_--) {
                    for (int i_173_ = 0; i_173_ < with; i_173_++)
                        if (i_173_ <= i_172_ >> 1)
                            data[data_position++] = overlay;
                        else
                            data[data_position++] = underlay;

                    data_position += jump;
                }

            } else if (orientation == 1) {
                for (int i_174_ = 0; i_174_ < height; i_174_++) {
                    for (int i_175_ = 0; i_175_ < with; i_175_++)
                        if (i_175_ >= i_174_ << 1)
                            data[data_position++] = overlay;
                        else
                            data[data_position++] = underlay;

                    data_position += jump;
                }

            } else if (orientation == 2) {
                for (int i_176_ = 0; i_176_ < height; i_176_++) {
                    for (int i_177_ = with - 1; i_177_ >= 0; i_177_--)
                        if (i_177_ <= i_176_ >> 1)
                            data[data_position++] = overlay;
                        else
                            data[data_position++] = underlay;

                    data_position += jump;
                }

            } else if (orientation == 3) {
                for (int i_178_ = height - 1; i_178_ >= 0; i_178_--) {
                    for (int i_179_ = with - 1; i_179_ >= 0; i_179_--)
                        if (i_179_ >= i_178_ << 1)
                            data[data_position++] = overlay;
                        else
                            data[data_position++] = underlay;

                    data_position += jump;
                }

            }
        } else if (position == 3) {
            if (orientation == 0) {
                for (int i_180_ = height - 1; i_180_ >= 0; i_180_--) {
                    for (int i_181_ = with - 1; i_181_ >= 0; i_181_--)
                        if (i_181_ <= i_180_ >> 1)
                            data[data_position++] = overlay;
                        else
                            data[data_position++] = underlay;

                    data_position += jump;
                }

            } else if (orientation == 1) {
                for (int i_182_ = height - 1; i_182_ >= 0; i_182_--) {
                    for (int i_183_ = 0; i_183_ < with; i_183_++)
                        if (i_183_ >= i_182_ << 1)
                            data[data_position++] = overlay;
                        else
                            data[data_position++] = underlay;

                    data_position += jump;
                }

            } else if (orientation == 2) {
                for (int i_184_ = 0; i_184_ < height; i_184_++) {
                    for (int i_185_ = 0; i_185_ < with; i_185_++)
                        if (i_185_ <= i_184_ >> 1)
                            data[data_position++] = overlay;
                        else
                            data[data_position++] = underlay;

                    data_position += jump;
                }

            } else if (orientation == 3) {
                for (int i_186_ = 0; i_186_ < height; i_186_++) {
                    for (int i_187_ = with - 1; i_187_ >= 0; i_187_--)
                        if (i_187_ >= i_186_ << 1)
                            data[data_position++] = overlay;
                        else
                            data[data_position++] = underlay;

                    data_position += jump;
                }

            }
        } else if (position == 4) {
            if (orientation == 0) {
                for (int i_188_ = height - 1; i_188_ >= 0; i_188_--) {
                    for (int i_189_ = 0; i_189_ < with; i_189_++)
                        if (i_189_ >= i_188_ >> 1)
                            data[data_position++] = overlay;
                        else
                            data[data_position++] = underlay;

                    data_position += jump;
                }

            } else if (orientation == 1) {
                for (int i_190_ = 0; i_190_ < height; i_190_++) {
                    for (int i_191_ = 0; i_191_ < with; i_191_++)
                        if (i_191_ <= i_190_ << 1)
                            data[data_position++] = overlay;
                        else
                            data[data_position++] = underlay;

                    data_position += jump;
                }

            } else if (orientation == 2) {
                for (int i_192_ = 0; i_192_ < height; i_192_++) {
                    for (int i_193_ = with - 1; i_193_ >= 0; i_193_--)
                        if (i_193_ >= i_192_ >> 1)
                            data[data_position++] = overlay;
                        else
                            data[data_position++] = underlay;

                    data_position += jump;
                }

            } else if (orientation == 3) {
                for (int i_194_ = height - 1; i_194_ >= 0; i_194_--) {
                    for (int i_195_ = with - 1; i_195_ >= 0; i_195_--)
                        if (i_195_ <= i_194_ << 1)
                            data[data_position++] = overlay;
                        else
                            data[data_position++] = underlay;

                    data_position += jump;
                }

            }
        } else if (position == 5) {
            if (orientation == 0) {
                for (int i_196_ = height - 1; i_196_ >= 0; i_196_--) {
                    for (int i_197_ = with - 1; i_197_ >= 0; i_197_--)
                        if (i_197_ >= i_196_ >> 1)
                            data[data_position++] = overlay;
                        else
                            data[data_position++] = underlay;

                    data_position += jump;
                }

            } else if (orientation == 1) {
                for (int i_198_ = height - 1; i_198_ >= 0; i_198_--) {
                    for (int i_199_ = 0; i_199_ < with; i_199_++)
                        if (i_199_ <= i_198_ << 1)
                            data[data_position++] = overlay;
                        else
                            data[data_position++] = underlay;

                    data_position += jump;
                }

            } else if (orientation == 2) {
                for (int i_200_ = 0; i_200_ < height; i_200_++) {
                    for (int i_201_ = 0; i_201_ < with; i_201_++)
                        if (i_201_ >= i_200_ >> 1)
                            data[data_position++] = overlay;
                        else
                            data[data_position++] = underlay;

                    data_position += jump;
                }

            } else if (orientation == 3) {
                for (int i_202_ = 0; i_202_ < height; i_202_++) {
                    for (int i_203_ = with - 1; i_203_ >= 0; i_203_--)
                        if (i_203_ <= i_202_ << 1)
                            data[data_position++] = overlay;
                        else
                            data[data_position++] = underlay;

                    data_position += jump;
                }

            }
        } else {
            if (position == 6) {
                if (orientation == 0) {
                    for (int i_204_ = 0; i_204_ < height; i_204_++) {
                        for (int i_205_ = 0; i_205_ < with; i_205_++)
                            if (i_205_ <= with / 2)
                                data[data_position++] = overlay;
                            else
                                data[data_position++] = underlay;

                        data_position += jump;
                    }

                    return;
                }
                if (orientation == 1) {
                    for (int i_206_ = 0; i_206_ < height; i_206_++) {
                        for (int i_207_ = 0; i_207_ < with; i_207_++)
                            if (i_206_ <= height / 2)
                                data[data_position++] = overlay;
                            else
                                data[data_position++] = underlay;

                        data_position += jump;
                    }

                    return;
                }
                if (orientation == 2) {
                    for (int i_208_ = 0; i_208_ < height; i_208_++) {
                        for (int i_209_ = 0; i_209_ < with; i_209_++)
                            if (i_209_ >= with / 2)
                                data[data_position++] = overlay;
                            else
                                data[data_position++] = underlay;

                        data_position += jump;
                    }

                    return;
                }
                if (orientation == 3) {
                    for (int i_210_ = 0; i_210_ < height; i_210_++) {
                        for (int i_211_ = 0; i_211_ < with; i_211_++)
                            if (i_210_ >= height / 2)
                                data[data_position++] = overlay;
                            else
                                data[data_position++] = underlay;

                        data_position += jump;
                    }

                    return;
                }
            }
            if (position == 7) {
                if (orientation == 0) {
                    for (int i_212_ = 0; i_212_ < height; i_212_++) {
                        for (int i_213_ = 0; i_213_ < with; i_213_++)
                            if (i_213_ <= i_212_ - height / 2)
                                data[data_position++] = overlay;
                            else
                                data[data_position++] = underlay;

                        data_position += jump;
                    }

                    return;
                }
                if (orientation == 1) {
                    for (int i_214_ = height - 1; i_214_ >= 0; i_214_--) {
                        for (int i_215_ = 0; i_215_ < with; i_215_++)
                            if (i_215_ <= i_214_ - height / 2)
                                data[data_position++] = overlay;
                            else
                                data[data_position++] = underlay;

                        data_position += jump;
                    }

                    return;
                }
                if (orientation == 2) {
                    for (int i_216_ = height - 1; i_216_ >= 0; i_216_--) {
                        for (int i_217_ = with - 1; i_217_ >= 0; i_217_--)
                            if (i_217_ <= i_216_ - height / 2)
                                data[data_position++] = overlay;
                            else
                                data[data_position++] = underlay;

                        data_position += jump;
                    }

                    return;
                }
                if (orientation == 3) {
                    for (int i_218_ = 0; i_218_ < height; i_218_++) {
                        for (int i_219_ = with - 1; i_219_ >= 0; i_219_--)
                            if (i_219_ <= i_218_ - height / 2)
                                data[data_position++] = overlay;
                            else
                                data[data_position++] = underlay;

                        data_position += jump;
                    }

                    return;
                }
            }
            if (position == 8)
                if (orientation == 0) {
                    for (int i_220_ = 0; i_220_ < height; i_220_++) {
                        for (int i_221_ = 0; i_221_ < with; i_221_++)
                            if (i_221_ >= i_220_ - height / 2)
                                data[data_position++] = overlay;
                            else
                                data[data_position++] = underlay;

                        data_position += jump;
                    }

                } else if (orientation == 1) {
                    for (int i_222_ = height - 1; i_222_ >= 0; i_222_--) {
                        for (int i_223_ = 0; i_223_ < with; i_223_++)
                            if (i_223_ >= i_222_ - height / 2)
                                data[data_position++] = overlay;
                            else
                                data[data_position++] = underlay;

                        data_position += jump;
                    }

                } else if (orientation == 2) {
                    for (int i_224_ = height - 1; i_224_ >= 0; i_224_--) {
                        for (int i_225_ = with - 1; i_225_ >= 0; i_225_--)
                            if (i_225_ >= i_224_ - height / 2)
                                data[data_position++] = overlay;
                            else
                                data[data_position++] = underlay;

                        data_position += jump;
                    }

                } else if (orientation == 3) {
                    for (int i_226_ = 0; i_226_ < height; i_226_++) {
                        for (int i_227_ = with - 1; i_227_ >= 0; i_227_--)
                            if (i_227_ >= i_226_ - height / 2)
                                data[data_position++] = overlay;
                            else
                                data[data_position++] = underlay;

                        data_position += jump;
                    }

                }
        }
    }

    public final MapDataStore getMapData() {
        String string = null;
        MapDataStore store;
        //openStream();
        try {
            string = getDataFilePath();
            System.out.println(string);
            byte is[] = FileReader.readFile(string + DataArchive.getValue(ArchiveKeys.MAP_DATA_FILE_PATH));

            store = new MapDataStore(is);
        } catch (Throwable throwable) {
            byte is[] = openStream();
            if (string != null && is != null)
                try {
                    printStackTrace((new StringBuilder()).append(string).append(DataArchive.getValue(1304)).toString(), is);
                } catch (Throwable throwable_228_) {
                    throwable = throwable_228_;
                }
            return new MapDataStore(is);
        }
        return store;
    }

    public final byte[] openStream() {
        renderLoadingBar(0, DataArchive.getValue(1318));
        byte is[];
        try {
            String string = "";

            for (int i = 0; i < 10; i++)
                string = (new StringBuilder()).append(string).append(Class3.anIntArray5[i]).toString();

            HttpURLConnection conn;
            conn = (HttpURLConnection) new URL("http://inubot.com/worldmap.dat").openConnection();
            DataInputStream datainputstream = new DataInputStream(conn.getInputStream());

            int num_bytes = conn.getContentLength();

            System.out.println(num_bytes);

            System.out.println(DataArchive.getValue(1333) + string + DataArchive.getValue(1251));
            int i = 0;
            int offset = 0;
            byte dest[] = new byte[num_bytes];
            while (offset < num_bytes) {
                int length = num_bytes - offset;
                if (length > 1024)
                    length = 1024;
                int bytes_read = datainputstream.read(dest, offset, length);
                if (bytes_read < 0)
                    throw new IOException(DataArchive.getValue(1342));
                offset += bytes_read;
                int complete = (offset * 100) / num_bytes;
                if (complete != i)
                    renderLoadingBar(complete, (new StringBuilder()).append(DataArchive.getValue(1354)).
                            append(complete).append(DataArchive.getValue(1369)).toString());
                i = complete;
            }
            datainputstream.close();
            is = dest;
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
            return null;
        }
        return is;
    }

    public final String getDataFilePath() {
        String[] drive_paths = {
                DataArchive.getValue(1398),
                DataArchive.getValue(1410),
                DataArchive.getValue(1420),
                DataArchive.getValue(1432),
                DataArchive.getValue(1442),
                DataArchive.getValue(1454),
                DataArchive.getValue(1464),
                DataArchive.getValue(1476),
                DataArchive.getValue(1486),
                DataArchive.getValue(1490),
                DataArchive.getValue(1493)
        };
        String path;
        String file_store = DataArchive.getValue(1499);

        for (String drive_path : drive_paths) {
            try {
                if (drive_path.length() > 0) {
                    File file = new File(drive_path);
                    if (!file.exists()) continue;
                }
                File file = new File(drive_path + file_store);
                if ((!file.exists()) && (!file.mkdir())) continue;
                path = drive_path + file_store + DataArchive.getValue(864);
            } catch (Exception exception) {
                continue;
            }
            return path;
        }
        return null;
    }

    public final void printStackTrace(String string, byte is[]) {
        try {
            FileOutputStream fileoutputstream = new FileOutputStream(string);
            fileoutputstream.write(is, 0, is.length);
            fileoutputstream.close();
        } catch (Exception exception) {
        }
    }

}
