// TuningParams.java
package com.ignoransel.whimsicalideas.content.tuningfork;

import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;

public record TuningParams(int range, int durationTicks, int power) {

    public static TuningParams fromPlayer(PlayerEntity player) {
        // 攻击力（含装备/药水等修饰）
        double dmg = player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);

        // 你可以改成更符合手感的映射
        int power = clamp((int)Math.round(dmg * 2.0), 1, 15);          // 伤害越高红石越强
        int range = clamp(16 + (int)Math.round(dmg * 2.0), 8, 48);     // 16 起步，最高 48
        int duration = clamp(40 + (int)Math.round(dmg * 10.0), 20, 200); // 默认 2s(40t)，最高 10s(200t)

        return new TuningParams(range, duration, power);
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }
}
