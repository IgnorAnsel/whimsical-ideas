package com.ignoransel.whimsicalideas.content.hex.buff;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class RegenerationStatusEffect extends StatusEffect {
    public RegenerationStatusEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x00FF00);
    }
}

