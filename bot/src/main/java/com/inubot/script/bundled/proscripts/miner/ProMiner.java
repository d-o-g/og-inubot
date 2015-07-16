/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.proscripts.miner;

import com.inubot.api.methods.*;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.methods.traversal.Path;
import com.inubot.api.methods.traversal.graph.WebPath;
import com.inubot.api.oldschool.*;
import com.inubot.api.util.*;
import com.inubot.api.util.filter.Filter;
import com.inubot.client.natives.oldschool.RSObjectDefinition;
import com.inubot.script.bundled.proscripts.framework.ProScript;

import java.awt.*;
import java.util.Map;

/**
 * @author Dogerina
 * @since 15-07-2015
 */
public class ProMiner extends ProScript implements MinerConstants {

    private final MinerController controller;
    private final StopWatch stopWatch;
    private GameObject rock;

    public ProMiner() {
        this.controller = new MinerController(new MinerView(), new MinerModel());
        this.stopWatch = new StopWatch(0);
    }

    @Override
    public boolean setup() {
        controller.getView().display();
        while (true) {
            Time.sleep(800);
            if (controller.getView().isDisposable()) {
                break;
            }
        }
        controller.getView().dispose(); //maybe just hide instead of dispose? might want to change while running script..
        return true;
    }

    @Override
    public void getPaintData(Map<String, Object> data) {
        data.put(RUNTIME_KEY, stopWatch.toElapsedString());
        int exp = Skills.getExperience(Skill.MINING) - controller.getModel().getStartExperience();
        data.put(EXP_KEY, exp);
        data.put(EXP_PH_KEY, stopWatch.getHourlyRate(exp));
        data.put(MINED_KEY, controller.getModel().getOreMined());
        data.put(MINED_PH_KEY, stopWatch.getHourlyRate(controller.getModel().getOreMined()));
    }

    @Override
    public int loop() {
        if (Game.isLoggedIn()) {

            if (controller.getModel().getStartExperience() == -1) { //loop because user might not start logged in
                controller.getModel().setStartExperience(Skills.getExperience(Skill.MINING));
            }

            if (Inventory.isFull()) {
                if (!controller.getModel().isBanking()) {
                    Inventory.dropAll(item -> !item.getName().contains("pickaxe"));
                } else {
                    if (!Bank.isOpen()) {
                        Tile bank = Movement.getWeb().getNearestBank().getLocation();
                        if (bank.distance() > 10) {
                            WebPath path = Movement.getWeb().findPathToNearestBank();
                            path.step(Path.Option.TOGGLE_RUN);
                        } else {
                            Bank.open(); //TODO make this support deposit boxes
                        }
                    } else {
                        Bank.depositAllExcept(item -> item.getName().contains("pickaxe"));
                    }
                }
            }

            if (rock != null) {
                rock = GameObjects.getNearest(t -> t.getLocation().equals(rock.getLocation()));
            }

            if (Players.getLocal().getAnimation() == -1 || rock != null && !isGoodRock(rock)) {
                rock = GameObjects.getNearest(go -> go.distance() <= 2 && isGoodRock(go));
                if (rock != null)
                    rock.processAction("Mine");
                return 1500;
            }
        }
        return 1000;
    }

    private boolean isGoodRock(GameObject obj) {
        for (Rock rock : controller.getModel().getSelectedRocks()) {
            if (rock.accept(obj))
                return true;
        }
        return false;
    }

    enum Location {

        RIMMINGTON(new Tile(2976, 3240)),
        LUMBY_SWAMP(new Tile(3241, 3166)),
        FALLY_WEST(new Tile(2909, 3358)),
        VARROCK_EAST(new Tile(3288, 3369)),
        VARROCK_WEST(new Tile(3178, 3368)),
        BARBARIAN_VILLAGE(new Tile(3081, 3421)),
        ARDOUGNE_EAST(new Tile(2699, 3331)),
        AL_KHARID(new Tile(3298, 3296));

        private final Tile location;

        Location(Tile location) {
            this.location = location;
        }

        public Tile getLocation() {
            return location;
        }
    }

    enum Rock implements Filter<GameObject> {

        CLAY(434, new Color(11898445)),
        COPPER(436, new Color(8145959)),
        TIN(438, new Color(8549491)),
        IRON(440, new Color(3808274)),
        SILVER(442, new Color(11050111)),
        COAL(453, new Color(2500117)),
        GOLD(444, new Color(16299531)),
        MITHRIL(447, new Color(3487327)),
        ADAMANTITE(449, new Color(3690553)),
        MINED(-1, null);

        private static final int[] raw = createPallet(0.9, 0, 512);
        private final int itemId;
        private final Color veinColor;

        Rock(int itemId, Color veinColor) {
            this.itemId = itemId;
            this.veinColor = veinColor;
        }

