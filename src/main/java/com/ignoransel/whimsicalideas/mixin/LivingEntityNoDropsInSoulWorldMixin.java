package com.ignoransel.whimsicalideas.mixin;

import com.ignoransel.whimsicalideas.content.soulsail.SoulSailRoomManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityNoDropsInSoulWorldMixin {

    private boolean soulsail$inSoulWorld() {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self.getWorld() instanceof ServerWorld sw)) return false;
        return sw.getRegistryKey().equals(SoulSailRoomManager.SOUL_SAIL_DIM);
    }

    @Inject(method = "dropLoot", at = @At("HEAD"), cancellable = true)
    private void soulsail$noLoot(DamageSource source, boolean causedByPlayer, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (soulsail$inSoulWorld() && !(self instanceof PlayerEntity)) {
            ci.cancel();
        }
    }

    @Inject(method = "dropEquipment", at = @At("HEAD"), cancellable = true)
    private void soulsail$noEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (soulsail$inSoulWorld() && !(self instanceof PlayerEntity)) {
            ci.cancel();
        }
    }

    @Inject(method = "dropXp", at = @At("HEAD"), cancellable = true)
    private void soulsail$noXp(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (soulsail$inSoulWorld() && !(self instanceof PlayerEntity)) {
            ci.cancel();
        }
    }
}
