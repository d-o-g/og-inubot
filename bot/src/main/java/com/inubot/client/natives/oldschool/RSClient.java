package com.inubot.client.natives.oldschool;

import com.inubot.api.oldschool.NodeTable;
import com.inubot.client.Artificial;
import com.inubot.client.natives.ClientNative;

import java.applet.Applet;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public interface RSClient extends ClientNative {

    int getCameraX();

    int getCameraY();

    int getCameraZ();

    int getCameraYaw();

    int getCameraPitch();

    int getMapScale();

    int getMapRotation();

    void setMapRotation(int newRotation);

    boolean isViewportWalking();

    void setViewportWalking(boolean viewportWalking);

    int getMapOffset();

    int[][][] getTileHeights();

    byte[][][] getRenderRules();

    int getGameState();

    int getBaseX();

    int getBaseY();

    int getPlane();

    int[] getTempVarps();

    int[] getVarps();

    int getMouseIdleTime();

    int[] getNpcIndices();

    RSNodeDeque[][][] getGroundItems();

    RSNpc[] getNpcs();

    RSPlayer[] getPlayers();

    RSPlayer getPlayer();

    RSCollisionMap[] getCollisionMaps();

    RSInterfaceComponent[][] getInterfaceComponents();

    RSRegion getRegion();

    int[] getInterfacePositionsX();

    int[] getInterfacePositionsY();

    int[] getInterfaceHeights();

    int[] getInterfaceWidths();

    int[] getExperiences();

    int[] getLevels();  //current levels (can boost/drain)

    int[] getRealLevels(); //'base' levels

    int getWeight();

    int getEnergy();

    String[] getPlayerActions(); //default player actions for the current region - max array size = 8

    RSNodeTable getItemTables();

    RSNodeTable getInterfaceConfigs();

    boolean isLowMemory();

    @Artificial
    void setLowMemory(boolean lowMemory);

    RSCache getVarpBitCache();

    RSReferenceTable getVarpBitTable();

    int getEngineCycle();

    int getScreenWidth();

    int getScreenHeight();

    int getScreenState();

    //int getScreenZoom();

    int getCurrentWorld();

    //boolean isMembersWorld();

    RSGrandExchangeOffer[] getGrandExchangeOffers();

    int getMenuX();

    int getMenuY();

    int getMenuWidth();

    int getMenuHeight();

    RSFont getFont_p12full();

    @Artificial
    void drawRectangle(int x, int y, int width, int height, int color);

    @Artificial
    void resetMouseIdleTime();

    int getHintArrowX();

    int getHintArrowY();

    int getHintArrowType();

    int getHoveredRegionTileX();

    @Artificial
    void setHoveredRegionTileX(int newRegionX);

    int getHoveredRegionTileY();

    @Artificial
    void setHoveredRegionTileY(int newRegionY);

    int[] getMenuOpcodes();

    int[] getMenuArg0();

    int[] getMenuArg1();

    int[] getMenuArg2();

    String getUsername();

    @Artificial
    void setUsername(String user);

    RSEntityMarker[] getObjects();

    String getPassword();

    @Artificial
    void setPassword(String pass);

    int getLoginState();

    int getHintArrowNpcIndex();

    int getHintArrowPlayerIndex();

    @Artificial
    default RSVarpBit getVarpBit(int id) {
        RSCache cache = getVarpBitCache();
        if (cache != null && cache.getTable() != null) {
            RSVarpBit vb = (RSVarpBit) new NodeTable(cache.getTable()).lookup((long) id);
            if (vb != null) {
                return vb;
            }
        }
        RSReferenceTable table = getVarpBitTable();
        if (table != null) {
            byte[] data = table.unpack(14, id);
            if (data != null) {
                ByteBuffer buffer = ByteBuffer.wrap(data);
                int valid = buffer.get() & 0xff;
                if (valid == 1) {
                    int idx = buffer.getShort() & 0xff;
                    int min = buffer.get() & 0xff;
                    int max = buffer.get() & 0xff;
                    return new RSVarpBit() {
                        @Override
                        public int getVarpIndex() {
                            return idx;
                        }

                        @Override
                        public int getLeft() {
                            return min;
                        }

                        @Override
                        public int getRight() {
                            return max;
                        }
                    };
                }
            }
        }
        return null;
    }

    @Artificial
    RSObjectDefinition loadObjectDefinition(int id);

    @Artificial
    RSItemDefinition loadItemDefinition(int id);

    @Artificial
    RSNpcDefinition loadNpcDefinition(int id);

    @Artificial
    RSSprite loadItemSprite(int itemId, int quantity, int borderThickness, int var3, int var4, boolean noted);

    @Artificial
    void processAction(int arg1, int arg2, int op, int arg0, String action, String target, int x, int y);

    @Artificial
    default Applet asApplet() {
        return (Applet) this;
    }

    @Artificial
    default RSInterface[] getInterfaces() {
        RSInterfaceComponent[][] raw = getInterfaceComponents();
        if (raw == null || raw.length == 0)
            return new RSInterface[0];
        List<RSInterface> containers = new ArrayList<>();
        for (int i = 0; i < raw.length; i++)
            containers.add(createInterface(i, raw[i]));
        return containers.toArray(new RSInterface[containers.size()]);
    }

    @Artificial
    default RSInterface createInterface(int index, RSInterfaceComponent[] components) {
        return new RSInterface() {
            @Override
            public int getIndex() {
                return index;
            }

            @Override
            public RSInterfaceComponent[] getComponents() {
                return components == null ? new RSInterfaceComponent[0] : components;
            }
        };
    }

    RSSocket getSocket();

    boolean isSpellSelected();
}
