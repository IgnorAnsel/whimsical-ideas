package com.ignoransel.whimsicalideas.content.hex.buff;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class MaxMiningStatusEffect extends StatusEffect {

    public MaxMiningStatusEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xAAAAFF); // 蓝色图标
        // 属性修改暂时不需要，逻辑通过事件/Mixin控制
    }
}
