package com.ignoransel.whimsicalideas.content.soulsail;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;

import static com.ignoransel.whimsicalideas.content.soulsail.SoulSailKeys.ABILITY_CDS;

public final class SoulSailItemCompat {
    private static final String BET = "BlockEntityTag";

    private SoulSailItemCompat() {}

    public static NbtCompound data(ItemStack stack) {
        var root = stack.getOrCreateNbt();
        var bet  = stack.getOrCreateSubNbt(BET);

        // 旧版本迁移：以前写在根 tag 的，搬到 BlockEntityTag（只搬一次）
        migrateLong(root, bet, SoulSailKeys.SOULS);
        migrateList(root, bet, SoulSailKeys.STORED, NbtElement.STRING_TYPE);

        migrateInt(root, bet, SoulSailKeys.ROOM_X);
        migrateInt(root, bet, SoulSailKeys.ROOM_Z);
        migrateString(root, bet, SoulSailKeys.RETURN_DIM);
        migrateString(root, bet, SoulSailKeys.SAIL_ID);
        migrateDouble(root, bet, SoulSailKeys.RETURN_X);
        migrateDouble(root, bet, SoulSailKeys.RETURN_Y);
        migrateDouble(root, bet, SoulSailKeys.RETURN_Z);
        migrateFloat(root, bet, SoulSailKeys.RETURN_YAW);
        migrateFloat(root, bet, SoulSailKeys.RETURN_PITCH);
        migrateBool(root, bet, SoulSailKeys.ACTIVE);
        migrateInt(root, bet, SoulSailKeys.BANNER_GRADE);
        migrateInt(root, bet, SoulSailKeys.LAST_RADIUS);
        migrateInt(root, bet, SoulSailKeys.SELECTED_ABILITY);
        migrateCompound(root, bet, ABILITY_CDS);
        migrateBool(root, bet, SoulSailKeys.PASSIVE_SOUL_TOTEM);
        migrateBool(root, bet, SoulSailKeys.PASSIVE_SOUL_BARRIER);
        migrateBool(root, bet, SoulSailKeys.PASSIVE_SOUL_DOMAIN);
        return bet; // 写入/读取都统一到 BlockEntityTag
    }


    // 技能
    // SoulSailItemCompat
    public static boolean isSoulTotemEnabled(ItemStack stack) {
        return data(stack).getBoolean(SoulSailKeys.PASSIVE_SOUL_TOTEM);
    }
    public static void setSoulTotemEnabled(ItemStack stack, boolean enabled) {
        data(stack).putBoolean(SoulSailKeys.PASSIVE_SOUL_TOTEM, enabled);
    }
    public static boolean toggleSoulTotem(ItemStack stack) {
        boolean next = !isSoulTotemEnabled(stack);
        setSoulTotemEnabled(stack, next);
        return next;
    }

    public static boolean isSoulBarrierEnabled(ItemStack stack) {
        return data(stack).getBoolean(SoulSailKeys.PASSIVE_SOUL_BARRIER);
    }

    public static void setSoulBarrierEnabled(ItemStack stack, boolean enabled) {
        data(stack).putBoolean(SoulSailKeys.PASSIVE_SOUL_BARRIER, enabled);
    }

    public static boolean toggleSoulBarrier(ItemStack stack) {
        boolean next = !isSoulBarrierEnabled(stack);
        setSoulBarrierEnabled(stack, next);
        return next;
    }

    public static boolean isAbilityOnCooldown(ItemStack stack, SoulSailAbility ab, long nowTick) {
        NbtCompound nbt = data(stack);
        NbtCompound cds = nbt.getCompound(ABILITY_CDS);
        long until = cds.getLong(ab.name());
        return until > nowTick;
    }

    public static void setAbilityCooldown(ItemStack stack, SoulSailAbility ab, long nowTick, int cooldownTicks) {
        if (cooldownTicks <= 0) return;
        NbtCompound nbt = data(stack);
        NbtCompound cds = nbt.getCompound(ABILITY_CDS);
        cds.putLong(ab.name(), nowTick + cooldownTicks);
        nbt.put(ABILITY_CDS, cds);
    }

    public static boolean isSoulDomainEnabled(ItemStack stack) {
        return data(stack).getBoolean(SoulSailKeys.PASSIVE_SOUL_DOMAIN);
    }
    public static void setSoulDomainEnabled(ItemStack stack, boolean enabled) {
        data(stack).putBoolean(SoulSailKeys.PASSIVE_SOUL_DOMAIN, enabled);
    }
    public static boolean toggleSoulDomain(ItemStack stack) {
        boolean next = !isSoulDomainEnabled(stack);
        setSoulDomainEnabled(stack, next);
        return next;
    }


    private static NbtCompound getAbilityCds(ItemStack stack) {
        NbtCompound nbt = data(stack);
        if (!nbt.contains(SoulSailKeys.ABILITY_CDS, NbtElement.COMPOUND_TYPE)) {
            nbt.put(SoulSailKeys.ABILITY_CDS, new NbtCompound());
        }
        return nbt.getCompound(SoulSailKeys.ABILITY_CDS);
    }


