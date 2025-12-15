package com.ignoransel.whimsicalideas.content.hex.hexs;

import com.ignoransel.whimsicalideas.content.hex.HexBase;
import com.ignoransel.whimsicalideas.content.hex.HexRarity;
import com.ignoransel.whimsicalideas.registry.WIStatusEffects;
import net.minecraft.entity.effect.StatusEffectInstance;

public class HexMight extends HexBase {
    public HexMight() {
        super("Might", "大力", "力量大啊",HexRarity.IRON, new StatusEffectInstance(WIStatusEffects.MIGHT, Integer.MAX_VALUE, 0, false, true, true));
    }

}
