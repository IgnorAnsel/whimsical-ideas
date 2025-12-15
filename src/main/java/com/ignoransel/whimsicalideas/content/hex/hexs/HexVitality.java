package com.ignoransel.whimsicalideas.content.hex.hexs;

import com.ignoransel.whimsicalideas.content.hex.HexBase;
import com.ignoransel.whimsicalideas.content.hex.HexRarity;
import com.ignoransel.whimsicalideas.registry.WIStatusEffects;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;

public class HexVitality extends HexBase {
    public HexVitality() {
        super("vitality", "生命", "生命多了",HexRarity.IRON, new StatusEffectInstance(WIStatusEffects.VITALITY, Integer.MAX_VALUE, 0, false, true, true));
    }
}