    public static int getSelectedAbility(ItemStack stack) {
        return data(stack).getInt(SoulSailKeys.SELECTED_ABILITY);
    }
    public static void setSelectedAbility(ItemStack stack, int idx) {
        data(stack).putInt(SoulSailKeys.SELECTED_ABILITY, idx);
    }
    public static SoulSailAbility getSelectedAbilitySafe(ItemStack stack) {
        int idx = getSelectedAbility(stack);
        SoulSailAbility[] vals = SoulSailAbility.values();
        if (idx < 0 || idx >= vals.length) return SoulSailAbility.NONE;
        return vals[idx];
    }



    public static String getOrCreateSailId(ItemStack stack) {
        var nbt = data(stack); // BlockEntityTag
        if (!nbt.contains(SoulSailKeys.SAIL_ID)) {
            nbt.putString(SoulSailKeys.SAIL_ID, UUID.randomUUID().toString());
            System.out.println("Sail ID generated: " + nbt.getString(SoulSailKeys.SAIL_ID));
        }
        else {
            System.out.println("Sail ID found: " + nbt.getString(SoulSailKeys.SAIL_ID));
        }
        return nbt.getString(SoulSailKeys.SAIL_ID);
    }

    public static void setLastRadius(ItemStack stack, int radius) {
        var nbt = data(stack);
        nbt.putInt(SoulSailKeys.LAST_RADIUS, radius);
    }

    public static int getLastRadius(ItemStack stack) {
        var nbt = data(stack);
        return nbt.getInt(SoulSailKeys.LAST_RADIUS);
    }

    public static void setActive(ItemStack stack, boolean active) {
        data(stack).putBoolean(SoulSailKeys.ACTIVE, active);
    }

    public static boolean isActive(ItemStack stack) {
        return data(stack).getBoolean(SoulSailKeys.ACTIVE);
    }

    public static SoulBannerGrade getBannerGrade(ItemStack stack) {
        var nbt = data(stack);
        if (!nbt.contains(SoulSailKeys.BANNER_GRADE)) {
            return SoulBannerGrade.MORTAL;
        }
        int level = nbt.getInt(SoulSailKeys.BANNER_GRADE);
        return SoulBannerGrade.byLevel(level);
    }

    public static void setBannerGrade(ItemStack stack, SoulBannerGrade grade) {
        var nbt = data(stack);
        nbt.putInt(SoulSailKeys.BANNER_GRADE, grade.getLevel());
    }
    // ----------------- Souls / Stored -----------------
    public static long getRawSouls(ItemStack stack) {
        var nbt = data(stack);
        return nbt.getLong(SoulSailKeys.RAW_SOULS);
    }

    public static long getRefinedSouls(ItemStack stack) {
        var nbt = data(stack);
        return nbt.getLong(SoulSailKeys.REFINED_SOULS);
    }

    public static long getSouls(ItemStack stack) {
        var nbt = data(stack);
        return nbt.getLong(SoulSailKeys.RAW_SOULS) + nbt.getLong(SoulSailKeys.REFINED_SOULS);
    }

    private static void syncTotal(NbtCompound nbt) {
        long raw = Math.max(0L, nbt.getLong(SoulSailKeys.RAW_SOULS));
        long refined = Math.max(0L, nbt.getLong(SoulSailKeys.REFINED_SOULS));
        nbt.putLong(SoulSailKeys.SOULS, raw + refined);
    }

    public static void addRawSouls(ItemStack stack, long amount, long cap) {
        var nbt = data(stack);
        long raw = nbt.getLong(SoulSailKeys.RAW_SOULS);
        nbt.putLong(SoulSailKeys.RAW_SOULS, Math.max(0L, raw + amount));
    }

    public static boolean spendRefinedSoulsFx(ServerPlayerEntity sp, ItemStack stack, long amount) {
        boolean ok = spendRefinedSouls(stack, amount);

        if (sp.getWorld() instanceof ServerWorld sw) {
            if (ok) {
                sw.spawnParticles(
                        ParticleTypes.SOUL,                // 也可换 SOUL_FIRE_FLAME 更亮
                        sp.getX(), sp.getBodyY(0.5), sp.getZ(),
                        20,                                  // 数量
                        0.25, 0.20, 0.25,                   // 扩散
                        0.01                                // 速度
                );
            } else {
                sw.spawnParticles(
                        ParticleTypes.SMOKE,
                        sp.getX(), sp.getBodyY(0.5), sp.getZ(),
                        6,
                        0.15, 0.15, 0.15,
                        0.01
                );
            }
        }

        return ok;
    }

    public static boolean spendRefinedSouls(ItemStack stack, long amount) {
        var nbt = data(stack);
        long refined = nbt.getLong(SoulSailKeys.REFINED_SOULS);
        if (refined >= amount) {
            nbt.putLong(SoulSailKeys.REFINED_SOULS, Math.max(0L, refined - amount));
            return true;
            }
        return false;
    }

