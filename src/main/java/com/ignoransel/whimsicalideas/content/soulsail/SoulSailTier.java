package com.ignoransel.whimsicalideas.content.soulsail;

public enum SoulSailTier {
    ZUN(100_000_000, 64);

    public final long capacity;
    public final int roomRadius; // 房间半径（用 barrier 围成边界）

    SoulSailTier(long capacity, int roomRadius) {
        this.capacity = capacity;
        this.roomRadius = roomRadius;
    }
}
