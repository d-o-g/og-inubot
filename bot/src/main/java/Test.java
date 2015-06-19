/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */

import org.runedream.api.methods.Npcs;
import org.runedream.api.oldschool.Npc;

/**
 * @author Jerome
 * @since 03-06-2015
 */
public class Test {

    public static void penis() {
        Npc randomNearCow = Npcs.getPool()
                .identify("Cow")          //Select element named Cow
                .animating(false)         //must not be animating
                .targeting(false)         //must have no target
                .healthBarVisible(false)  //must not be under attack (health bar not visible)
                .nearest()                //sort the results by distance (nearest first)
                .limit(3)                 //remove all elements except the first 3
                .fish();                  //fish the pool for a random element

        Npc nearestCow = Npcs.getPool()
                .identify("Cow")
                .animating(false)
                .targeting(false)
                .healthBarVisible(false)
                .closest();
    }

    public static void main(String... args) {
        ok();
    }

    static void ok() {
        StackTraceElement stackTraceElement = new Exception().getStackTrace()[1];
        String string = stackTraceElement.getMethodName() + stackTraceElement.getClassName();
        System.out.println(string);
    }
}
