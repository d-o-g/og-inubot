package com.inubot.bundledscripts.complete;

import com.inubot.api.methods.*;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.*;
import com.inubot.api.oldschool.action.ActionOpcodes;
import com.inubot.api.oldschool.action.tree.Action;
import com.inubot.api.util.Time;
import com.inubot.api.util.filter.Filter;
import com.inubot.bundledscripts.proframework.ProScript;
import com.inubot.script.Manifest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

@Manifest(name = "AutoWaterOrbsPRO", version = 0.1, developer = "Dogerina, rapping name SasukeFan203", desc = "req: antipoison fally tabs lobsters 80+ agil")
public final class WaterOrbs extends ProScript {

    //LOL IM NOT GONNA CLEAN THIS UP UNTIL WE HAVE A WEB FUCK THAT

    private static final Tile NEAR_OBELISK = new Tile(2841, 3425);
    private static final Tile ORBS_LADDER = new Tile(2841, 9824);
    private static final Tile BANK = new Tile(2946, 3368);
    private static final Tile OUT_CAVE = new Tile(2885, 3396);
    private static final Area CAVE_START = new Area(new Tile(2880, 9793), new Tile(2888, 9814));
    private static final Tile UNLOADED_ENTRY = new Tile(2934, 3355), LOADED_ENTRY = new Tile(2920, 3365);
    private static final String[] ITEMS = {"Unpowered orb", "Cosmic rune", "Falador teleport", "Lobster"};
    private final Filter<Item> ANTIPOT_PREDICATE = item -> item.getName().contains("Antipoison");
    private int orbsMade = 0;
    private int orbPrice = 0, cosmicPrice = 0, unpoweredOrbPrice = 0;

    @Override
    public boolean setup() {
        cosmicPrice = getPrice("Cosmic_rune", 564);
        unpoweredOrbPrice = getPrice("Unpowered_orb", 567);
        orbPrice = getPrice("Water_orb", 571);
        return true;
    }

    @Override
    public int loop() {
        if (!Movement.isRunEnabled() && (Movement.getRunEnergy() > 50 || Players.getLocal().isHealthBarVisible())) {
            Movement.toggleRun(true);
        }
        if (Skills.getCurrentLevel(Skill.HITPOINTS) < 25) {
            Item food = Inventory.getFirst("Lobster");
            if (food != null) {
                food.processFirst();
            }
        }
        if (Combat.isPoisoned()) {
            Item antipot = Inventory.getFirst(ANTIPOT_PREDICATE);
            if (antipot != null) {
                antipot.processFirst();
            }
        }

        if (GameObjects.getNearest("Obelisk of Water") != null) {
            if (!Inventory.contains("Unpowered orb")) {
                System.out.println("Tele 1");
                Item tele = Inventory.getFirst("Falador teleport");
                if (tele != null) {
                    tele.processFirst();
                }
            } else if (!Inventory.contains("Lobster") && NEAR_OBELISK.distance() < 5) {
                penis();
                return 100;
            }
        }
        if (!isReady()) {
            if (Bank.isOpen()) {
                withdrawSetup();
            } else if (BANK.distance() < 35) {
                Item food = Inventory.getFirst("Lobster");
                if (food != null) {
                    food.processFirst();
                }
                GameObjects.getNearest("Bank booth").processAction("Bank");
            } else if (Skills.getCurrentLevel(Skill.HITPOINTS) < 25) {
                System.out.println("Tele 2");
                Item tele = Inventory.getFirst("Falador teleport");
                if (tele != null) {
                    tele.processFirst();
                }
            }
        } else if (Movement.isReachable(BANK)) {
            if (Bank.isOpen()) {
                Bank.close();
                return 420;
            }
            GameObject wall = GameObjects.getNearest("Crumbling wall");
            if (wall != null && wall.processAction("Climb-over")) {
                Time.await(() -> !Players.getLocal().isMoving(), 12000);
            }
        } else if (Players.getLocal().getLocation().equals(UNLOADED_ENTRY)) {
            Movement.walkTo(LOADED_ENTRY);
            Time.await(() -> LOADED_ENTRY.distance() < 3, 10000);
        } else if (BANK.distance() < 20) {
            Movement.walkTo(OUT_CAVE);
        } else if (OUT_CAVE.distance() < 25) {
            GameObject down = GameObjects.getNearest("Ladder");
            if (down != null && down.processAction("Climb-down")) {
                return 1500;
            }
        } else if (CAVE_START.contains(Players.getLocal())) {
            GameObject floor = GameObjects.getNearest("Strange floor");
            if (floor != null && floor.processAction("Jump-over")) {
                Time.await(() -> !Players.getLocal().isMoving(), 8000);
            }
        } else if (GameObjects.getNearest("Obelisk of Water") == null) {
            if (ORBS_LADDER.distance() > 5) {
                Movement.walkTo(ORBS_LADDER);
            } else {
                GameObject ladder = GameObjects.getNearest("Ladder");
                if (ladder != null && ladder.processAction("Climb-up")) {
                    return 1500;
                }
            }
        } else {
            penis();
            return 100;
        }

        return 600;
    }

