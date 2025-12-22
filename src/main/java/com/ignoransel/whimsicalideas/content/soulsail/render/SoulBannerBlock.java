package com.ignoransel.whimsicalideas.content.soulsail.render;

import com.ignoransel.whimsicalideas.entity.SoulBannerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class SoulBannerBlock extends BlockWithEntity {
    public static final IntProperty ROTATION = Properties.ROTATION;

    public SoulBannerBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(ROTATION, 0));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ROTATION);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        int rot = MathHelper.floor((180.0F + ctx.getPlayerYaw()) * 16.0F / 360.0F + 0.5D) & 15;
        return this.getDefaultState().with(ROTATION, rot);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SoulBannerBlockEntity(pos, state);
    }
}
