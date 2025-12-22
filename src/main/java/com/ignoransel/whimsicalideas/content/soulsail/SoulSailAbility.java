package com.ignoransel.whimsicalideas.content.soulsail;

public enum SoulSailAbility {
    NONE(SoulBannerGrade.MORTAL, "无", false, 0L, 0),
    HEAL(SoulBannerGrade.EARTH, "回春", false, 200L, 40),
    SOUL_TOTEM(SoulBannerGrade.HEAVEN, "魂替", true, 1500L, 600),
    SOUL_BARRIER(SoulBannerGrade.MYSTERIOUS, "魂御", true, 120L, 80);

//    // 玄阶：护盾（吸收 + 抗性）
//    SHIELD(SoulBannerGrade.MYSTERIOUS, "玄盾", 500L, 120),
//
//    // 黄阶：魂步（冲刺位移）
//    DASH(SoulBannerGrade.YELLOW, "黄影", 400L, 60),
//
//    // 宇阶：魂爆（范围伤害+击退）
//    BLAST(SoulBannerGrade.UNIVERSE, "宇爆", 800L, 120),
//
//    // 宙阶：宙缚（范围减速+虚弱+发光）
//    BIND(SoulBannerGrade.COSMOS, "宙缚", 900L, 160),
//
//    // 洪阶：洪噬（范围抽血：伤敌回血）
//    DRAIN(SoulBannerGrade.FLOOD, "洪噬", 1200L, 160),
//
//    // 荒阶：荒怒（强力增益但带饥饿）
//    BERSERK(SoulBannerGrade.WASTELAND, "荒怒", 2000L, 240),
//
//    // 仙阶：仙临（强力全套增益+净化）
//    ASCEND(SoulBannerGrade.IMMORTAL, "仙临", 5000L, 400);

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
