/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bundledscripts.complete.chopper;

import com.inubot.bundledscripts.proframework.ProController;
import com.inubot.bundledscripts.proframework.ProModel.Event;

public class ChopperController extends ProController<ChopperView, ChopperModel> implements ChopperConstants {

    ChopperController(ChopperView view, ChopperModel model) {
        super(view, model);
    }

    @Override
    public void encounter(Event<?> event) {
        switch (event.getProperty()) {
            case START_PROP: {
                getModel().setBanking(getView().isBanking());
                getModel().setProgressive(getView().isProgressive());
                break;
            }
        }
    }
}
