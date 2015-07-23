/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bundledscripts.complete.miner;

import com.inubot.bundledscripts.proframework.ProModel.Event;
import com.inubot.bundledscripts.proframework.ProController;

/**
 * @author Dogerina
 * @since 15-07-2015
 */
public class MinerController extends ProController<MinerView, MinerModel> implements MinerConstants {

    MinerController(MinerView view, MinerModel model) {
        super(view, model);
    }

    @Override
    public void encounter(Event<?> event) {
        switch (event.getProperty()) {
            case INITIATE_PROP: {
                getModel().setSelectedLocation(getView().getSelectedLocation());
                getModel().setSelectedRocks(getView().getSelectedRocks());
                getModel().setBanking(!getView().isPowermining());
                break;
            }
        }
    }
}