    private void penis() {
        GameObject obelisk = GameObjects.getNearest("Obelisk of Water");
        if (obelisk != null) {
            InterfaceComponent iface = Interfaces.get(309, 6);
            if (iface != null && iface.isVisible() && !iface.isHidden()) {
                int xp = Skills.getExperience(Skill.MAGIC);
                Client.processAction(Action.valueOf(ActionOpcodes.BUTTON_DIALOG, 0, -1, 20250630), "Make 1", "");
                Time.await(() -> Skills.getExperience(Skill.MAGIC) != xp, 3000);
                //if (iface.processAction("Make All")) {
                  //  Time.sleep(3000);
                //}
            } else {
                Magic.select(Spell.Modern.CHARGE_WATER_ORB);
                Magic.cast(Spell.Modern.CHARGE_WATER_ORB, obelisk);
                if (Inventory.getCount("Unpowered orb") == 1) {
                    Time.await(() -> Inventory.getCount("Unpowered orb") == 0, 3000);
                } else {
                    Time.await(() -> Interfaces.get(309, 6) != null, 800);
                }
            }
        }
    }

    private boolean isReady() {
        for (String item : ITEMS) {
            if (!Inventory.contains(item)) {
                return false;
            }
        }
        return Inventory.getFirst(ANTIPOT_PREDICATE) != null || Inventory.contains("Vial");
    }

    private void withdrawSetup() {
        Time.sleep(1000);
        Bank.depositInventory();
        if (Inventory.getCount() != 0) {
            return;
        }
        if (Skills.getCurrentLevel(Skill.HITPOINTS) < 25) {
            Item food;
            if (Inventory.contains("Lobster") && Bank.close()) {
                food = Inventory.getFirst("Lobster");
                if (food != null) {
                    food.processFirst();
                }
            } else {
                food = Bank.getFirst("Lobster");
                if (food != null) {
                    food.processFirst();
                }
            }
            return;
        }
        Item tabs = Bank.getFirst("Falador teleport");
        if (tabs != null && !Inventory.contains("Falador teleport")) {
            if (tabs.processFirst()) {
                Time.await(() -> Inventory.contains("Falador teleport"), 5000);
            }
        }
        Item cosmics = Bank.getFirst("Cosmic rune");
        if (cosmics != null && !Inventory.contains("Cosmic rune")) {
            if (cosmics.processAction("Withdraw-75")) {
                Time.await(() -> Inventory.contains("Cosmic rune"), 5000);
            }
        }
        Item food = Bank.getFirst("Lobster");
        if (food != null && !Inventory.contains("Lobster")) {
            if (food.processFirst()) {
                Time.await(() -> Inventory.contains("Lobster"), 5000);
            }
        }
        Item pot = Bank.getFirst(ANTIPOT_PREDICATE);
        if (pot != null && !Inventory.contains(ANTIPOT_PREDICATE)) {
            if (pot.processFirst()) {
                Time.await(() -> Inventory.contains(ANTIPOT_PREDICATE), 5000);
            }
        }
        Item orbs = Bank.getFirst("Unpowered orb");
        if (orbs != null && !Inventory.contains("Unpowered orb")) {
            if (orbs.processAction("Withdraw-All")) {
                Time.await(() -> Inventory.contains("Unpowered orb"), 5000);
            }
        }
        Time.sleep(600);
    }

    public int getPrice(String name, int id) { //Cosmic_rune, 564
        try {
            final URL url = new URL("http://services.runescape.com/m=itemdb_oldschool/" + name + "/viewitem?obj=" + id);
            final InputStreamReader isr = new InputStreamReader(url.openStream());
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("<h3>Current Guide Price <span title='")) {
                    String[] parts = line.split("'");
                    return Integer.parseInt(parts[1].replaceAll(",", ""));
                }
            }
            br.close();
            isr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void getPaintData(Map<String, Object> data) {
        int xp = getTrackedSkill(Skill.MAGIC).getGainedExperience();
        if (xp > 0) {
            data.put("Orbs", xp / 66);
            data.put("Orbs/hr", stopWatch.getHourlyRate(xp / 66));
            int profit = orbsMade * (orbPrice - (cosmicPrice * 3) - unpoweredOrbPrice);
            data.put("Profit", profit);
            data.put("Profit/hr", stopWatch.getHourlyRate(profit));
        }
    }
}