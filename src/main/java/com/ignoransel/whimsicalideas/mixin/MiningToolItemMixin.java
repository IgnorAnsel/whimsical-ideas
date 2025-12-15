package com.ignoransel.whimsicalideas.mixin;

import com.ignoransel.whimsicalideas.registry.WIStatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.MiningToolItem;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MiningToolItem.class)
public abstract class MiningToolItemMixin {

    @Inject(method = "isSuitableFor", at = @At("HEAD"), cancellable = true)
    private void modifyMiningLevel(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        // 获取玩家
        PlayerEntity player = net.minecraft.client.MinecraftClient.getInstance().player;
        if (player != null && player.hasStatusEffect(WIStatusEffects.MAX_MINING)) {
            cir.setReturnValue(true);
        }
    }
}