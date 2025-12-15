package com.ignoransel.whimsicalideas.content.hex;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;

public abstract class HexBase {

    private final String id;
    private final String name;
    private final String description;
    private final HexRarity rarity;
    private final StatusEffectInstance effect;

    protected HexBase(String id, String name, String description, HexRarity rarity, StatusEffectInstance effect) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.rarity = rarity;
        this.effect = effect;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public HexRarity getRarity() { return rarity; }
    public StatusEffectInstance getEffect() { return effect; }

    public void apply(PlayerEntity player) {
        if (effect != null && !player.getWorld().isClient) {
            player.addStatusEffect(new StatusEffectInstance(effect));
        }
    }
}
