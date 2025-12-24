package com.ignoransel.whimsicalideas.content.soulsail;

public enum SoulSailAbility {
    NONE(SoulBannerGrade.MORTAL, "无", false, 0L, 0),
    HEAL(SoulBannerGrade.EARTH, "回春", false, 200L, 40),
    SOUL_BARRIER(SoulBannerGrade.HEAVEN, "魂御", true, 120L, 80),
    SOUL_TOTEM(SoulBannerGrade.MYSTERIOUS, "魂替", true, 100L, 600),
    LIGHTNING(SoulBannerGrade.YELLOW, "唤雷", false, 600L, 100),
    SOUL_DOMAIN(SoulBannerGrade.UNIVERSE, "魂域", true, 0L, 0),
    SOUL_GRASP(SoulBannerGrade.COSMOS, "魂锁", false, 2000L, 120),
    SOUL_WAVE(SoulBannerGrade.FLOOD, "魂潮", false, 3500L, 1600),
    SOUL_MAELSTROM(SoulBannerGrade.WASTELAND, "归葬", false, 8000L, 2400),
    SOUL_JUDGMENT(SoulBannerGrade.IMMORTAL, "魂劫", false, 5000L, 2400);


    public final SoulBannerGrade minGrade;
    public final String displayName;
    public final boolean passive;
    public final long costSouls;
    public final int cooldownTicks;

    SoulSailAbility(SoulBannerGrade minGrade, String displayName, boolean passive, long costSouls, int cooldownTicks) {
        this.minGrade = minGrade;
        this.displayName = displayName;
        this.passive = passive;
        this.costSouls = costSouls;
        this.cooldownTicks = cooldownTicks;
    }

    public boolean unlockedBy(SoulBannerGrade grade) {
        return grade.getLevel() >= minGrade.getLevel();
    }
}