        private static double distance(Color a, Color b) {
            int dr = a.getRed() - b.getRed();
            int dg = a.getGreen() - b.getGreen();
            int db = a.getBlue() - b.getBlue();
            return Math.sqrt(dr * dr + dg * dg + db * db);
        }

        private static double distance(int id, Color veinColor) {
            if (id < 0)
                id += 65536;
            int rgb = Rock.raw[id];
            Color color = new Color(rgb);
            return distance(veinColor, color);
        }

        private static Rock identify(final int objectID) {
            RSObjectDefinition def = CacheLoader.findObjectDefinition(objectID);
            if (def == null || !def.getName().equals("Rocks"))
                return null;
            short[] newColors = def.getNewColors();
            if (newColors == null)
                return Rock.MINED;
            if (newColors.length > 2 || newColors.length == 0)
                return null;
            if (newColors.length == 2 && newColors[0] == newColors[1])
                return Rock.MINED;
            boolean oldRock = newColors.length == 1;
            int color = oldRock ? newColors[0] : newColors[1];
            Rock best = null;
            double dist = Double.MAX_VALUE;
            for (Rock rock : Rock.values()) {
                if (rock == Rock.MINED) continue;
                double distance = distance(color, rock.veinColor);
                if (distance < dist) {
                    dist = distance;
                    best = rock;
                }
            }
            return best;
        }

        private static int[] createPallet(double brightness, int from, int to) {
            int[] pallet = new int[(to - from) * 128];
            int var4 = from * 128;
            for (int var5 = from; var5 < to; ++var5) {
                double var6 = (double) (var5 >> 3) / 64.0D + 0.0078125D;
                double var8 = (double) (var5 & 7) / 8.0D + 0.0625D;
                for (int var10 = 0; var10 < 128; ++var10) {
                    double var11 = (double) var10 / 128.0D;
                    double var13 = var11;
                    double var15 = var11;
                    double var17 = var11;
                    if (var8 != 0.0D) {
                        double var19;
                        if (var11 < 0.5D) {
                            var19 = var11 * (1.0D + var8);
                        } else {
                            var19 = var11 + var8 - var11 * var8;
                        }
                        double var21 = 2.0D * var11 - var19;
                        double var23 = var6 + 0.3333333333333333D;
                        if (var23 > 1.0D) {
                            --var23;
                        }
                        double var27 = var6 - 0.3333333333333333D;
                        if (var27 < 0.0D) {
                            ++var27;
                        }
                        if (6.0D * var23 < 1.0D) {
                            var13 = var21 + (var19 - var21) * 6.0D * var23;
                        } else if (2.0D * var23 < 1.0D) {
                            var13 = var19;
                        } else if (3.0D * var23 < 2.0D) {
                            var13 = var21 + (var19 - var21) * (0.6666666666666666D - var23) * 6.0D;
                        } else {
                            var13 = var21;
                        }
                        if (6.0D * var6 < 1.0D) {
                            var15 = var21 + (var19 - var21) * 6.0D * var6;
                        } else if (2.0D * var6 < 1.0D) {
                            var15 = var19;
                        } else if (3.0D * var6 < 2.0D) {
                            var15 = var21 + (var19 - var21) * (0.6666666666666666D - var6) * 6.0D;
                        } else {
                            var15 = var21;
                        }
                        if (6.0D * var27 < 1.0D) {
                            var17 = var21 + (var19 - var21) * 6.0D * var27;
                        } else if (2.0D * var27 < 1.0D) {
                            var17 = var19;
                        } else if (3.0D * var27 < 2.0D) {
                            var17 = var21 + (var19 - var21) * (0.6666666666666666D - var27) * 6.0D;
                        } else {
                            var17 = var21;
                        }
                    }
                    int var30 = (int) (var13 * 256.0D);
                    int var20 = (int) (var15 * 256.0D);
                    int var29 = (int) (var17 * 256.0D);
                    int var22 = (var30 << 16) + (var20 << 8) + var29;
                    var22 = adjust(var22, brightness);
                    if (var22 == 0) {
                        var22 = 1;
                    }
                    pallet[var4++] = var22;
                }
            }
            return pallet;
        }

        private static int adjust(int var0, double var1) {
            double var3 = (double) (var0 >> 16) / 256.0D;
            double var5 = (double) (var0 >> 8 & 255) / 256.0D;
            double var7 = (double) (var0 & 255) / 256.0D;
            var3 = Math.pow(var3, var1);
            var5 = Math.pow(var5, var1);
            var7 = Math.pow(var7, var1);
            int var9 = (int) (var3 * 256.0D);
            int var10 = (int) (var5 * 256.0D);
            int var11 = (int) (var7 * 256.0D);
            return (var9 << 16) + (var10 << 8) + var11;
        }

        @Override
        public boolean accept(GameObject o) {
            return identify(o.getId()) == this;
        }
    }
}