    public static void addRefinedSouls(ItemStack stack, long amount, long cap) {
        var nbt = data(stack);
        long refined = nbt.getLong(SoulSailKeys.REFINED_SOULS);
        nbt.putLong(SoulSailKeys.REFINED_SOULS, Math.max(0L, refined + amount));
    }

    public static void addSouls(ItemStack stack, long amount, long cap) {
        var nbt = data(stack);
        long cur = nbt.getLong(SoulSailKeys.SOULS);
        long next = Math.min(cap, Math.max(0, cur + amount));
        nbt.putLong(SoulSailKeys.SOULS, next);
    }

    public static void addPendingMob(ItemStack stack, String entityId) {
        var nbt = data(stack);
        var list = nbt.getList(SoulSailKeys.STORED, NbtElement.STRING_TYPE);
        list.add(NbtString.of(entityId));
        nbt.put(SoulSailKeys.STORED, list);
    }

    public static int getPendingCount(ItemStack stack) {
        return data(stack).getList(SoulSailKeys.STORED, NbtElement.STRING_TYPE).size();
    }

    public static NbtList getPendingList(ItemStack stack) {
        return data(stack).getList(SoulSailKeys.STORED, NbtElement.STRING_TYPE);
    }

    public static void clearPending(ItemStack stack) {
        data(stack).put(SoulSailKeys.STORED, new NbtList());
    }

    // ----------------- Room coords -----------------
    public static boolean hasRoom(ItemStack stack) {
        var nbt = data(stack);
        return nbt.contains(SoulSailKeys.ROOM_X) && nbt.contains(SoulSailKeys.ROOM_Z);
    }

    public static int getRoomX(ItemStack stack) { return data(stack).getInt(SoulSailKeys.ROOM_X); }
    public static int getRoomZ(ItemStack stack) { return data(stack).getInt(SoulSailKeys.ROOM_Z); }
    public static void setRoom(ItemStack stack, int x, int z) {
        var nbt = data(stack);
        nbt.putInt(SoulSailKeys.ROOM_X, x);
        nbt.putInt(SoulSailKeys.ROOM_Z, z);
    }


    // ----------------- Return point -----------------
    public static void setReturn(ItemStack stack, String dim, double x, double y, double z, float yaw, float pitch) {
        var nbt = data(stack);
        nbt.putString(SoulSailKeys.RETURN_DIM, dim);
        nbt.putDouble(SoulSailKeys.RETURN_X, x);
        nbt.putDouble(SoulSailKeys.RETURN_Y, y);
        nbt.putDouble(SoulSailKeys.RETURN_Z, z);
        nbt.putFloat(SoulSailKeys.RETURN_YAW, yaw);
        nbt.putFloat(SoulSailKeys.RETURN_PITCH, pitch);
    }

    // ----------------- Migration helpers -----------------
    private static void migrateLong(NbtCompound from, NbtCompound to, String key) {
        if (from.contains(key) && !to.contains(key)) { to.putLong(key, from.getLong(key)); from.remove(key); }
    }
    private static void migrateInt(NbtCompound from, NbtCompound to, String key) {
        if (from.contains(key) && !to.contains(key)) { to.putInt(key, from.getInt(key)); from.remove(key); }
    }
    private static void migrateBool(NbtCompound from, NbtCompound to, String key) {
        if (from.contains(key) && !to.contains(key)) { to.putBoolean(key, from.getBoolean(key)); from.remove(key); }
    }
    private static void migrateDouble(NbtCompound from, NbtCompound to, String key) {
        if (from.contains(key) && !to.contains(key)) { to.putDouble(key, from.getDouble(key)); from.remove(key); }
    }
    private static void migrateFloat(NbtCompound from, NbtCompound to, String key) {
        if (from.contains(key) && !to.contains(key)) { to.putFloat(key, from.getFloat(key)); from.remove(key); }
    }
    private static void migrateString(NbtCompound from, NbtCompound to, String key) {
        if (from.contains(key) && !to.contains(key)) { to.putString(key, from.getString(key)); from.remove(key); }
    }
    private static void migrateList(NbtCompound from, NbtCompound to, String key, int elementType) {
        if (from.contains(key) && !to.contains(key)) {
            to.put(key, from.getList(key, elementType));
            from.remove(key);
        }
    }
    private static void migrateCompound(NbtCompound from, NbtCompound to, String key) {
        if (from.contains(key, NbtElement.COMPOUND_TYPE) && !to.contains(key, NbtElement.COMPOUND_TYPE)) {
            to.put(key, from.getCompound(key));
            from.remove(key);
        }
    }


    public static ItemStack findSoulSail(ServerPlayerEntity sp) {
        ItemStack main = sp.getMainHandStack();
        if (main.getItem() instanceof SoulSailBannerItem) return main;

        ItemStack off = sp.getOffHandStack();
        if (off.getItem() instanceof SoulSailBannerItem) return off;

        return ItemStack.EMPTY;
    }
}
