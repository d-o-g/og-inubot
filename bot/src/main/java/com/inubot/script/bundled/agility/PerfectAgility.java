/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.agility;

import com.inubot.Inubot;
import com.inubot.api.methods.*;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.*;
import com.inubot.api.oldschool.action.tree.InputButtonAction;
import com.inubot.api.oldschool.event.MessageEvent;
import com.inubot.api.util.*;
import com.inubot.api.util.filter.Filter;
import com.inubot.script.Script;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author unsigned
 * @since 26-04-2015
 */
public class PerfectAgility extends Script implements Paintable {

    private static final Filter<Widget> DIALOGUE_FILTER = w -> w.getText() != null && (w.getText().equals("Click here to continue") || w.getText().equals("Sure, I'll give it a go."));
    private static final Filter<Widget> LOBBY_FILTER = w -> w.getText() != null && w.getText().equals("Play RuneScape");
    private static final Tile ARDY_STUCK = new Tile(2654, 3299, 3);
    private final StopWatch runtime = new StopWatch(0);
    private final StopWatch stucktime = new StopWatch(0);
    private Course course = null;
    private int startingAgilityExperience;
    private int stuck = 0;
    private boolean loggedInLast = false;

    public void useCourse(Course course) {
        this.course = course;
    }

    @Override
    public boolean setup() {
        startingAgilityExperience = Skills.getExperience(Skill.AGILITY);
        //JFrame frame = new JFrame();
        //frame.setLayout(new FlowLayout());
        //JComboBox<Course> courses = new JComboBox<>(Course.values());
        //frame.add(courses);
        //JButton start = new JButton("Start");
        //start.addActionListener(e -> {
//            frame.dispose();
//            course = (Course) courses.getSelectedItem();
//        });
//        frame.add(start);
//        frame.pack();
//        frame.setVisible(true);
        //while (course == null)
//            Time.sleep(400);
        return true;
    }

    @Override
    public int loop() {
        if (!Game.isLoggedIn()) {
            loggedInLast = false;
            return 100;
        }

        if (Players.getLocal().getAnimation() != -1)
            return 100;

        if (!loggedInLast) { // if the bot has just logged in, or has just been ran
            for (Course cours : Course.values()) {
                for (Obstacle obs : cours.getObstacles()) {
                    if (obs.getLocation().contains(Players.getLocal().getLocation())) {
                        this.course = cours;
                        System.out.println("Using course: " + course.toString());
                    }
                }
            }
        }

        loggedInLast = true;

        if (this.course == null) {
            return 100;
        }

        if (stucktime.getElapsed() > 12000) {
            stucktime.reset();
            if (stuck > 4 && Players.getLocal().getLocation().equals(ARDY_STUCK)) {
                Movement.walkTo(new Tile(2656, 3296, 3));
                Time.sleep(3000);
            }
            stuck = 0;
        }
        if (!Movement.isRunEnabled() && Movement.getRunEnergy() > 10) {
            Mouse.move(578, 138);
            Mouse.click(true);
        }
        if (Skills.getCurrentLevel(Skill.HITPOINTS) < 10)
            return 5000;

        if (Interfaces.getWidgets(LOBBY_FILTER).length > 0) {
            for (Widget widget : Interfaces.getWidgets(DIALOGUE_FILTER)) {
                if (widget.getText() == null)
                    continue;
                Client.processAction(new InputButtonAction(widget.getId()), "Play RuneScape", "");
            }
        }
        GroundItem mark = GroundItems.getNearest("Mark of grace");
        if (mark != null && Movement.isEntityReachable(mark) && mark.getLocation().getPlane() == Players.getLocal().getLocation().getPlane()) {
            mark.processAction("Take");
            return 400;
        }
        Obstacle obstacle = course.getNext();
        if (obstacle == null)
            return 400;

        final GameObject obj;

        if (obstacle.getTile() != null) {
            if (obstacle.getTile().getPlane() == Players.getLocal().getLocation().getPlane() && obstacle.getTile().distance() > 15) {
                Movement.walkTo(obstacle.getTile());
                return 400;
            }

            obj = GameObjects.getNearest(gameObject -> {
                if (gameObject.getName() != null && gameObject.getName().equals(obstacle.getName())) {
                    if (gameObject.getLocation().getRegionX() == obstacle.getTile().getRegionX() && gameObject.getLocation().getRegionY() == obstacle.getTile().getRegionY())
                        return true;
                }
                return false;
            });
        } else
            obj = GameObjects.getNearest(obstacle.name);
        if (obj != null)
            obj.processAction(obstacle.action);
        return 400;
    }

