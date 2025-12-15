package com.ignoransel.whimsicalideas.content.soultablet;

import com.ignoransel.whimsicalideas.registry.WIItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class SoulTabletBlock extends BlockWithEntity {
    public static final IntProperty ROTATION = Properties.ROTATION;

    private static final VoxelShape OUTLINE = Block.createCuboidShape(3, 0, 3, 13, 16, 13);

    public SoulTabletBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(ROTATION, 0));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL; // ✅ 必须：让 block model json 生效
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos below = pos.down();
        BlockState belowState = world.getBlockState(below);
        return belowState.isSideSolidFullSquare(world, below, net.minecraft.util.math.Direction.UP);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        if (ctx.getPlayer() == null) return getDefaultState();
        float yaw = ctx.getPlayer().getYaw();
        int rot = MathHelper.floor((yaw * 16.0F / 360.0F) + 0.5D) & 15;
        return getDefaultState().with(ROTATION, rot);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ROTATION);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SoulTabletBlockEntity(pos, state);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        ItemStack stack = new ItemStack(WIItems.SOUL_TABLET_ITEM);
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof SoulTabletBlockEntity tablet) {
            tablet.writeOwnerToStack(stack);
        }
        return stack;
    }
}
