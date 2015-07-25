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
                break;
            }
        }
    }
}
