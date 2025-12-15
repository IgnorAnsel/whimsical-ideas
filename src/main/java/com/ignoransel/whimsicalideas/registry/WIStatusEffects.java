package com.ignoransel.whimsicalideas.registry;

import com.ignoransel.whimsicalideas.WhimsicalIdeas;

import com.ignoransel.whimsicalideas.content.hex.buff.AgilityStatusEffect;
import com.ignoransel.whimsicalideas.content.hex.buff.MaxMiningStatusEffect;
import com.ignoransel.whimsicalideas.content.hex.buff.MightStatusEffect;
import com.ignoransel.whimsicalideas.content.hex.buff.VitalityStatusEffect;
import com.ignoransel.whimsicalideas.effect.BacklashEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class WIStatusEffects {
    private WIStatusEffects(){}

    public static StatusEffect BACKLASH;
    public static final StatusEffect MIGHT = new MightStatusEffect();
    public static final StatusEffect AGILITY = new AgilityStatusEffect();
    public static final StatusEffect VITALITY = new VitalityStatusEffect();
    public static final StatusEffect MAX_MINING = new MaxMiningStatusEffect();
    public static void init() {
        BACKLASH = Registry.register(
                Registries.STATUS_EFFECT,
                new Identifier(WhimsicalIdeas.MODID, "backlash"),

                new BacklashEffect()
        );
        Registry.register(Registries.STATUS_EFFECT, new Identifier("whimsical-ideas", "might"), MIGHT);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("whimsical-ideas", "agility"), AGILITY);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("whimsical-ideas", "vitality"), VITALITY);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("whimsical-ideas", "max_mining"), MAX_MINING);
    }
}
