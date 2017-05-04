package com.inubot.client.natives.oldschool;

import com.inubot.client.Artificial;
import com.inubot.client.natives.ClientNative;

import java.applet.Applet;
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

    boolean isViewportWalking();

    void setViewportWalking(boolean viewportWalking);

    void setMapRotation(int newRotation);

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

    RSWidget[][] getWidgets();

    RSRegion getRegion();

    int[] getWidgetPositionsX();

    int[] getWidgetPositionsY();

    int[] getWidgetHeights();

    int[] getWidgetWidths();

    int[] getExperiences();

    int[] getLevels();  //current levels (can boost/drain)

    int[] getRealLevels(); //'base' levels

    int getWeight();

    int getEnergy();

    String[] getPlayerActions(); //default player actions for the current region - max array size = 8

    RSNodeTable getItemTables();

    boolean isLowMemory();

    @Artificial
    void setLowMemory(boolean lowMemory);

    int getEngineCycle();

    int getScreenWidth();

    int getScreenHeight();

    int getScreenState();

    int getScreenZoom();

    int getCurrentWorld();

    boolean isMembersWorld();

    RSGrandExchangeOffer[] getGrandExchangeOffers();

    int getMenuX();

    int getMenuY();

    int getMenuWidth();

    int getMenuHeight();

    int[] getOnCursorUids();

    int getOnCursorCount();

    void setOnCursorCount(int onCursorCount);

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

    RSInteractableEntity[] getObjects();

    String getPassword();

    @Artificial
    void setPassword(String pass);

    int getLoginState();

    int getHintArrowNpcIndex();

    int getHintArrowPlayerIndex();

    @Artificial
    RSVarpBit getVarpBit(int id);

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
        RSWidget[][] raw = getWidgets();
        if (raw == null || raw.length == 0)
            return new RSInterface[0];
        List<RSInterface> containers = new ArrayList<>();
        for (int i = 0; i < raw.length; i++)
            containers.add(createInterface(i, raw[i]));
        return containers.toArray(new RSInterface[containers.size()]);
    }

    @Artificial
    default RSInterface createInterface(int index, RSWidget[] widgets) {
        return new RSInterface() {
            @Override
            public int getIndex() {
                return index;
            }

            @Override
            public RSWidget[] getWidgets() {
                return widgets == null ? new RSWidget[0] : widgets;
            }
        };
    }
}
