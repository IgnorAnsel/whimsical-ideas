package com.ignoransel.whimsicalideas.content.Viewfinder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ProjectionFrameItem extends Item {

    // 采样分辨率 (15x15 射线)，数值越高越精细但越卡
    private static final int RESOLUTION = 15;
    // 最大取景距离
    private static final int MAX_DISTANCE = 64;
    // 放置时的前方偏移距离
    private static final double PLACE_DISTANCE = 5.0;

    public ProjectionFrameItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (world.isClient) return TypedActionResult.success(stack);

        NbtCompound nbt = stack.getOrCreateNbt();
        if (nbt.contains("CapturedBlocks")) {
            // 如果已经有照片，则执行“放置”
            reify(world, user, stack);
            nbt.remove("CapturedBlocks");
            user.sendMessage(Text.literal("现实已被改写").formatted(Formatting.AQUA), true);
        } else {
            // 如果是空的，则执行“取景”
            capture(world, user, stack);
            user.sendMessage(Text.literal("空间已拓印").formatted(Formatting.GOLD), true);
        }

        return TypedActionResult.success(stack);
    }

    /**
     * 取景逻辑：发射阵列射线，记录相对于玩家视角的相对坐标
     */
    private void capture(World world, PlayerEntity player, ItemStack stack) {
        NbtList blockList = new NbtList();
        Vec3d eyePos = player.getEyePos();

        // 获取玩家当前的基向量（正前方、右侧、上方）
        Vec3d forward = player.getRotationVec(1.0F).normalize();
        Vec3d right = forward.crossProduct(new Vec3d(0, 1, 0)).normalize();
        if (right.lengthSquared() < 0.01) right = new Vec3d(1, 0, 0); // 防止垂直看向天空时出错
        Vec3d up = right.crossProduct(forward).normalize();

        // 扫描一个网格
        for (int x = -RESOLUTION; x <= RESOLUTION; x++) {
            for (int y = -RESOLUTION; y <= RESOLUTION; y++) {
                // 计算射线的偏移方向：稍微偏向右侧 x 距离，上方 y 距离
                // 这里 0.1 是展开系数，决定取景框的大小
                Vec3d rayDir = forward.add(right.multiply(x * 0.08)).add(up.multiply(y * 0.08)).normalize();
                Vec3d endPos = eyePos.add(rayDir.multiply(MAX_DISTANCE));

                BlockHitResult hit = world.raycast(new RaycastContext(
                        eyePos, endPos, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player
                ));

                if (hit.getType() == HitResult.Type.BLOCK) {
                    BlockPos pos = hit.getBlockPos();
                    BlockState state = world.getBlockState(pos);
                    if (state.isAir()) continue;

                    // 计算该点相对于玩家眼睛的局部坐标
                    Vec3d hitVec = hit.getPos();
                    Vec3d relative = hitVec.subtract(eyePos);

                    // 将世界相对坐标投影到玩家的三个基向量上 (点积)
                    // 这样我们记录的就是：右移多少，上移多少，前移多少
                    double localRight = relative.dotProduct(right);
                    double localUp = relative.dotProduct(up);
                    double localForward = relative.dotProduct(forward);

                    NbtCompound tag = new NbtCompound();
                    tag.putDouble("r", localRight);
                    tag.putDouble("u", localUp);
                    tag.putDouble("f", localForward);
                    tag.put("state", NbtHelper.fromBlockState(state));
                    blockList.add(tag);
                }
            }
        }
        stack.getOrCreateNbt().put("CapturedBlocks", blockList);
    }

    /**
     * 放置逻辑：将记录的局部坐标映射到玩家当前的新视野中
     */
    private void reify(World world, PlayerEntity player, ItemStack stack) {
        NbtList blockList = stack.getNbt().getList("CapturedBlocks", NbtElement.COMPOUND_TYPE);
        Vec3d eyePos = player.getEyePos();

        // 获取当前的新基向量
        Vec3d forward = player.getRotationVec(1.0F).normalize();
        Vec3d right = forward.crossProduct(new Vec3d(0, 1, 0)).normalize();
        if (right.lengthSquared() < 0.01) right = new Vec3d(1, 0, 0);
        Vec3d up = right.crossProduct(forward).normalize();

        for (int i = 0; i < blockList.size(); i++) {
            NbtCompound tag = blockList.getCompound(i);
            double r = tag.getDouble("r");
            double u = tag.getDouble("u");
            double f = tag.getDouble("f");
            BlockState state = NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), tag.getCompound("state"));

            // 将局部坐标重新还原为当前的世界坐标
            // 新坐标 = 眼睛位置 + (新右向量 * 之前的右偏移) + (新上向量 * 之前的上偏移) + (新前向量 * 之前的前偏移)
            Vec3d newWorldVec = eyePos
                    .add(right.multiply(r))
                    .add(up.multiply(u))
                    .add(forward.multiply(f));

            BlockPos targetPos = BlockPos.ofFloored(newWorldVec);

            if (world.isInBuildLimit(targetPos)) {
                // 替换方块
                world.setBlockState(targetPos, state, Block.NOTIFY_ALL);
            }
        }
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return stack.hasNbt() && stack.getNbt().contains("CapturedBlocks");
    }
}