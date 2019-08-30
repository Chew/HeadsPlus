package io.github.thatsmusic99.headsplus.config.customheads.inventories;

import io.github.thatsmusic99.headsplus.config.customheads.HeadInventory;

public class ChallengesMenu extends HeadInventory {
    @Override
    public String getDefaultTitle() {
        return "HeadsPlus Challenges: Menu";
    }

    @Override
    public String getDefaultItems() {
        return  "GGGGGGGGG" +
                "GAAAAAAAG" +
                "GASASASAG" +
                "GAASASAAG" +
                "GAAAAAAAG" +
                "GGGBXNGGG";
    }

    @Override
    public String getDefaultId() {
        return "challenges-menu";
    }

    @Override
    public String getName() {
        return "challenges-menu";
    }
}
