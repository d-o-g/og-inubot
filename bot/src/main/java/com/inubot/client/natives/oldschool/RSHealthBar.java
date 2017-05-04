package com.inubot.client.natives.oldschool;

public interface RSHealthBar extends RSNode {

    RSHealthBarDefinition getDefinition();

    RSNodeIterable<RSHitbar> getHitsplats();
}
