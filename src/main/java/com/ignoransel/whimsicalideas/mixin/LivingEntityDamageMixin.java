package com.ignoransel.whimsicalideas.mixin;

import com.ignoransel.whimsicalideas.content.soulsail.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityDamageMixin {

    @ModifyVariable(
            method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z",
            at = @At("HEAD"),
            argsOnly = true
    )
    private float wi$soulBarrierReduceDamage(float amount, DamageSource source) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof ServerPlayerEntity sp)) return amount;
        if (amount <= 0) return amount;

        ItemStack stack = findSoulSail(sp);
        if (stack.isEmpty()) return amount;

        // 品阶 + 开关 + 冷却 + 扣魂
        SoulBannerGrade grade = SoulSailItemCompat.getBannerGrade(stack);
        if (grade.getLevel() < SoulBannerGrade.MYSTERIOUS.getLevel()) return amount;
        if (!SoulSailItemCompat.isSoulBarrierEnabled(stack)) return amount;

        long now = sp.age;
        if (SoulSailItemCompat.isAbilityOnCooldown(stack, SoulSailAbility.SOUL_BARRIER, now)) return amount;

        long cost = SoulSailAbility.SOUL_BARRIER.costSouls;
        if (cost > 0 && !SoulSailItemCompat.spendRefinedSouls(stack, cost)) {
            // 魂不足就不触发，不要刷屏可去掉提示
            sp.sendMessage(Text.literal("魂御失败：魂魄不足").formatted(Formatting.RED), true);
            return amount;
        }

        SoulSailItemCompat.setAbilityCooldown(stack, SoulSailAbility.SOUL_BARRIER, now, SoulSailAbility.SOUL_BARRIER.cooldownTicks);

        float multiplier = 0.6f; // 本次伤害乘数（你可调）
        sp.sendMessage(Text.literal("魂御触发：减伤").formatted(Formatting.LIGHT_PURPLE), true);
        return amount * multiplier;
    }

    private static ItemStack findSoulSail(ServerPlayerEntity sp) {
        ItemStack main = sp.getMainHandStack();
        if (main.getItem() instanceof SoulSailBannerItem) return main;

        ItemStack off = sp.getOffHandStack();
        if (off.getItem() instanceof SoulSailBannerItem) return off;

        return ItemStack.EMPTY;
    }
}
