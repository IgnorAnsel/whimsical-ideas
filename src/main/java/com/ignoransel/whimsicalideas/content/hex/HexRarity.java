package com.ignoransel.whimsicalideas.content.hex;


public enum HexRarity {

    IRON(0xFFD8D8D8, 0),
    GOLD(0xFFFFD700, 1),
    DIAMOND(0xFF55FFFF, 2),
    NETHERITE(0xFF3B3B3B, 3);

    /** UI / 文本颜色 */
    private final int color;

    /** 稀有度等级（用于排序 / 权重） */
    private final int level;

    HexRarity(int color, int level) {
        this.color = color;
        this.level = level;
    }

    public int getColor() {
        return color;
    }

    public int getLevel() {
        return level;
    }
}
