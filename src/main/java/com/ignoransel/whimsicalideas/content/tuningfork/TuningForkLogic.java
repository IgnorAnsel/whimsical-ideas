// TuningForkLogic.java
package com.ignoransel.whimsicalideas.content.tuningfork;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class TuningForkLogic {
    private TuningForkLogic() {}

    public static void propagate(World world, BlockPos origin, Block baseBlock, int range, int durationTicks, int power) {
        int r = Math.max(1, range);
        int r2 = r * r;

        BlockPos.Mutable m = new BlockPos.Mutable();
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (dx*dx + dy*dy + dz*dz > r2) continue;

                    m.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);

                    BlockState s = world.getBlockState(m);
                    if (!(s.getBlock() instanceof TuningForkBlock)) continue;

                    BlockState below = world.getBlockState(m.down());
                    if (below.getBlock() != baseBlock) continue;

                    activateFork(world, m.toImmutable(), durationTicks, power);
                }
            }
        }
    }

    public static void activateFork(World world, BlockPos pos, int durationTicks, int power) {
        BlockState s = world.getBlockState(pos);
        if (!(s.getBlock() instanceof TuningForkBlock)) return;

        int p = clamp(power, 1, 15);
        int dur = Math.max(durationTicks, 1);

        // 设置红石强度
        if (s.get(TuningForkBlock.POWER) != p) {
            BlockState newState = s.with(TuningForkBlock.POWER, p);
            world.setBlockState(pos, newState, Block.NOTIFY_ALL);

            world.updateNeighborsAlways(pos, newState.getBlock());
            world.updateNeighborsAlways(pos.down(), newState.getBlock());   // 有些红石只吃下面更新
            world.updateNeighborsAlways(pos.up(), newState.getBlock());     // 保险起见

        }

        var be = world.getBlockEntity(pos);
        if (be instanceof TuningForkBlockEntity forkBe) {
            // 取更大的持续时间，避免被短的覆盖
            forkBe.setActiveTicks(Math.max(forkBe.getActiveTicks(), dur));
        }
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }
}
