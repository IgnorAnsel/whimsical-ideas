package com.ignoransel.whimsicalideas.content.hex.hexs;

import com.ignoransel.whimsicalideas.content.hex.HexBase;
import com.ignoransel.whimsicalideas.content.hex.HexRarity;
import com.ignoransel.whimsicalideas.registry.WIStatusEffects;
import net.minecraft.entity.effect.StatusEffectInstance;

public class HexRegeneration extends HexBase {
    public HexRegeneration() {
        super("regeneration", "无休回复", "每移动一百格回复一点生命", HexRarity.GOLD, new StatusEffectInstance(WIStatusEffects.REGENERATION, Integer.MAX_VALUE, 0, false, true, true));
    }
}
