package com.ignoransel.whimsicalideas.content.soultablet;

import com.ignoransel.whimsicalideas.util.NbtKeys;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class SoulTabletItem extends Item {
    private static final int USE_TICKS = 32;
    private static final float BIND_DAMAGE = 2.0f;

    private final Block standing;
    private final Block wall;

    public SoulTabletItem(Block standing, Block wall, Settings settings) {
        super(settings);
        this.standing = standing;
        this.wall = wall;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return USE_TICKS;
    }

    // 对空使用：绑定
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        boolean openSky = world.isSkyVisible(user.getBlockPos().up());
        boolean lookingUp = user.getPitch() < -60f;

        if (!openSky || !lookingUp) {
            if (!world.isClient) user.sendMessage(Text.literal("需要在露天仰望天空，才能祭炼魂牌。"), true);
            return TypedActionResult.fail(stack);
        }

        user.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!(user instanceof PlayerEntity player)) return stack;

        if (!world.isClient) {
            player.damage(world.getDamageSources().magic(), BIND_DAMAGE);

            NbtCompound nbt = stack.getOrCreateNbt();
            nbt.putUuid(NbtKeys.OWNER_UUID, player.getUuid());
            nbt.putString(NbtKeys.OWNER_NAME, player.getGameProfile().getName());

            stack.setCustomName(Text.literal("魂牌（" + player.getGameProfile().getName() + "）"));
            world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                    player.getSoundCategory(), 0.8f, 1.2f);
        }
        return stack;
    }

    // 对方块使用：放置（插地/挂墙）
    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        World world = ctx.getWorld();
        PlayerEntity player = ctx.getPlayer();
        ItemStack stack = ctx.getStack();

        ItemPlacementContext ipc = new ItemPlacementContext(ctx);
        BlockPos placePos = ipc.getBlockPos();
        Direction side = ctx.getSide();

        BlockState placeState;
        if (side.getAxis().isHorizontal()) {
            placeState = wall.getPlacementState(ipc);
        } else {
            placeState = standing.getPlacementState(ipc);
        }

        if (placeState == null) return ActionResult.FAIL;

        if (!world.setBlockState(placePos, placeState, 3)) return ActionResult.FAIL;

        // 写入 BE（绑定信息落地）
        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(placePos);
            if (be instanceof SoulTabletBlockEntity tablet) {
                tablet.applyFromItem(stack);
            }
        }

//        // 播放放置音
//        SoundGroup sg = placeState.getSoundGroup();
//        world.playSound(player, placePos, sg.getPlaceSound(), SoundCategory.BLOCKS,
//                (sg.getVolume() + 1.0F) / 2.0F, sg.getPitch() * 0.8F);

        if (player == null || !player.getAbilities().creativeMode) {
            stack.decrement(1);
        }

        return ActionResult.success(world.isClient);
    }
}
