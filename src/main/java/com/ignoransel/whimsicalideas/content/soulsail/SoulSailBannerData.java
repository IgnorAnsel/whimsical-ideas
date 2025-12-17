package com.ignoransel.whimsicalideas.content.soulsail;

import net.minecraft.nbt.NbtList;

public interface SoulSailBannerData {
    long whimsicalideas$getSouls();
    void whimsicalideas$setSouls(long v);

    NbtList whimsicalideas$getStoredMobs();
    void whimsicalideas$setStoredMobs(NbtList list);
}
