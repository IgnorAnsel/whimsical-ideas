package com.ignoransel.whimsicalideas.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class BacklashEffect extends StatusEffect {

    public BacklashEffect() {
        super(StatusEffectCategory.HARMFUL, 0x6B1A7A); // 紫红色

        // 反噬：减速 + 降攻击（你可改数值）
        this.addAttributeModifier(
                EntityAttributes.GENERIC_MOVEMENT_SPEED,
                "c5d39b6c-3b8a-4d4a-a1fd-1e6bcb1c8b2a",
                -0.15,
                EntityAttributeModifier.Operation.MULTIPLY_TOTAL
        );

        this.addAttributeModifier(
                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                "7a1c1c65-5f34-4c87-8fd7-3d1c9f2f6a11",
                -2.0,
                EntityAttributeModifier.Operation.ADDITION
        );
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // 每 (20 / (amp+1)) tick 触发一次，最低每 5 tick 一次
        int interval = Math.max(5, 20 / (amplifier + 1));
        return duration % interval == 0;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        // 反噬：持续扣血（魔法伤害），不会被护甲减免
        float dmg = 1.0f + amplifier * 0.5f; // 你可调
        entity.damage(entity.getDamageSources().magic(), dmg);
    }
}