    @Override
    public void render(Graphics2D g) {
        AWTUtil.drawBoldedString(g, "Perfect Agility", 20, 40, Color.MAGENTA);
        AWTUtil.drawBoldedString(g, "Runtime: " + runtime.toElapsedString(), 20, 60, Color.YELLOW);
        AWTUtil.drawBoldedString(g, "Agility Experience: " + formatNumber((Skills.getExperience(Skill.AGILITY) - startingAgilityExperience)) + "(" + perHour(Skills.getExperience(Skill.AGILITY) - startingAgilityExperience) + ")", 20, 80, Color.YELLOW);
        if (Skills.getCurrentLevel(Skill.HITPOINTS) < 10)
            AWTUtil.drawBoldedString(g, "Waiting because your retarded-ass character has less than 10 health!", 20, 100, Color.RED);
    }

    //thanks kenneh
    private String runtime(long i) {
        DecimalFormat nf = new DecimalFormat("00");
        long millis = System.currentTimeMillis() - i;
        long hours = millis / (1000 * 60 * 60);
        millis -= hours * (1000 * 60 * 60);
        long minutes = millis / (1000 * 60);
        millis -= minutes * (1000 * 60);
        long seconds = millis / 1000;
        return nf.format(hours) + ":" + nf.format(minutes) + ":" + nf.format(seconds);
    }

    private String perHour(int gained) {
        return formatNumber((int) ((gained) * 3600000D / (System.currentTimeMillis() - runtime.getStart())));
    }

