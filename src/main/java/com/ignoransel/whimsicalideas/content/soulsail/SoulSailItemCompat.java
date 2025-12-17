package com.ignoransel.whimsicalideas.content.soulsail;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

import java.util.UUID;

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
        return bet; // 写入/读取都统一到 BlockEntityTag
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
}
