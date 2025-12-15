package com.ignoransel.whimsicalideas.content.hex.buff;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

import java.util.UUID;

// 生命符文，生命上限提升
public class VitalityStatusEffect extends StatusEffect {

    private static final UUID HEALTH_BOOST_UUID = UUID.fromString("00000000-0000-0000-0000-000000000003");

    public VitalityStatusEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x5555FF);
        this.addAttributeModifier(EntityAttributes.GENERIC_MAX_HEALTH,
                HEALTH_BOOST_UUID.toString(), 10.0, EntityAttributeModifier.Operation.ADDITION);
    }
}
