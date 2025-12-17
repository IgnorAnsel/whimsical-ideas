package com.ignoransel.whimsicalideas.content.soulsail;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
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

        if (!nbt.contains(SoulSailKeys.ROOM_X) || !nbt.contains(SoulSailKeys.ROOM_Z)) {
            // 简单：用玩家 UUID hash 分配房间中心点（避免多人重叠）
            UUID id = UUID.fromString(getOrCreateSailId(sail));
            int hx = id.hashCode();
            int roomSize = 256; // 房间间距
            int cx = (hx & 1023) * roomSize;
            int cz = ((hx >>> 10) & 1023) * roomSize;

            nbt.putInt(SoulSailKeys.ROOM_X, cx);
            nbt.putInt(SoulSailKeys.ROOM_Z, cz);
        }

        int cx = nbt.getInt(SoulSailKeys.ROOM_X);
        int cz = nbt.getInt(SoulSailKeys.ROOM_Z);
        int r = tier.roomRadius;
        int y = w.getBottomY() + 80; // 固定高度，简单稳定

        // 建地板
        for (int x = cx - r; x <= cx + r; x++) {
            for (int z = cz - r; z <= cz + r; z++) {
                w.setBlockState(new BlockPos(x, y, z), Blocks.SMOOTH_STONE.getDefaultState(), 3);
//                w.setBlockState(new BlockPos(x, y + 1, z), Blocks.AIR.getDefaultState(), 3);
//                w.setBlockState(new BlockPos(x, y + 2, z), Blocks.AIR.getDefaultState(), 3);
            }
        }

        // 围边界（Barrier）
        int wallH = 6;
        for (int x = cx - r; x <= cx + r; x++) {
            for (int dy = 0; dy <= wallH; dy++) {
                w.setBlockState(new BlockPos(x, y + dy, cz - r), Blocks.BARRIER.getDefaultState(), 3);
                w.setBlockState(new BlockPos(x, y + dy, cz + r), Blocks.BARRIER.getDefaultState(), 3);
            }
        }
        for (int z = cz - r; z <= cz + r; z++) {
            for (int dy = 0; dy <= wallH; dy++) {
                w.setBlockState(new BlockPos(cx - r, y + dy, z), Blocks.BARRIER.getDefaultState(), 3);
                w.setBlockState(new BlockPos(cx + r, y + dy, z), Blocks.BARRIER.getDefaultState(), 3);
            }
        }
    }

    public static void teleportIntoRoom(ServerWorld w, ServerPlayerEntity player, ItemStack sail, SoulSailTier tier) {
        var nbt = SoulSailItemCompat.data(sail);
        int cx = nbt.getInt(SoulSailKeys.ROOM_X);
        int cz = nbt.getInt(SoulSailKeys.ROOM_Z);
        int y = w.getBottomY() + 82;

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
