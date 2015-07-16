/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.proscripts.miner;

import com.inubot.script.bundled.proscripts.framework.ProModel;

/**
 * @author Dogerina
 * @since 15-07-2015
 */
public class MinerModel extends ProModel implements MinerConstants {

    private ProMiner.Rock[] selectedRocks;
    private ProMiner.Location selectedLocation;
    private boolean banking;
    private int oreMined;
    private int startExperience = -1;

    MinerModel() {

    }

    public ProMiner.Rock[] getSelectedRocks() {
        return selectedRocks;
    }

    public void setSelectedRocks(ProMiner.Rock[] selectedRocks) {
        this.selectedRocks = selectedRocks;
        fireLater(ROCKS_PROP);
    }

    public ProMiner.Location getSelectedLocation() {
        return selectedLocation;
    }

    public void setSelectedLocation(ProMiner.Location selectedLocation) {
        this.selectedLocation = selectedLocation;
        fireLater(LOCATION_PROP);
    }

    public boolean isBanking() {
        return banking;
    }

    public void setBanking(boolean banking) {
        this.banking = banking;
        fireLater(BANKING_PROP);
    }

    public int getOreMined() {
        return oreMined;
    }

    public void setOreMined(int oreMined) {
        this.oreMined = oreMined;
    }

    public void incrementOreMined() {
        oreMined++;
    }

    public int getStartExperience() {
        return startExperience;
    }

    public void setStartExperience(int startExperience) {
        this.startExperience = startExperience;
    }
}
