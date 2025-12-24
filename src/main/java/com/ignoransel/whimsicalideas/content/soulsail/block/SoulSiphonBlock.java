package com.ignoransel.whimsicalideas.content.soulsail.block;

import com.ignoransel.whimsicalideas.content.soulsail.SoulBannerGrade;
import com.ignoransel.whimsicalideas.content.soulsail.SoulSailBannerItem;

import com.ignoransel.whimsicalideas.content.soulsail.SoulSailItemCompat;
import com.ignoransel.whimsicalideas.content.soulsail.entity.SoulSiphonBlockEntity;
import com.ignoransel.whimsicalideas.registry.WIBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SoulSiphonBlock extends BlockWithEntity implements BlockEntityProvider {
    public static final BooleanProperty ACTIVE = BooleanProperty.of("active");
    public SoulSiphonBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(ACTIVE, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SoulSiphonBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) return null;
        return type == WIBlockEntities.SOUL_SIPHON ? (w, p, s, be) -> SoulSiphonBlockEntity.tick(w, p, s, (SoulSiphonBlockEntity) be) : null;
    }


    @Override
    public ActionResult onUse(BlockState state, World world, net.minecraft.util.math.BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof SoulSiphonBlockEntity siphon)) return ActionResult.PASS;

        ItemStack held = player.getStackInHand(hand);

        // 右键：放入魂幡
        if (!held.isEmpty() && held.getItem() instanceof SoulSailBannerItem) {
            ItemStack remain = siphon.tryInsertBanner(held);
            player.setStackInHand(hand, remain);
            // siphon.syncNow();
            world.playSound(null, pos, SoundEvents.BLOCK_ENDER_CHEST_OPEN, SoundCategory.BLOCKS, 0.7f, 1.2f);
            return ActionResult.CONSUME;
        }

        // 空手/非魂幡：取出魂幡
        ItemStack out = siphon.tryExtractBanner();
        if (!out.isEmpty()) {
            SoulSailItemCompat.setBannerGrade(out, getNextGrade(SoulSailItemCompat.getBannerGrade(out), SoulSailItemCompat.getRefinedSouls(out)));
            if (!player.getInventory().insertStack(out)) {
                player.dropItem(out, false);
            }
            // siphon.syncNow();
            world.playSound(null, pos, SoundEvents.BLOCK_ENDER_CHEST_CLOSE, SoundCategory.BLOCKS, 0.7f, 1.2f);
            return ActionResult.CONSUME;
        }

        return ActionResult.PASS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, net.minecraft.util.math.BlockPos pos,
                                BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof SoulSiphonBlockEntity siphon) {
                siphon.dropContents(world, pos);
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
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

}
