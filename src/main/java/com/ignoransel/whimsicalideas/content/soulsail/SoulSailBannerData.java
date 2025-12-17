package com.ignoransel.whimsicalideas.content.soulsail;

public interface SoulSailBannerData {
    boolean wi$isSoulSailBanner();

    String wi$getSailId();
    void wi$setSailId(String id);

    long wi$getRawSouls();
    long wi$getRefinedSouls();

    void wi$setRawSouls(long v);
    void wi$setRefinedSouls(long v);

    /** Souls = Raw + Refined */
    void wi$syncTotal();

    int wi$getStoredCount();

    /** pop 一个 StoredMobs，返回实体 id 字符串；为空则返回 null */
    String wi$popOneStoredMob();

    /** markDirty + updateListeners 同步到客户端 */
    void wi$markDirtyAndSync();
}
