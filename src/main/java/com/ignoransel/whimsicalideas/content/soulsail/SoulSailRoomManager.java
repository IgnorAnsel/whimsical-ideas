package com.ignoransel.whimsicalideas.content.soulsail;

import com.ignoransel.whimsicalideas.mixin.ExperienceOrbEntityAccessor;
import com.ignoransel.whimsicalideas.registry.WIEntities;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

import java.util.UUID;

import static com.ignoransel.whimsicalideas.content.soulsail.SoulSailItemCompat.getOrCreateSailId;

public final class SoulSailRoomManager {
    private SoulSailRoomManager() {}

    public static final net.minecraft.registry.RegistryKey<World> SOUL_SAIL_DIM =
            net.minecraft.registry.RegistryKey.of(RegistryKeys.WORLD, new Identifier("whimsical-ideas", "soul_sail"));

    public static void ensureRoomBuilt(ServerWorld w, ServerPlayerEntity player, ItemStack sail, SoulSailTier tier) {
        var nbt = SoulSailItemCompat.data(sail);

        //  初始化房间坐标
        if (!nbt.contains(SoulSailKeys.ROOM_X) || !nbt.contains(SoulSailKeys.ROOM_Z)) {
            UUID id = UUID.fromString(SoulSailItemCompat.getOrCreateSailId(sail));
            int roomSize = 1024;
            int hx = id.hashCode();
            int cx = (hx & 1023) * roomSize;
            int cz = ((hx >>> 10) & 1023) * roomSize;

            nbt.putInt(SoulSailKeys.ROOM_X, cx);
            nbt.putInt(SoulSailKeys.ROOM_Z, cz);
            nbt.putInt(SoulSailKeys.LAST_RADIUS, 0);
        }

        int cx = nbt.getInt(SoulSailKeys.ROOM_X);
        int cz = nbt.getInt(SoulSailKeys.ROOM_Z);
        int r = tier.roomRadius;

        int lastR = nbt.contains(SoulSailKeys.LAST_RADIUS) ? nbt.getInt(SoulSailKeys.LAST_RADIUS) : 0;
        System.out.println("Last R:" + lastR);
        int floorY = w.getBottomY() + 80; // 地板高度
        int minY = w.getBottomY();        // 世界最低点
        int maxY = w.getTopY();           // 世界最高点

        if (lastR > 0 && lastR != r) {
            removeFullHeightWallRing(w, cx, cz, lastR, minY, maxY);
        }

        for (int x = cx - r; x <= cx + r; x++) {
            for (int z = cz - r; z <= cz + r; z++) {
                BlockPos pos = new BlockPos(x, floorY, z);
                // 只有当是空气、旧屏障时才铺，防止覆盖玩家放的方块
                BlockState state = w.getBlockState(pos);
                if (state.isAir() || state.isOf(Blocks.BARRIER)) {
                    w.setBlockState(pos, Blocks.SMOOTH_STONE.getDefaultState(), 3);
                }
            }
        }

        buildFullHeightWallRing(w, cx, cz, r, minY, maxY, floorY);

        if (lastR != r) {
            System.out.println("存入"+r);
            nbt.putInt(SoulSailKeys.LAST_RADIUS, r);
        }
    }

    /**
     * 辅助：建造全高度屏障墙
     * @param floorY 地板高度，如果你希望墙壁在地板处也是屏障（把你围起来），或者地板处是石头
     * 这里逻辑是：墙壁外圈全是屏障，地板那是石头（在主逻辑里铺了），所以这里不需要特殊避开地板，
     * 因为墙壁是在 r 的边缘，而地板是在 r 内部。边缘通常也作为墙。
     */
    private static void buildFullHeightWallRing(ServerWorld w, int cx, int cz, int r, int minY, int maxY, int floorY) {
        // 遍历四条边
        for (int x = cx - r; x <= cx + r; x++) {
            fillColumn(w, x, cz - r, minY, maxY, floorY); // 北墙
            fillColumn(w, x, cz + r, minY, maxY, floorY); // 南墙
        }
        for (int z = cz - r; z <= cz + r; z++) {
            fillColumn(w, cx - r, z, minY, maxY, floorY); // 西墙
            fillColumn(w, cx + r, z, minY, maxY, floorY); // 东墙
        }
    }

    /**
     * 辅助：移除全高度屏障墙 (只移除 Barrier，不破坏其他方块)
     */
    private static void removeFullHeightWallRing(ServerWorld w, int cx, int cz, int r, int minY, int maxY) {
        for (int x = cx - r; x <= cx + r; x++) {
            clearColumn(w, x, cz - r, minY, maxY);
            clearColumn(w, x, cz + r, minY, maxY);
        }
        for (int z = cz - r; z <= cz + r; z++) {
            clearColumn(w, cx - r, z, minY, maxY);
            clearColumn(w, cx + r, z, minY, maxY);
        }
    }

    // 单列填充屏障
    private static void fillColumn(ServerWorld w, int x, int z, int minY, int maxY, int floorY) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(x, minY, z);
        BlockState barrier = Blocks.BARRIER.getDefaultState();

