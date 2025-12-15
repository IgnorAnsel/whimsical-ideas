package com.ignoransel.whimsicalideas.content.hex;

import com.ignoransel.whimsicalideas.content.hex.buff.HexBuff;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;

public abstract class HexBase {

    private final HexRarity rarity;
    private final String name;
    private final StatusEffectInstance effect;

    public HexBase(String name, HexRarity rarity, StatusEffectInstance effect) {
        this.name = name;
        this.rarity = rarity;
        this.effect = effect;
    }

    public HexRarity getRarity() { return rarity; }

    public String getName() { return name; }

    public StatusEffectInstance getEffect() { return effect; }

    public void apply(PlayerEntity player) {
        if (effect != null) {
            player.addStatusEffect(new StatusEffectInstance(effect));
        }
    }
}
