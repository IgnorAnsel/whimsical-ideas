package com.ignoransel.whimsicalideas.content.soultablet;

import com.ignoransel.whimsicalideas.registry.WIItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class SoulTabletWallBlock extends BlockWithEntity {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    private static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(3, 0, 14, 13, 16, 16); // 贴北墙(方块在墙南侧)
    private static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(3, 0, 0,  13, 16, 2);
    private static final VoxelShape WEST_SHAPE  = Block.createCuboidShape(14,0, 3,  16, 16, 13);
    private static final VoxelShape EAST_SHAPE  = Block.createCuboidShape(0, 0, 3,  2,  16, 13);


    public SoulTabletWallBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST  -> WEST_SHAPE;
            case EAST  -> EAST_SHAPE;
            default -> SOUTH_SHAPE;
        };
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction facing = state.get(FACING);
        BlockPos supportPos = pos.offset(facing.getOpposite());
        BlockState support = world.getBlockState(supportPos);
        return support.isSideSolidFullSquare(world, supportPos, facing);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction side = ctx.getSide();
        if (!side.getAxis().isHorizontal()) return null;
        return getDefaultState().with(FACING, side);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
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
