package com.ignoransel.whimsicalideas.content.hex.buff;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

import java.util.UUID;

// 敏捷符文，速度提升
public class AgilityStatusEffect extends StatusEffect {

    private static final UUID SPEED_BOOST_UUID = UUID.fromString("00000000-0000-0000-0000-000000000002");

    public AgilityStatusEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x55FF55);
        this.addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                SPEED_BOOST_UUID.toString(), 0.1, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
