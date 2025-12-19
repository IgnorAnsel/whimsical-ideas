package com.ignoransel.whimsicalideas.content.soulsail;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

import java.util.List;

public class SoulSailBannerItem extends BannerItem implements ISoulSailItem {
    private final SoulSailTier tier;

    public SoulSailBannerItem(Block standingBlock, Block wallBlock, Settings settings, SoulSailTier tier) {
        super(standingBlock, wallBlock, settings);
        this.tier = tier;
    }

    public SoulSailBannerItem(Block standingBlock, Settings settings, SoulSailTier tier) {
        super(standingBlock, null, settings);
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
            if (sp.isSneaking() && inSoulWorld && SoulSailActive.isActiveSail(sp, stack)) {
                SoulSailActive.clearActive(sp);
                SoulSailItemCompat.setActive(stack, false);
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
    private Formatting getGradeFormatting(SoulBannerGrade grade) {
        return switch (grade) {
            case MORTAL -> Formatting.GRAY;
            case EARTH -> Formatting.DARK_GREEN;
            case HEAVEN -> Formatting.BLUE;
            case MYSTERIOUS -> Formatting.DARK_PURPLE;
            case YELLOW -> Formatting.YELLOW;
            case UNIVERSE -> Formatting.DARK_BLUE;
            case COSMOS -> Formatting.AQUA;
            case FLOOD -> Formatting.LIGHT_PURPLE;
            case WASTELAND -> Formatting.GOLD;
            case IMMORTAL -> Formatting.RED;
        };
    }
    private static SoulBannerGrade getNextGrade(SoulBannerGrade currentGrade, long refinedSouls) {
        // 计算每个品阶的提升条件（每10个已炼化的魂魄对应一个品阶）
        int requiredSouls = (currentGrade.getLevel() + 1) * 10;  // 当前品阶 + 1 后的魂魄数量

        // 如果已经炼化的魂魄超过了要求，返回下一个品阶
        if (refinedSouls >= requiredSouls) {
            switch (currentGrade) {
                case MORTAL: return SoulBannerGrade.EARTH;
                case EARTH: return SoulBannerGrade.HEAVEN;
                case HEAVEN: return SoulBannerGrade.MYSTERIOUS;
                case MYSTERIOUS: return SoulBannerGrade.YELLOW;
                case YELLOW: return SoulBannerGrade.UNIVERSE;
                case UNIVERSE: return SoulBannerGrade.COSMOS;
                case COSMOS: return SoulBannerGrade.FLOOD;
                case FLOOD: return SoulBannerGrade.WASTELAND;
                case WASTELAND: return SoulBannerGrade.IMMORTAL;
                default: return currentGrade;  // 达到最高品阶时不再升级
            }
        }

        return currentGrade;  // 如果未达到要求，保持当前品阶
    }
    // Tooltip 显示信息
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {

        SoulBannerGrade grade = SoulSailItemCompat.getBannerGrade(stack);
        long rawsouls = SoulSailItemCompat.getRawSouls(stack);
        long souls = SoulSailItemCompat.getSouls(stack);
        long refinedsouls = SoulSailItemCompat.getRefinedSouls(stack);

        SoulBannerGrade Nextgrade = getNextGrade(grade, refinedsouls);
        if(Nextgrade != grade) {
            SoulSailItemCompat.setBannerGrade(stack, Nextgrade);
            grade = Nextgrade;
        }
        Formatting gradeColor = getGradeFormatting(grade);
        tooltip.add(Text.literal("品阶: ").formatted(Formatting.GRAY)
                .append(Text.literal(grade.getDisplayName()).formatted(gradeColor, Formatting.BOLD)));

        tooltip.add(Text.literal("魂魄: " + souls));

        tooltip.add(Text.literal("未炼化: " + rawsouls));

        tooltip.add(Text.literal("已炼化: " + refinedsouls));
        if (SoulSailItemCompat.isActive(stack)) {
            tooltip.add(Text.literal("位于此魂幡（已锁定）"));
        } else {
            tooltip.remove(Text.literal("位于此魂幡（已锁定）"));
        }
//        int pending = SoulSailItemCompat.getPendingCount(stack);
//        tooltip.add(Text.literal("待收容生物: " + pending));
    }
}
