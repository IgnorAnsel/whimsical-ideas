package com.ignoransel.whimsicalideas.content.soulsail;

import com.ignoransel.whimsicalideas.mixin.ExperienceOrbEntityAccessor;
import com.ignoransel.whimsicalideas.registry.WIEntities;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public final class SoulSailRefine {
    private SoulSailRefine() {}

    // basePos -> cooldown
    private static final Map<ServerWorld, Map<BlockPos, Integer>> RUNNING = new HashMap<>();

    public static void register() {
        // 右键催化体开始炼化
        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            if (world.isClient) return ActionResult.PASS;
            if (!(world instanceof ServerWorld sw)) return ActionResult.PASS;

            BlockPos base = hit.getBlockPos();
            if (!sw.getBlockState(base).isOf(Blocks.SCULK_CATALYST)) return ActionResult.PASS;
            if (!player.getStackInHand(hand).isOf(Items.ECHO_SHARD)) return ActionResult.PASS;

            BlockPos bannerPos = base.up();
            SoulSailBannerData data = getSoulSailBannerData(sw, bannerPos);
            if (data == null) return ActionResult.PASS;

            if (!checkRitual(sw, base)) return ActionResult.PASS;

            RUNNING.computeIfAbsent(sw, k -> new HashMap<>()).put(base.toImmutable(), 0);
            return ActionResult.SUCCESS;
        });

        // tick：推进炼化
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            Map<BlockPos, Integer> map = RUNNING.get(world);
            if (map == null || map.isEmpty()) return;

            List<BlockPos> keys = new ArrayList<>(map.keySet());
            for (BlockPos base : keys) {
                if (!world.getBlockState(base).isOf(Blocks.SCULK_CATALYST) || !checkRitual(world, base)) {
                    map.remove(base);
                    continue;
                }

                int cd = map.getOrDefault(base, 0) + 1;

                // 每 10 tick 炼化 1 个
                if (cd >= 10) {
                    cd = 0;
                    boolean ok = refineOnce((ServerWorld) world, base);
                    if (!ok) {
                        map.remove(base);
                        continue;
                    }
                }

                map.put(base, cd);
            }
        });
    }

    private static boolean checkRitual(ServerWorld w, BlockPos base) {
        return w.getBlockState(base.east()).isOf(Blocks.SOUL_LANTERN)
                && w.getBlockState(base.west()).isOf(Blocks.SOUL_LANTERN)
                && w.getBlockState(base.north()).isOf(Blocks.SOUL_LANTERN)
                && w.getBlockState(base.south()).isOf(Blocks.SOUL_LANTERN);
    }

    private static boolean refineOnce(ServerWorld w, BlockPos base) {
        BlockPos bannerPos = base.up();
        SoulSailBannerData data = getSoulSailBannerData(w, bannerPos);
        if (data == null) return false;

        // 必须有待炼化生物 && RawSouls > 0
        if (data.wi$getStoredCount() <= 0) return false;
        if (data.wi$getRawSouls() <= 0) return false;

        // 一个生物 = 一个经验球
        String mobId = data.wi$popOneStoredMob();
        if (mobId == null) return false;

        data.wi$setRawSouls(data.wi$getRawSouls() - 1);
        data.wi$setRefinedSouls(data.wi$getRefinedSouls() + 1);
        data.wi$syncTotal();
        data.wi$markDirtyAndSync();

        // 生成自定义经验球（不可吸取）
        double x = bannerPos.getX() + 0.5;
        double y = bannerPos.getY() + 1.2;
        double z = bannerPos.getZ() + 0.5;

        SoulXpOrbEntity orb = new SoulXpOrbEntity(WIEntities.SOUL_XP_ORB, w);
        orb.refreshPositionAndAngles(x, y, z, 0, 0);

        // 设置经验量为 1（一个生物=一个球=1）
        ((ExperienceOrbEntityAccessor) orb).wi$setAmount(1);

        w.spawnEntity(orb);
        return true;
    }

    private static SoulSailBannerData getSoulSailBannerData(ServerWorld w, BlockPos pos) {
        BlockEntity be = w.getBlockEntity(pos);
        if (be instanceof SoulSailBannerData d && d.wi$isSoulSailBanner()) return d;
        return null;
    }
}
