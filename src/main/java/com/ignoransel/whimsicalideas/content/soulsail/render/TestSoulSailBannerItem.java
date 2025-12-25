package com.ignoransel.whimsicalideas.content.soulsail.render;

import com.ignoransel.whimsicalideas.content.soulsail.*;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.block.Block;

import java.util.List;

public class TestSoulSailBannerItem extends BlockItem implements ISoulSailItem {
    private SoulSailTier tier;

    public TestSoulSailBannerItem(Block standingBlock, Settings settings, SoulSailTier tier) {
        super(standingBlock, settings);
        this.tier = tier;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (!world.isClient && world.getRegistryKey().equals(SoulSailRoomManager.SOUL_SAIL_DIM)) {
            if (context.getPlayer() instanceof ServerPlayerEntity sp) {
                if (SoulSailActive.isActiveSail(sp, context.getStack())) {
                    SoulSailRoomManager.teleportBack(sp, context.getStack());
                    return ActionResult.FAIL;
                }
            }
        }
        return super.useOnBlock(context);
    }
    @Override
    public SoulSailTier tier() { return tier; }
    public void setTier(SoulSailTier soulSailTier) {
        this.tier = soulSailTier;
    }
    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }

    // 右键空气：开始“使用”（长按）
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (user.isSneaking()) {
            user.setCurrentHand(hand);
            return TypedActionResult.consume(stack);
        }

        if (!world.isClient && user instanceof ServerPlayerEntity sp) {
            SoulSailAbilities.castSelectedAbility(sp, stack);
            user.stopUsingItem();
            return TypedActionResult.success(stack);
        }
        user.stopUsingItem();
        return TypedActionResult.pass(stack);
    }

    // 长按完成：进入/离开魂幡世界
    @Override
    public ItemStack finishUsing(ItemStack stack, World world, net.minecraft.entity.LivingEntity user) {
        if (!world.isClient && user instanceof ServerPlayerEntity sp) {
            boolean inSoulWorld = sp.getServerWorld().getRegistryKey().equals(SoulSailRoomManager.SOUL_SAIL_DIM);

            // 魂幡世界：潜行长按 -> 回去
            if (sp.isSneaking() && inSoulWorld && SoulSailActive.isActiveSail(sp, stack)) {
                SoulSailActive.clearActive(sp);
                SoulSailRoomManager.teleportBack(sp, stack);
                return stack;
            }

            // 外界：长按 -> 记录返回点并进入
            if (!inSoulWorld) {
                SoulSailActive.setActive(sp, stack);
                SoulSailItemCompat.setActive(stack, true);
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

    private static SoulBannerGrade getNextGrade(SoulBannerGrade currentGrade, long refinedSouls) {
        SoulBannerGrade g = currentGrade;

        while (g != SoulBannerGrade.IMMORTAL) {
            long requiredSouls = (long) Math.pow(10, g.getLevel() + 1);
            if (refinedSouls < requiredSouls) break;

            g = switch (g) {
                case MORTAL -> SoulBannerGrade.EARTH;
                case EARTH -> SoulBannerGrade.HEAVEN;
                case HEAVEN -> SoulBannerGrade.MYSTERIOUS;
                case MYSTERIOUS -> SoulBannerGrade.YELLOW;
                case YELLOW -> SoulBannerGrade.UNIVERSE;
                case UNIVERSE -> SoulBannerGrade.COSMOS;
                case COSMOS -> SoulBannerGrade.FLOOD;
                case FLOOD -> SoulBannerGrade.WASTELAND;
                case WASTELAND -> SoulBannerGrade.IMMORTAL;
                default -> g;
            };
        }

        return g;
    }

    // Tooltip 显示信息
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {

        SoulBannerGrade grade = SoulSailItemCompat.getBannerGrade(stack);
        long rawsouls = SoulSailItemCompat.getRawSouls(stack);
        long souls = SoulSailItemCompat.getSouls(stack);
        long refinedsouls = SoulSailItemCompat.getRefinedSouls(stack);

        SoulBannerGrade Nextgrade = getNextGrade(grade, refinedsouls);
        if(Nextgrade != grade) {
            SoulSailItemCompat.setBannerGrade(stack, Nextgrade);
            setTier(grade.getSoulSailTier());
            grade = Nextgrade;
        }
        tooltip.add(Text.literal("品阶: ").formatted(Formatting.GRAY)
                .append(Text.literal(grade.getDisplayName()).formatted(grade.getTooltipFormatting(), Formatting.BOLD)));

        tooltip.add(Text.literal("魂魄: " + souls));

        tooltip.add(Text.literal("未炼化: " + rawsouls));

        tooltip.add(Text.literal("已炼化: " + refinedsouls));
        SoulSailAbility ab = SoulSailItemCompat.getSelectedAbilitySafe(stack);
        tooltip.add(Text.literal("当前术式: " + ab.displayName).formatted(Formatting.AQUA));
        boolean on = SoulSailItemCompat.isSoulTotemEnabled(stack);
        tooltip.add(Text.literal("魂替状态: " + (on ? "开启" : "关闭"))
                .formatted(on ? Formatting.GREEN : Formatting.DARK_GRAY));
        on = SoulSailItemCompat.isSoulBarrierEnabled(stack);
        tooltip.add(Text.literal("魂御状态: " + (on ? "开启" : "关闭"))
                .formatted(on ? Formatting.GREEN : Formatting.DARK_GRAY));
        if (ab != SoulSailAbility.NONE) {
            tooltip.add(Text.literal("消耗: " + ab.costSouls + "魂  冷却: " + (ab.cooldownTicks / 20f) + "s")
                    .formatted(Formatting.DARK_GRAY));
        }
        if (SoulSailItemCompat.isActive(stack)) {
            tooltip.add(Text.literal("位于此魂幡（已锁定）"));
        } else {
            tooltip.remove(Text.literal("位于此魂幡（已锁定）"));
        }
//        int pending = SoulSailItemCompat.getPendingCount(stack);
//        tooltip.add(Text.literal("待收容生物: " + pending));
    }
}
