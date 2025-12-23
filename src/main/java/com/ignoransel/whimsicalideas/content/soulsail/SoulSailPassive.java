package com.ignoransel.whimsicalideas.content.soulsail;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Objects;

public final class SoulSailPassive {
    private SoulSailPassive() {}

    public static boolean tryUseSoulTotem(ServerPlayerEntity sp, ItemStack stack, DamageSource source) {
        SoulBannerGrade grade = SoulSailItemCompat.getBannerGrade(stack);
        if (grade.getLevel() < SoulBannerGrade.HEAVEN.getLevel()) return false;

        if (!SoulSailItemCompat.isSoulTotemEnabled(stack)) return false;
        long now = Objects.requireNonNull(sp.getServer()).getOverworld().getTime();
        // 冷却：用物品 cooldown 控制（简单可靠）
        if (SoulSailItemCompat.isAbilityOnCooldown(stack, SoulSailAbility.SOUL_TOTEM, now)) return false;


        long cost = SoulSailAbility.SOUL_TOTEM.costSouls;
        if (!SoulSailItemCompat.spendRefinedSouls(stack, cost)) {
            sp.sendMessage(Text.literal("魂替失败：魂魄不足").formatted(Formatting.RED), true);
            return false;
        }

        // 模拟原版图腾效果（可调）
        sp.setHealth(1.0F);
        sp.clearStatusEffects();
        sp.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
        sp.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
        sp.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));

        SoulSailItemCompat.setAbilityCooldown(stack, SoulSailAbility.SOUL_TOTEM, now, SoulSailAbility.SOUL_TOTEM.cooldownTicks);

        sp.sendMessage(Text.literal("魂替发动 (-" + cost + "魂)").formatted(Formatting.AQUA), true);
        return true;
    }
}
