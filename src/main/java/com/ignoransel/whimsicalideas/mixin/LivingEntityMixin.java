package com.ignoransel.whimsicalideas.mixin;

import com.ignoransel.whimsicalideas.content.soulsail.SoulSailPassive;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.ignoransel.whimsicalideas.content.soulsail.SoulSailItemCompat.findSoulSail;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "tryUseTotem", at = @At("RETURN"), cancellable = true)
    private void wi$tryUseSoulTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return; // 原版图腾已触发，直接退出

        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof ServerPlayerEntity sp)) return;

        // 主手/副手找你的魂幡
        ItemStack stack = findSoulSail(sp);
        if (stack.isEmpty()) return;

        if (!SoulSailPassive.tryUseSoulTotem(sp, stack, source)) return;

        cir.setReturnValue(true); // 告诉 MC：图腾已消耗，阻止死亡
    }

}