        for (int y = minY; y < maxY; y++) {
            mutable.setY(y);
            // 性能优化：只有当不是屏障时才设置，减少区块更新
            if (!w.getBlockState(mutable).isOf(Blocks.BARRIER)) {
                // 可选：如果 y == floorY，是否要强制设为屏障？
                // 通常边缘一圈是墙，地板在墙里面。所以边缘全设屏障没问题。
                w.setBlockState(mutable, barrier, 3);
            }
        }
    }

    // 单列清除屏障
    private static void clearColumn(ServerWorld w, int x, int z, int minY, int maxY) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(x, minY, z);
        BlockState air = Blocks.AIR.getDefaultState();

        for (int y = minY; y < maxY; y++) {
            mutable.setY(y);
            // 安全检查：只拆 Barrier，防止拆了玩家的建筑
            if (w.getBlockState(mutable).isOf(Blocks.BARRIER)) {
                w.setBlockState(mutable, air, 3);
            }
        }
    }
    private static long countExistingSoulOrbs(ServerWorld w, int cx, int cz, int y, SoulSailTier tier) {
        Box box = new Box(cx - tier.roomRadius, y - 20, cz - tier.roomRadius, cx + tier.roomRadius, y + 6, cz + tier.roomRadius);
        System.out.println("Querying box: " + box);  // 调试信息，确保查询范围正确
        var entities = w.getEntitiesByType(WIEntities.SOUL_XP_ORB, box, e -> true);
        System.out.println("Found " + entities.size() + " SoulXpOrbs.");
        for (Entity entity : entities) {
            System.out.println("SoulXpOrb at: " + entity.getBlockPos());
        }

        return entities.size();  // 返回找到的实体数量
    }

    private static void spawnMissingSoulOrbs(ServerWorld w, ItemStack sail, SoulSailTier tier, long missingOrbs) {
        int cx = SoulSailItemCompat.getRoomX(sail);
        int cz = SoulSailItemCompat.getRoomZ(sail);
        int y = w.getBottomY() + 82;

        long remainingOrbs = missingOrbs;
        while (remainingOrbs > 0) {

            SoulXpOrbEntity orb = new SoulXpOrbEntity(WIEntities.SOUL_XP_ORB, w);
            double posX = cx + 0.5 + (w.random.nextDouble() - 0.5) * 0.8;
            double posZ = cz + 0.5 + (w.random.nextDouble() - 0.5) * 0.8;
            double posY = y + 0.2;
            orb.refreshPositionAndAngles(posX, posY, posZ, 0, 0);
            ((ExperienceOrbEntityAccessor) orb).wi$setAmount(1);
//            if (w.spawnEntity(orb)) {
//                System.out.println("SoulXpOrbEntity successfully spawned.");
//            } else {
//                System.out.println("Failed to spawn SoulXpOrbEntity at: " + posX + ", " + posY + ", " + posZ);
//            }
            remainingOrbs -= 1;
        }
    }
    public static void teleportIntoRoom(ServerWorld w, ServerPlayerEntity player, ItemStack sail, SoulSailTier tier) {
        var nbt = SoulSailItemCompat.data(sail);
        int cx = nbt.getInt(SoulSailKeys.ROOM_X);
        int cz = nbt.getInt(SoulSailKeys.ROOM_Z);
        int y = w.getBottomY() + 82;
        // 计算当前已经生成的 SoulXpOrbEntity 数量
        long currentOrbs = countExistingSoulOrbs(w, cx, cz, y, tier);
        System.out.println("currentOrbs: " + currentOrbs);
        // 计算需要生成的 SoulXpOrbEntity 数量
        long missingOrbs = Math.max(0, SoulSailItemCompat.getRefinedSouls(sail) - currentOrbs);
        System.out.println("missingOrbs: " + missingOrbs);
        // 如果有缺失的魂，补充生成
        if (missingOrbs > 0) {
            spawnMissingSoulOrbs(w, sail, tier, missingOrbs);
        }
        long afterSpawnOrbs = countExistingSoulOrbs(w, cx, cz, y, tier);
        System.out.println("生成后魂数量: " + afterSpawnOrbs);
        // FabricDimensions 传送
        net.fabricmc.fabric.api.dimension.v1.FabricDimensions.teleport(player, w, new TeleportTarget(
                new Vec3d(cx + 0.5, y, cz + 0.5),
                Vec3d.ZERO,
                player.getYaw(),
                player.getPitch()
        ));


    }
    public static void spawnPendingMobsOnce(ServerWorld w, ItemStack sail, SoulSailTier tier) {
        var nbt = SoulSailItemCompat.data(sail);
        NbtList pending = nbt.getList(SoulSailKeys.STORED, NbtElement.STRING_TYPE);
        if (pending.isEmpty()) return;

        int cx = nbt.getInt(SoulSailKeys.ROOM_X);
        int cz = nbt.getInt(SoulSailKeys.ROOM_Z);
        int y = w.getBottomY() + 82;

        int r = tier.roomRadius - 2;
        int count = pending.size();

        for (int i = 0; i < count; i++) {
            String id = pending.getString(i);
            EntityType<?> type = net.minecraft.registry.Registries.ENTITY_TYPE.get(new Identifier(id));
            Entity e = type.create(w);
            if (e == null) continue;

            // 简单散布：一圈圈摆放
            double px = cx + 0.5 + (i % 8) * 1.5;
            double pz = cz + 0.5 + (i / 8) * 1.5;
            px = Math.min(cx + r, Math.max(cx - r, px));
            pz = Math.min(cz + r, Math.max(cz - r, pz));

            e.refreshPositionAndAngles(px, y, pz, 0, 0);
            String sailId = getOrCreateSailId(sail);
            e.addCommandTag("wi:soul");
            e.addCommandTag("wi:sail:" + sailId);


            // 小世界生物：无攻击欲望
            if (e instanceof net.minecraft.entity.mob.MobEntity mob) {
                mob.setAiDisabled(true);
                mob.setPersistent();
            }

            w.spawnEntity(e);
        }

        // 清空“待生成”，避免下次进入重复生成；实体本身会留在房间里（持久化）
        nbt.put(SoulSailKeys.STORED, new NbtList());
    }

    public static void spawnStoredMobs(ServerWorld w, ServerPlayerEntity player, ItemStack sail, SoulSailTier tier) {
        var nbt = SoulSailItemCompat.data(sail);
        NbtList list = nbt.getList(SoulSailKeys.STORED, NbtElement.STRING_TYPE);
        if (list.isEmpty()) return;

        int cx = nbt.getInt(SoulSailKeys.ROOM_X);
        int cz = nbt.getInt(SoulSailKeys.ROOM_Z);
        int y = w.getBottomY() + 82;

        // 简单策略：把列表里的生物“全部生成一次”，并在生成后把列表清空（避免每次进来无限重复刷）
        for (int i = 0; i < list.size(); i++) {
            String id = list.getString(i);
            EntityType<?> type = net.minecraft.registry.Registries.ENTITY_TYPE.get(new Identifier(id));
            var e = type.create(w);
            if (e == null) continue;

            e.refreshPositionAndAngles(cx + 0.5 + (i % 6) * 1.5, y, cz + 0.5 + (i / 6) * 1.5, 0, 0);

            // 生物不攻击：直接 NoAI
            if (e instanceof net.minecraft.entity.mob.MobEntity mob) {
                mob.setAiDisabled(true);
                mob.setPersistent();
            }

            w.spawnEntity(e);
        }

        // 清空收容列表（你也可以改成只生成一部分、或生成“幻影副本”）
        nbt.put(SoulSailKeys.STORED, new NbtList());
    }

    public static void applyPacifistRules(ServerWorld w, ServerPlayerEntity player) {
//        // 兜底：给玩家极强抗性/回血（真正“无敌”我们在事件里直接取消伤害）
//        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20 * 60, 10, true, false));
//        player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 20 * 60, 10, true, false));
//        player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 20 * 60, 10, true, false));
    }

    public static void storeReturnPoint(ServerPlayerEntity player, ItemStack sail) {
        var nbt = SoulSailItemCompat.data(sail);
        var w = player.getServerWorld();

        nbt.putString(SoulSailKeys.RETURN_DIM, w.getRegistryKey().getValue().toString());
        nbt.putDouble(SoulSailKeys.RETURN_X, player.getX());
        nbt.putDouble(SoulSailKeys.RETURN_Y, player.getY());
        nbt.putDouble(SoulSailKeys.RETURN_Z, player.getZ());
        nbt.putFloat(SoulSailKeys.RETURN_YAW, player.getYaw());
        nbt.putFloat(SoulSailKeys.RETURN_PITCH, player.getPitch());
    }

    public static void teleportBack(ServerPlayerEntity player, ItemStack sail) {
        var nbt = SoulSailItemCompat.data(sail);
        if (!nbt.contains(SoulSailKeys.RETURN_DIM)) {
            // 没有记录就回主世界出生点附近（兜底）
            var overworld = player.getServer().getWorld(World.OVERWORLD);
            if (overworld != null) {
                FabricDimensions.teleport(player, overworld,
                        new TeleportTarget(
                                overworld.getSpawnPos().toCenterPos(),
                                Vec3d.ZERO,
                                player.getYaw(),
                                player.getPitch()
                        ));
            }
            return;
        }

        Identifier dimId = new Identifier(nbt.getString(SoulSailKeys.RETURN_DIM));
        var key = net.minecraft.registry.RegistryKey.of(RegistryKeys.WORLD, dimId);
        ServerWorld target = player.getServer().getWorld(key);
        if (target == null) return;

        double x = nbt.getDouble(SoulSailKeys.RETURN_X);
        double y = nbt.getDouble(SoulSailKeys.RETURN_Y);
        double z = nbt.getDouble(SoulSailKeys.RETURN_Z);
        float yaw = nbt.getFloat(SoulSailKeys.RETURN_YAW);
        float pitch = nbt.getFloat(SoulSailKeys.RETURN_PITCH);

        FabricDimensions.teleport(player, target,
                new TeleportTarget(
                        new Vec3d(x, y, z),
                        Vec3d.ZERO,
                        yaw,
                        pitch
                ));
    }


}
