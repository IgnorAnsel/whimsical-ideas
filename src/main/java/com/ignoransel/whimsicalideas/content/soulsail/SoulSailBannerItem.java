package com.ignoransel.whimsicalideas.content.soulsail;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

import java.util.List;

public class SoulSailBannerItem extends BannerItem {
    private final SoulSailTier tier;

    public SoulSailBannerItem(Block standingBlock, Block wallBlock, Settings settings, SoulSailTier tier) {
        super(standingBlock, wallBlock, settings);
        this.tier = tier;
    }

    public SoulSailTier tier() { return tier; }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }

    // 右键空气：开始“使用”（长按）
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    // 长按完成：进入/离开魂幡世界
    @Override
    public ItemStack finishUsing(ItemStack stack, World world, net.minecraft.entity.LivingEntity user) {
        if (!world.isClient && user instanceof ServerPlayerEntity sp) {
            boolean inSoulWorld = sp.getServerWorld().getRegistryKey().equals(SoulSailRoomManager.SOUL_SAIL_DIM);

            // 魂幡世界：潜行长按 -> 回去
            if (sp.isSneaking() && inSoulWorld) {
                SoulSailRoomManager.teleportBack(sp, stack);
                return stack;
            }

            // 外界：长按 -> 记录返回点并进入
            if (!inSoulWorld) {
                SoulSailRoomManager.storeReturnPoint(sp, stack);

                ServerWorld target = sp.getServer().getWorld(SoulSailRoomManager.SOUL_SAIL_DIM);
                if (target != null) {
                    SoulSailRoomManager.ensureRoomBuilt(target, sp, stack, tier);
                    SoulSailRoomManager.teleportIntoRoom(target, sp, stack, tier);
                    SoulSailRoomManager.spawnPendingMobsOnce(target, stack, tier);
                    SoulSailRoomManager.applyPacifistRules(target, sp);
                }
            }
        }
        return stack;
    }

    // Tooltip 显示魂数
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        long souls = SoulSailItemCompat.getSouls(stack);
        tooltip.add(Text.literal("魂魄: " + souls));
        long rawsouls = SoulSailItemCompat.getRawSouls(stack);
        tooltip.add(Text.literal("未炼化: " + rawsouls));
        long refinedsouls = SoulSailItemCompat.getRefinedSouls(stack);
        tooltip.add(Text.literal("已炼化: " + refinedsouls));
//        int pending = SoulSailItemCompat.getPendingCount(stack);
//        tooltip.add(Text.literal("待收容生物: " + pending));
    }
}
