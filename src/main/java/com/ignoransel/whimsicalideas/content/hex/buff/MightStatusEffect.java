package com.ignoransel.whimsicalideas.content.hex.buff;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;

import java.util.UUID;

// 大力符文，伤害提升20%
public class MightStatusEffect extends StatusEffect {

    private static final UUID DAMAGE_BOOST_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    public MightStatusEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xFF5555);
        this.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                DAMAGE_BOOST_UUID.toString(), 0.2, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}

