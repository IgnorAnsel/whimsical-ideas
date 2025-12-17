package com.ignoransel.whimsicalideas.mixin;

import com.ignoransel.whimsicalideas.content.soulsail.SoulSailActive;
import com.ignoransel.whimsicalideas.content.soulsail.SoulSailBannerItem;
import com.ignoransel.whimsicalideas.content.soulsail.SoulSailRoomManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityDropMixin {

    @Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;Z)Lnet/minecraft/entity/ItemEntity;",
            at = @At("HEAD"))
    private void wi$teleportThenDrop1(ItemStack stack, boolean retainOwnership, CallbackInfoReturnable<?> cir) {
        wi$teleportIfSoulSail(stack);
    }

    @Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;",
            at = @At("HEAD"))
    private void wi$teleportThenDrop2(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<?> cir) {
        wi$teleportIfSoulSail(stack);
    }

    private void wi$teleportIfSoulSail(ItemStack stack) {
        PlayerEntity self = (PlayerEntity)(Object)this;
        if (!(self instanceof ServerPlayerEntity sp)) return;
        if (!sp.getServerWorld().getRegistryKey().equals(SoulSailRoomManager.SOUL_SAIL_DIM)) return;

        if (!stack.isEmpty() && SoulSailActive.isActiveSail(sp, stack) && stack.getItem() instanceof SoulSailBannerItem) {
            SoulSailRoomManager.teleportBack(sp, stack);
        }
    }
}