    private String formatNumber(int start) {
        DecimalFormat nf = new DecimalFormat("0.0");
        return start >= 1000000 ? nf.format(((double) start / 1000000)) + "m" : start >= 1000 ? nf.format(((double) start / 1000)) + "k" : String.valueOf(start);
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void messageReceived(MessageEvent e) {
        if (e.getText().contains("You can't do that from here"))
            stuck++;
    }

    public enum Course implements ICourse {

        GNOME_COURSE(ObstacleFactory.newInstance(true)
                .build(new Area(new Tile(2472, 3438, 0), new Tile(2490, 3436, 0)),
                        "Log balance", "Walk-across", new Tile(2474, 3435))
                .build(new Area(new Tile(2470, 3429, 0), new Tile(2477, 3426, 0)),
                        "Obstacle net", "Climb-over", new Tile(2473, 3425))
                .build(new Area(new Tile(2471, 3424, 1), new Tile(2476, 3422, 1), 1),
                        "Tree branch", "Climb", new Tile(2473, 3422, 1))
                .build(new Area(new Tile(2472, 3421, 2), new Tile(2477, 3418, 2), 2),
                        "Balancing rope", "Walk-on", new Tile(2478, 3420, 2))
                .build(new Area(new Tile(2483, 3421, 2), new Tile(2488, 3418, 2), 2),
                        "Tree branch", "Climb-down", new Tile(2486, 3419, 2))
                .build(new Area(new Tile(2482, 3425, 0), new Tile(2489, 3419, 0)),
                        "Obstacle net", "Climb-over", new Tile(2487, 3426))
                .build(new Area(new Tile(2482, 3431, 0), new Tile(2490, 3427, 0)),
                        "Obstacle pipe", "Squeeze-through", new Tile(2484, 3431))
                .array()
        ),

        WILDERNESS_COURSE(ObstacleFactory.newInstance()
                .build(new Area(new Tile(3078, 3288, 0), new Tile(3006, 3938, 0)),
                        "Obstacle pipe", "Squeeze through")
                .array()
        ),

        DRAYNOR_COURSE(ObstacleFactory.newInstance(true)
                .build(new Area(new Tile(3060, 3281, 0), new Tile(3110, 3147, 0), 0),
                        "Rough wall", "Climb")
                .build(new Area(new Tile(3097, 3281, 3), new Tile(3102, 3277, 3), 3),
                        "Tightrope", "Cross")
                .build(new Area(new Tile(3088, 3276, 3), new Tile(3091, 3273, 3), 3),
                        "Tightrope", "Cross")
                .build(new Area(new Tile(3089, 3267, 3), new Tile(3094, 3265, 3), 3),
                        "Narrow wall", "Balance")
                .build(new Area(new Tile(3088, 3261, 3), new Tile(3088, 3257, 3), 3),
                        "Wall", "Jump-up")
                .build(new Area(new Tile(3088, 3255, 3), new Tile(3094, 3255, 3), 3),
                        "Gap", "Jump")
                .build(new Area(new Tile(3096, 3621, 3), new Tile(3101, 3256, 3), 3),
                        "Crate", "Climb-down")
                .array()
        ),

        VARROCK_COURSE(ObstacleFactory.newInstance(true)
                .build(new Area(new Tile(3249, 3392, 0), new Tile(3186, 3431, 0), 0),
                        "Rough wall", "Climb")
                .build(new Area(new Tile(3219, 3419, 3), new Tile(3214, 3410, 3), 3),
                        "Clothes line", "Cross")
                .build(new Area(new Tile(3208, 3414, 3), new Tile(3201, 3417, 3), 3),
                        "Gap", "Leap")
                .build(new Area(new Tile(3197, 3416, 1), new Tile(3193, 3416, 1), 1),
                        "Wall", "Balance")
                .build(new Area(new Tile(3192, 3406, 3), new Tile(3198, 3402, 3), 3),
                        "Gap", "Leap", new Tile(3193, 3401, 3))
                .build(new Area(new Tile(3182, 3382, 3), new Tile(3208, 3398, 3), 3),
                        "Gap", "Leap", new Tile(3209, 3397, 3))
                .build(new Area(new Tile(3218, 3393, 3), new Tile(3232, 3402, 3), 3),
                        "Gap", "Leap", new Tile(3233, 3402, 3))
                .build(new Area(new Tile(3236, 3403, 3), new Tile(3240, 3408, 3), 3),
                        "Ledge", "Hurdle")
                .build(new Area(new Tile(3240, 3410, 3), new Tile(3236, 3415, 3), 3),
                        "Edge", "Jump-off")
                .array()
        ),

        FALADOR_COURSE(ObstacleFactory.newInstance(true)
                .build(new Area(new Tile(3003, 3363, 0), new Tile(3059, 3328, 0), 0),
                        "Rough wall", "Climb")
                .build(new Area(new Tile(3036, 3343, 3), new Tile(3040, 3342, 3), 3),
                        "Tightrope", "Cross", new Tile(3040, 3343, 3))
                .build(new Area(new Tile(3045, 3349, 3), new Tile(3051, 3341, 3), 3),
                        "Hand holds", "Cross", new Tile(3050, 3350, 3))
                .build(new Area(new Tile(3048, 3358, 3), new Tile(3050, 3357, 3), 3),
                        "Gap", "Jump", new Tile(3048, 3359, 3))
                .build(new Area(new Tile(3045, 3367, 3), new Tile(3048, 3361, 3), 3),
                        "Gap", "Jump", new Tile(3044, 3361, 3))
                .build(new Area(new Tile(3034, 3364, 3), new Tile(3041, 3361, 3), 3),
                        "Tightrope", "Cross", new Tile(3034, 3361, 3))
                .build(new Area(new Tile(3026, 3354, 3), new Tile(3029, 3352, 3), 3),
                        "Tightrope", "Cross", new Tile(3026, 3353, 3))
                .build(new Area(new Tile(3009, 3358, 3), new Tile(3021, 3353, 3), 3),
                        "Gap", "Jump", new Tile(3016, 3352, 3))
                .build(new Area(new Tile(3016, 3349, 3), new Tile(3022, 3343, 3), 3),
                        "Ledge", "Jump", new Tile(3015, 3345, 3))
                .build(new Area(new Tile(3011, 3346, 3), new Tile(3014, 3344, 3), 3),
                        "Ledge", "Jump", new Tile(3011, 3343, 3))// the first bendy corner bit
                .build(new Area(new Tile(3009, 3342, 3), new Tile(3013, 3335, 3), 3),
                        "Ledge", "Jump", new Tile(3012, 3334, 3))
                .build(new Area(new Tile(3012, 3334, 3), new Tile(3017, 3331, 3), 3),
                        "Ledge", "Jump", new Tile(3018, 3332, 3))
                .build(new Area(new Tile(3019, 3335, 3), new Tile(3024, 3332, 3), 3),
                        "Edge", "Jump", new Tile(3025, 3332, 3))
                .array()
        ),

        SEERS_COURSE(ObstacleFactory.newInstance(true)
                .build(new Area(new Tile(2682, 3511, 0), new Tile(2728, 3451, 0), 0),
                        "Wall", "Climb-up")
                .build(new Area(new Tile(2721, 3497, 3), new Tile(2730, 3490, 3), 3),
                        "Gap", "Jump", new Tile(2720, 3492, 3))
                .build(new Area(new Tile(2705, 3495, 2), new Tile(2713, 3488, 2), 2),
                        "Tightrope", "Cross", new Tile(2710, 3489, 2))
                .build(new Area(new Tile(2710, 3481, 2), new Tile(2715, 3477, 2), 2),
                        "Gap", "Jump", new Tile(2710, 3476, 2))
                .build(new Area(new Tile(2700, 3475, 3), new Tile(2715, 3470, 3), 3),
                        "Gap", "Jump", new Tile(2700, 3469, 3))
                .build(new Area(new Tile(2698, 3475, 2), new Tile(2702, 3460, 2), 2),
                        "Edge", "Jump", new Tile(2703, 3461, 2))
                .array()
        ),

        POLLNIVNEACH_COURSE(ObstacleFactory.newInstance(true)
                .build(new Area(new Tile(3344, 3003, 0), new Tile(3400, 2900, 0), 0), "Basket", "Climb-on", new Tile(3351, 2962))
                .build(new Area(new Tile(3351, 2961, 1), new Tile(3346, 2968, 1), 1), "Market stall", "Jump-on")
                .build(new Area(new Tile(3352, 2973, 1), new Tile(3355, 2976, 1), 1), "Banner", "Grab")
                .build(new Area(new Tile(3360, 2977, 1), new Tile(3362, 2979, 1), 1), "Gap", "Leap")
                .build(new Area(new Tile(3366, 2976, 1), new Tile(3369, 2974, 1), 1), "Tree", "Jump-to")
                .build(new Area(new Tile(3369, 2982, 1), new Tile(3365, 2986, 1), 1), "Rough wall", "Climb")
                .build(new Area(new Tile(3365, 2985, 2), new Tile(3355, 2981, 2), 2), "Monkeybars", "Cross")
                .build(new Area(new Tile(3357, 2995, 2), new Tile(3370, 2990, 2), 2), "Tree", "Jump-on")
                .build(new Area(new Tile(3356, 3000, 2), new Tile(3362, 3004, 2), 2), "Drying line", "Jump-to")
                .array()
        ),

        RELLEKKA_COURSE(ObstacleFactory.newInstance(true)
                .build(new Area(new Tile(2675, 3647, 0), new Tile(2620, 3681, 0), 0), "Rough wall", "Climb")
                .build(new Area(new Tile(2626, 3676, 3), new Tile(2622, 3672, 3), 3), "Gap", "Leap")
                .build(new Area(new Tile(2622, 3668, 3), new Tile(2615, 3658, 3), 3), "Tightrope", "Cross")
                .build(new Area(new Tile(2626, 3651, 3), new Tile(2629, 3655, 3), 3), "Gap", "Leap")
                .build(new Area(new Tile(2639, 3653, 3), new Tile(2643, 3649, 3), 3), "Gap", "Hurdle")
                .build(new Area(new Tile(2643, 3657, 3), new Tile(2650, 3662, 3), 3), "Tightrope", "Cross")
                .build(new Area(new Tile(2655, 3665, 3), new Tile(2663, 3685, 3), 3), "Pile of fish", "Jump-in")
                .array()
        ),

        ARDY_COURSE(ObstacleFactory.newInstance(true)
                .build(new Area(new Tile(2647, 3327, 0), new Tile(2679, 3286, 0), 0), "Wooden Beams", "Climb-up")
                .build(new Area(new Tile(2670, 3310, 3), new Tile(2672, 3297, 3), 3), "Gap", "Jump")
                .build(new Area(new Tile(2660, 3320, 3), new Tile(2666, 3316, 3), 3), "Plank", "Walk-on")
                .build(new Area(new Tile(2652, 3320, 3), new Tile(2657, 3316, 3), 3), "Gap", "Jump")
                .build(new Area(new Tile(2652, 3315, 3), new Tile(2654, 3309, 3), 3), "Gap", "Jump", new Tile(2653, 3308, 3))
                .build(new Area(new Tile(2650, 3310, 3), new Tile(2655, 3299, 3), 3), "Steep roof", "Balance-across", new Tile(2654, 3300, 3))
                .build(new Area(new Tile(2654, 3300, 3), new Tile(2658, 3296, 3), 3), "Gap", "Jump", new Tile(2656, 3296, 3))
                .array()
        );

        private final Obstacle[] obstacles;

        private Course(final Obstacle[] obstacles) {
            this.obstacles = obstacles;
        }

        private Obstacle getNext() {
            for (final Obstacle obstacle : obstacles) {
                if (obstacle.getLocation().contains(Players.getLocal()))
                    return obstacle;
            }
            return null;
        }

        public Obstacle[] getObstacles() {
            return obstacles;
        }
    }

    private interface ICourse {
        Obstacle[] getObstacles();
    }

    private static class Obstacle {

        private final Area location;
        private final String name;
        private final String action;
        private final Tile tile; // exact location for obstacle, only needed if script tries 2 do prev obstacle agen
        private final boolean npc;

        public Obstacle(final Area location, final String name, final String action, final boolean npc, Tile tile) {
            this.location = location;
            this.name = name;
            this.action = action;
            this.npc = npc;
            this.tile = tile;
        }

        public Obstacle(final Area location, final String name, final String action, final Tile tile) {
            this(location, name, action, false, tile);
        }

        public Obstacle(final Area location, final String name, final String action) {
            this(location, name, action, false, null);
        }

        public Area getLocation() {
            return location;
        }

        public String getAction() {
            return action;
        }

        public String getName() {
            return name;
        }

        public Tile getTile() {
            return tile;
        }

        public boolean isNpc() {
            return npc;
        }
    }

    private static class ObstacleFactory {

        private static List<Obstacle> obstacles = new ArrayList<>();

        private ObstacleFactory() {

        }

        public static ObstacleFactory newInstance(final boolean clear) {
            if (clear)
                obstacles.clear();
            return new ObstacleFactory();
        }

        public static ObstacleFactory newInstance() {
            return newInstance(false);
        }

        public ObstacleFactory build(final Area loc, final String action, final String target, final boolean npc, final Tile tile) {
            obstacles.add(new Obstacle(loc, action, target, npc, tile));
            return this;
        }

        public ObstacleFactory build(final Area loc, final String action, final String target, final Tile tile) {
            return build(loc, action, target, false, tile);
        }

        public ObstacleFactory build(final Area loc, final String action, final String target) {
            return build(loc, action, target, false, null);
        }

        public Obstacle[] array() {
            return obstacles.toArray(new Obstacle[obstacles.size()]);
        }
    }
}

