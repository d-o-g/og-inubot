/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */

import com.inubot.api.methods.*;
import com.inubot.api.oldschool.GameObject;
import com.inubot.api.util.Time;
import com.inubot.api.util.filter.IdFilter;
import com.inubot.api.util.filter.NameFilter;
import com.inubot.script.Manifest;
import com.inubot.script.Script;

/**
 * @author Dogerina
 * @since 26-07-2015
 */
@Manifest(name = "bigdik powerminer", developer = "dogerina", desc = "")
public class Powerminer extends Script {

    @Override
    public int loop() {
        GameObject rock = GameObjects.getNearest(new IdFilter<>(12606, 12605));
        if (Inventory.contains("Iron ore") && Players.getLocal().getAnimation() == -1) {
            Inventory.dropAllExcept(new NameFilter<>(true, "pickaxe"));
            return 700;
        }
        if (rock != null) {
            rock.processAction("Mine");
            if (Time.await(() -> {
                if (Players.getLocal().getAnimation() != -1) {
                    return true;
                }
                GameObject current = GameObjects.getNearest(t -> t.getLocation().equals(rock.getLocation()));
                return current != null && current.getId() != rock.getId();
            }, 1000)) {
                GameObject top = GameObjects.getNearest(t -> t.getLocation().equals(rock.getLocation()));
                if (top != null && top.getId() == rock.getId()) {
                    Time.sleep(300, 450);
                    Time.await(() -> {
                        if (Players.getLocal().getAnimation() == -1) {
                            return true;
                        }
                        GameObject current = GameObjects.getNearest(t -> t.getLocation().equals(rock.getLocation()));
                        return current != null && current.getId() != rock.getId();
                    }, 1000);
                }
            }
        }
        return 900;
    }
}
