package com.ignoransel.whimsicalideas.content.soulsail;

public enum SoulSailTier {
    MORTAL(1_00L, 1),
    EARTH(5_00L, 5),
    HEAVEN(10_00L, 10),
    MYSTERIOUS(50_00L, 16),
    YELLOW(100_00L, 23),
    UNIVERSE(300_00L, 32),
    COSMOS(1_000_00L, 41),
    FLOOD(3_000_00L, 51),
    WASTELAND(10_000_00L, 62),
    IMMORTAL(100_000_000L, 74);

    public final long capacity;
    public final int roomRadius;

    SoulSailTier(long capacity, int roomRadius) {
        this.capacity = capacity;
        this.roomRadius = roomRadius;
    }
}
