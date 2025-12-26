// TuningForkBlockEntity.java
package com.ignoransel.whimsicalideas.content.tuningfork;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class TuningForkBlockEntity extends BlockEntity {
    private int activeTicks = 0;

    public TuningForkBlockEntity(BlockPos pos, BlockState state) {
        super(com.ignoransel.whimsicalideas.registry.WIBlockEntities.TUNING_FORK_BE, pos, state);
    }

    public void setActiveTicks(int ticks) {
        this.activeTicks = Math.max(ticks, 0);
        markDirty();
    }

    public int getActiveTicks() {
        return activeTicks;
    }

    public static void tickServer(net.minecraft.world.World world, BlockPos pos, BlockState state, TuningForkBlockEntity be) {
        if (be.activeTicks <= 0) return;

        be.activeTicks--;
        if (be.activeTicks == 0) {
            // 归零红石
            if (state.get(TuningForkBlock.POWER) != 0) {
                world.setBlockState(pos, state.with(TuningForkBlock.POWER, 0), Block.NOTIFY_ALL);
                world.updateNeighborsAlways(pos, state.getBlock());
                world.updateNeighborsAlways(pos.down(), state.getBlock());
                world.updateNeighborsAlways(pos.up(), state.getBlock());

            }
            be.markDirty();
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("ActiveTicks", activeTicks);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        activeTicks = nbt.getInt("ActiveTicks");
    }
}
