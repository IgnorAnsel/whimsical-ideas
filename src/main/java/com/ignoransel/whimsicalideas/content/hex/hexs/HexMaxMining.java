package com.ignoransel.whimsicalideas.content.hex.hexs;

import com.ignoransel.whimsicalideas.content.hex.HexBase;
import com.ignoransel.whimsicalideas.content.hex.HexRarity;
import com.ignoransel.whimsicalideas.registry.WIStatusEffects;
import net.minecraft.entity.effect.StatusEffectInstance;

public class HexMaxMining extends HexBase {

    public HexMaxMining() {
        super("MaxMining","神稿", "我的神稿说是",HexRarity.GOLD,
                new StatusEffectInstance(WIStatusEffects.MAX_MINING, Integer.MAX_VALUE, 0, false, true, true));
    }
}
