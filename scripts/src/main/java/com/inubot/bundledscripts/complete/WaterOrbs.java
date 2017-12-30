package com.inubot.bundledscripts.complete;

import com.inubot.api.methods.*;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.*;
import com.inubot.api.oldschool.action.ActionOpcodes;
import com.inubot.api.oldschool.action.tree.Action;
import com.inubot.api.oldschool.event.ExperienceEvent;
import com.inubot.api.util.Time;
import com.inubot.api.util.filter.Filter;
import com.inubot.bundledscripts.proframework.ProScript;
import com.inubot.script.Manifest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

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
        final RSExchange rsExchange = new RSExchange();
        cosmicPrice = rsExchange.getExchangeItem("Cosmic rune").map(ExchangeItem::getBuyAverage).orElse(0);
        unpoweredOrbPrice = rsExchange.getExchangeItem("Unpowered orb").map(ExchangeItem::getBuyAverage).orElse(0);
        orbPrice = rsExchange.getExchangeItem("Water orb").map(ExchangeItem::getBuyAverage).orElse(0);
        return true;
    }

    @Override
    public void experienceChanged(ExperienceEvent e) {
        super.experienceChanged(e);
        if (e.getSkill() == Skill.MAGIC) {
            if (e.getGain() == 66)
                orbsMade++;
        }
    }

    @Override
    public int loop() {
        if (!Movement.isRunEnabled() && (Movement.getRunEnergy() > 50 || Players.getLocal().isHealthBarVisible())) {
            Movement.toggleRun(true);
        }
        if (Skills.getCurrentLevel(Skill.HITPOINTS) < 25 && Inventory.contains("Lobster")) {
            if (Bank.isOpen()) {
                Bank.close();
            } else {
                Item food = Inventory.getFirst("Lobster");
                if (food != null) {
                    food.processFirst();
                }
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

    private boolean meme() {
        InterfaceComponent component = Interfaces.get(270, 14, 38);
        return component != null && component.isVisible() && !component.isHidden();
    }

    private void penis() {
        GameObject obelisk = GameObjects.getNearest("Obelisk of Water");
        if (obelisk != null) {
            if (meme()) {
                int xp = Skills.getExperience(Skill.MAGIC);
                Client.processAction(Action.valueOf(ActionOpcodes.COMPONENT_ACTION, 1, -1, 17694734), "Charge", "");
                Time.await(() -> Skills.getExperience(Skill.MAGIC) != xp, 1000);
            } else {
                Magic.select(Spell.Modern.CHARGE_WATER_ORB);
                Magic.cast(Spell.Modern.CHARGE_WATER_ORB, obelisk);
                if (Inventory.getCount("Unpowered orb") == 1) {
                    Time.await(() -> Inventory.getCount("Unpowered orb") == 0, 3000);
                } else {
                    Time.await(this::meme, ThreadLocalRandom.current().nextInt(600, 800));
                }
            }
        }
    }

    private boolean isReady() {
        for (String item : ITEMS) {
            if (!Inventory.contains(item)) {
                System.out.println("Failed item " + item);
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
        } else {
            if (tabs == null)
                stop();
        }
        Item cosmics = Bank.getFirst("Cosmic rune");
        if (cosmics != null && !Inventory.contains("Cosmic rune")) {
            if (cosmics.processAction("Withdraw-75")) {
                Time.await(() -> Inventory.contains("Cosmic rune"), 5000);
            }
        } else {
            if (cosmics == null || Bank.getCount(item -> item.getName().equals("Cosmic rune")) < 75)
                stop();
        }
        Item food = Bank.getFirst("Lobster");
        if (food != null && !Inventory.contains("Lobster")) {
            if (food.processFirst()) {
                Time.await(() -> Inventory.contains("Lobster"), 5000);
            }
        } else {
            if (food == null)
                stop();
        }
        Item pot = Bank.getFirst(ANTIPOT_PREDICATE);
        if (pot != null && !Inventory.contains(ANTIPOT_PREDICATE)) {
            if (pot.processFirst()) {
                Time.await(() -> Inventory.contains(ANTIPOT_PREDICATE), 5000);
            }
        } else {
            if (pot == null)
                stop();
        }
        Item orbs = Bank.getFirst("Unpowered orb");
        if (orbs != null && !Inventory.contains("Unpowered orb")) {
            if (orbs.processAction("Withdraw-All")) {
                Time.await(() -> Inventory.contains("Unpowered orb"), 5000);
            }
        } else {
            if (orbs == null)
                stop();
        }
        Time.sleep(600);
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