package com.ignoransel.whimsicalideas.event;

import com.ignoransel.whimsicalideas.registry.WIStatusEffects;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.PickaxeItem;
import net.minecraft.registry.tag.BlockTags;

public class MaxMiningEvents {

    public static void register() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, entity) -> {
            // 1️⃣ 只有玩家有神稿状态才处理
            if (!player.hasStatusEffect(WIStatusEffects.MAX_MINING)) {
                return false; // false = 不取消，原版逻辑
            }

            // 2️⃣ 只影响镐工具
            if (!(player.getMainHandStack().getItem() instanceof PickaxeItem)) {
                return false; // 不是镐工具，按原版逻辑
            }

            // 3️⃣ 对需要钻石工具的方块，允许破坏
            if (state.isIn(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return false; // false = 不取消 → 允许挖掘
            }

            // 4️⃣ 其他方块按原版逻辑
            return false; // false = 不取消
        });
    }
}
