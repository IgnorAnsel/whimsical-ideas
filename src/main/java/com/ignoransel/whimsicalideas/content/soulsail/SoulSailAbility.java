package com.ignoransel.whimsicalideas.content.soulsail;

public enum SoulSailAbility {
    NONE(SoulBannerGrade.MORTAL, "无"),
    HEAL(SoulBannerGrade.EARTH, "回春"); // 地阶技能：回血

    public final SoulBannerGrade minGrade;
    public final String displayName;

    SoulSailAbility(SoulBannerGrade minGrade, String name) {
        this.minGrade = minGrade;
        this.displayName = name;
    }

    public boolean unlockedBy(SoulBannerGrade grade) {
        return grade.getLevel() >= minGrade.getLevel();
    }
}
