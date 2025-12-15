package com.ignoransel.whimsicalideas.content.hex.hexs;

import com.ignoransel.whimsicalideas.content.hex.HexBase;
import com.ignoransel.whimsicalideas.content.hex.HexRarity;
import com.ignoransel.whimsicalideas.registry.WIStatusEffects;
import net.minecraft.entity.effect.StatusEffectInstance;

public class HexRefine extends HexBase {
    public HexRefine() {
        super("refine", "顷刻炼化", "挖掘的矿物自动变为其矿锭",HexRarity.GOLD, new StatusEffectInstance(WIStatusEffects.REFINE, Integer.MAX_VALUE, 0, false, true, true));
    }
}
