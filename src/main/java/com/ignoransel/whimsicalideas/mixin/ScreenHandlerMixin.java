package com.ignoransel.whimsicalideas.mixin;

import com.ignoransel.whimsicalideas.content.soulsail.SoulSailActive;
import com.ignoransel.whimsicalideas.content.soulsail.SoulSailBannerItem;
import com.ignoransel.whimsicalideas.content.soulsail.SoulSailRoomManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {
    @Shadow public DefaultedList<Slot> slots;
    @Shadow public abstract ItemStack getCursorStack();

    @Inject(method = "onSlotClick", at = @At("HEAD"), cancellable = true)
    private void wi$kickOutIfMoveSoulSail(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (!(player instanceof ServerPlayerEntity sp)) return;
        if (!sp.getServerWorld().getRegistryKey().equals(SoulSailRoomManager.SOUL_SAIL_DIM)) return;

        // 1) 光标上的物品
        ItemStack cursor = this.getCursorStack();
        ItemStack sail = isSoulSail(cursor) ? cursor : ItemStack.EMPTY;

        // 2) 被点击的槽位里的物品
        if (sail.isEmpty() && slotIndex >= 0 && slotIndex < this.slots.size()) {
            ItemStack inSlot = this.slots.get(slotIndex).getStack();
            if (isSoulSail(inSlot)) sail = inSlot;
        }

        // 3) 热键交换（SWAP）时，button=0..8 表示热键栏格子
        if (sail.isEmpty() && actionType == SlotActionType.SWAP && button >= 0 && button < 9) {
            ItemStack hotbar = sp.getInventory().getStack(button);
            if (isSoulSail(hotbar)) sail = hotbar;
        }

        if (!sail.isEmpty() && SoulSailActive.isActiveSail(sp, sail)) {
            // 取消这次搬运，直接踢回外界
            ci.cancel();
            sp.closeHandledScreen();
            SoulSailRoomManager.teleportBack(sp, sail);
        }
    }

    private static boolean isSoulSail(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof SoulSailBannerItem;
    }
}
