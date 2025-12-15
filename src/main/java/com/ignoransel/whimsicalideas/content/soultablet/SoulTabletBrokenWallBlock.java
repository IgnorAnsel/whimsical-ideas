package com.ignoransel.whimsicalideas.content.soultablet;

import com.ignoransel.whimsicalideas.registry.WIBlockEntities;
import com.ignoransel.whimsicalideas.util.SoulTabletBlockEntityTicker;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.WoodType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SoulTabletBrokenWallBlock extends WallSignBlock {
    public SoulTabletBrokenWallBlock(Settings settings) {
        super(settings, WoodType.OAK);
        this.setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(WATERLOGGED, false));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SoulTabletBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) return null;
        return type == WIBlockEntities.SOUL_TABLET_BE ? (w, p, s, be) -> {
            if (be instanceof SoulTabletBlockEntity tablet) SoulTabletBlockEntityTicker.tickServer((ServerWorld) w, tablet);
        } : null;
    }
}

