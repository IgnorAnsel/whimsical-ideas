package com.ignoransel.whimsicalideas.content.soulsail;

import com.ignoransel.whimsicalideas.mixin.ExperienceOrbEntityAccessor;
import com.ignoransel.whimsicalideas.registry.WIEntities;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LightningEntity;
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
        // 一个生物 = 一个经验球（这里 mobId 你目前没用，但 pop 仍然是消耗列表）
        String mobId = data.wi$popOneStoredMob();
        if (mobId == null) return false;

        data.wi$setRawSouls(data.wi$getRawSouls() - 1);
        data.wi$setRefinedSouls(data.wi$getRefinedSouls() + 1);
        data.wi$syncTotal();
        data.wi$markDirtyAndSync();
        boolean upgraded = checkAndUpgradeGrade(w, data, bannerPos);

        // ===== A) 当前维度：生成原版经验球（可吸）=====
        double x = bannerPos.getX() + 0.5;
        double y = bannerPos.getY() + 1.2;
        double z = bannerPos.getZ() + 0.5;

        ExperienceOrbEntity overworldOrb = new ExperienceOrbEntity(w, x, y, z, 1);
        w.spawnEntity(overworldOrb);

        // ===== B) 魂帆世界：生成自定义淡蓝经验球（不可吸）=====
        ServerWorld soulWorld = w.getServer().getWorld(SoulSailRoomManager.SOUL_SAIL_DIM);
        if (soulWorld != null) {
            int cx = data.wi$getRoomX();
            int cz = data.wi$getRoomZ();
            int sy = soulWorld.getBottomY() + 82;

            double sx = cx + 0.5 + (soulWorld.random.nextDouble() - 0.5) * 0.8;
            double sz = cz + 0.5 + (soulWorld.random.nextDouble() - 0.5) * 0.8;

            SoulXpOrbEntity soulOrb = new SoulXpOrbEntity(WIEntities.SOUL_XP_ORB, soulWorld);
            System.out.println("Spawned: " + net.minecraft.registry.Registries.ENTITY_TYPE.getId(soulOrb.getType()));

            soulOrb.refreshPositionAndAngles(sx, sy + 0.2, sz, 0, 0);
            ((ExperienceOrbEntityAccessor) soulOrb).wi$setAmount(1);
            soulWorld.spawnEntity(soulOrb);
        }

        return true;
    }
    private static boolean checkAndUpgradeGrade(ServerWorld world, SoulSailBannerData data, BlockPos pos) {
        SoulBannerGrade currentGrade = data.wi$getBannerGrade();
        long refinedSouls = data.wi$getRefinedSouls();

        // 根据品阶的阈值来判断是否需要升级
        SoulBannerGrade nextGrade = getNextGrade(currentGrade, refinedSouls);
        if (nextGrade != currentGrade) {
            // 升级品阶
            data.wi$setBannerGrade(nextGrade);
            System.out.println("Upgraded to: " + nextGrade.name());

            // 触发雷击
            spawnLightning(world, pos);
            return true;
        }

        return false;
    }
    private static void spawnLightning(ServerWorld world, BlockPos pos) {
        LightningEntity bolt = EntityType.LIGHTNING_BOLT.create(world);
        if (bolt != null) {
            bolt.refreshPositionAfterTeleport(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            world.spawnEntity(bolt);
        }
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


    public static SoulSailBannerData getSoulSailBannerData(ServerWorld w, BlockPos pos) {
        BlockEntity be = w.getBlockEntity(pos);
        if (be instanceof SoulSailBannerData d && d.wi$isSoulSailBanner()) return d;
        return null;
    }
}
