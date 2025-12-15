package com.ignoransel.whimsicalideas.content.hex.handers;

import com.ignoransel.whimsicalideas.registry.WIStatusEffects;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public final class HexRefineHandler {

    private HexRefineHandler() {}

    public static void register() {
        PlayerBlockBreakEvents.BEFORE.register(HexRefineHandler::beforeBreak);
    }
    private static boolean isOre(BlockState state) {
        return state.isIn(BlockTags.IRON_ORES)
                || state.isIn(BlockTags.GOLD_ORES)
                || state.isIn(BlockTags.COPPER_ORES)
                || state.isIn(BlockTags.DIAMOND_ORES)
                || state.isIn(BlockTags.REDSTONE_ORES)
                || state.isIn(BlockTags.LAPIS_ORES)
                || state.isIn(BlockTags.COAL_ORES)
                || state.isIn(BlockTags.EMERALD_ORES);
    }

    private static boolean beforeBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        // 只在服务端处理
        if (world.isClient) return true;
        if (!(world instanceof ServerWorld serverWorld)) return true;

        // 必须有 REFINE 状态
        if (!player.hasStatusEffect(WIStatusEffects.REFINE)) return true;

        if (!isOre(state)) return true;

        // 拿到工具（用于计算 Fortune / Silk Touch 掉落）
        ItemStack tool = player.getMainHandStack();

        // 计算原始掉落（已经包含附魔影响）
        List<ItemStack> drops = Block.getDroppedStacks(state, serverWorld, pos, blockEntity, player, tool);

        // 先把方块挖掉，但不掉落原物品
        // world.breakBlock(pos, false) 会移除方块且不掉落
        world.breakBlock(pos, false, player);

        // 工具耐久扣 1（你想不扣也行）
        if (!tool.isEmpty() && tool.isDamageable()) {
            tool.damage(1, player, p -> p.sendToolBreakStatus(Hand.MAIN_HAND));
        }

        // 将掉落物“自动熔炼”
        for (ItemStack stack : drops) {
            ItemStack out = trySmelt(serverWorld, stack);
            Block.dropStack(serverWorld, pos, out);
        }

        // 给个声音反馈（可选）
        serverWorld.playSound(null, pos, SoundEvents.BLOCK_BLASTFURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 0.25f, 1.2f);

        // 返回 false：表示我们已经处理了破坏，不让原逻辑继续（避免重复掉落）
        return false;
    }

    private static ItemStack trySmelt(ServerWorld world, ItemStack in) {
        if (in.isEmpty()) return in;

        // 以“每种掉落物”为输入找熔炉配方（比如 raw_iron -> iron_ingot）
        Optional<SmeltingRecipe> match = world.getRecipeManager()
                .getFirstMatch(RecipeType.SMELTING, new SimpleInventory(in), world);

        if (match.isEmpty()) return in;

        ItemStack result = match.get().getOutput(world.getRegistryManager()).copy();
        if (result.isEmpty()) return in;

        // 按数量换算（1 raw -> 1 ingot 这种）
        result.setCount(result.getCount() * in.getCount());
        return result;
    }
}
