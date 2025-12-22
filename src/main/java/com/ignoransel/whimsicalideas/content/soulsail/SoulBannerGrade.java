package com.ignoransel.whimsicalideas.content.soulsail;

import net.minecraft.util.Formatting;

/**
 * 魂幡品阶枚举
 * 十阶划分：凡、地、天、玄、黄、宇、宙、洪、荒、仙
 */
public enum SoulBannerGrade {
    MORTAL(0, "凡阶", 0xFFB0B0B0, 1.0f, 0xCCB0B0B0, false, 1.0f, 0.5f, 0.5f, 1000L, 1.0f, 8.0f, SoulSailTier.MORTAL, Formatting.GRAY),
    EARTH(1, "地阶", 0xFF4CAF50, 1.1f, 0xCC4CAF50, true, 1.2f, 0.6f, 0.6f, 2000L, 1.2f, 10.0f, SoulSailTier.EARTH, Formatting.DARK_GREEN),
    HEAVEN(2, "天阶", 0xFF2196F3, 1.2f, 0xCC2196F3, true, 1.4f, 0.7f, 0.7f, 3000L, 1.4f, 12.0f, SoulSailTier.HEAVEN, Formatting.BLUE),
    MYSTERIOUS(3, "玄阶", 0xFF9C27B0, 1.3f, 0xCC9C27B0, true, 1.6f, 0.8f, 0.8f, 5000L, 1.6f, 14.0f, SoulSailTier.MYSTERIOUS, Formatting.DARK_PURPLE),
    YELLOW(4, "黄阶", 0xFFFFC107, 1.4f, 0xCCFFC107, true, 1.8f, 0.9f, 0.9f, 8000L, 1.8f, 16.0f, SoulSailTier.YELLOW, Formatting.YELLOW),
    UNIVERSE(5, "宇阶", 0xFF3F51B5, 1.5f, 0xCC3F51B5, true, 2.0f, 1.0f, 1.0f, 12000L, 2.0f, 18.0f, SoulSailTier.UNIVERSE, Formatting.DARK_BLUE),
    COSMOS(6, "宙阶", 0xFF00BCD4, 1.6f, 0xCC00BCD4, true, 2.2f, 1.1f, 1.1f, 20000L, 2.2f, 20.0f, SoulSailTier.COSMOS, Formatting.AQUA),
    FLOOD(7, "洪阶", 0xFFE91E63, 1.7f, 0xCCE91E63, true, 2.4f, 1.2f, 1.2f, 30000L, 2.4f, 22.0f, SoulSailTier.FLOOD, Formatting.LIGHT_PURPLE),
    WASTELAND(8, "荒阶", 0xFFFF5722, 1.8f, 0xCCFF5722, true, 2.6f, 1.3f, 1.3f, 50000L, 2.6f, 24.0f, SoulSailTier.WASTELAND, Formatting.GOLD),
    IMMORTAL(9, "仙阶", 0xFFFFEB3B, 2.0f, 0xCCFFEB3B, true, 3.0f, 1.5f, 1.5f, 100000L, 3.0f, 30.0f, SoulSailTier.IMMORTAL, Formatting.RED);
    private final int level;
    private final String displayName;
    private final int glowColor;
    private final float scale;
    private final int particleColor;
    private final boolean hasAura;
    private final float auraRadius;
    private final float glowIntensity;
    private final float animSpeed;
    private final long soulCapacity;
    private final float refineSpeed;
    private final float soulAttraction;
    private final SoulSailTier soulSailTier;
    private final Formatting tooltipFormatting;


    SoulBannerGrade(int level, String displayName, int glowColor, float scale,
                    int particleColor, boolean hasAura, float auraRadius,
                    float glowIntensity, float animSpeed, long soulCapacity,
                    float refineSpeed, float soulAttraction, SoulSailTier soulSailTier, Formatting tooltipFormatting) {
        this.level = level;
        this.displayName = displayName;
        this.glowColor = glowColor;
        this.scale = scale;
        this.particleColor = particleColor;
        this.hasAura = hasAura;
        this.auraRadius = auraRadius;
        this.glowIntensity = glowIntensity;
        this.animSpeed = animSpeed;
        this.soulCapacity = soulCapacity;
        this.refineSpeed = refineSpeed;
        this.soulAttraction = soulAttraction;
        this.soulSailTier = soulSailTier;
        this.tooltipFormatting = tooltipFormatting;
    }

    // Getter方法
    public int getLevel() { return level; }
    public String getDisplayName() { return displayName; }
    public int getGlowColor() { return glowColor; }
    public float getScale() { return scale; }
    public int getParticleColor() { return particleColor; }
    public boolean hasAura() { return hasAura; }
    public float getAuraRadius() { return auraRadius; }
    public float getGlowIntensity() { return glowIntensity; }
    public float getAnimSpeed() { return animSpeed; }
    public long getSoulCapacity() { return soulCapacity; }
    public float getRefineSpeed() { return refineSpeed; }
    public float getSoulAttraction() { return soulAttraction; }
    public Formatting getTooltipFormatting() {
        return tooltipFormatting;
    }
    // 按等级获取枚举
    public static SoulBannerGrade byLevel(int level) {
        for (SoulBannerGrade grade : values()) {
            if (grade.getLevel() == level) {
                return grade;
            }
        }
        return MORTAL;
    }

    public SoulSailTier getSoulSailTier() {
        return soulSailTier;
    }
}