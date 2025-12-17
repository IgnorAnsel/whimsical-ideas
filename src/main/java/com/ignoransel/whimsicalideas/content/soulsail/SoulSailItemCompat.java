package com.ignoransel.whimsicalideas.content.soulsail;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;

public final class SoulSailItemCompat {
    private static final String BET = "BlockEntityTag";

    private SoulSailItemCompat() {}

    private static NbtCompound data(ItemStack stack) {
        return stack.getOrCreateSubNbt(BET); // 写入/读取都统一到 BlockEntityTag
    }

    public static long getSouls(ItemStack stack) {
        return data(stack).getLong(SoulSailKeys.SOULS);
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
}
