package com.ignoransel.whimsicalideas.content.soulsail;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;

public final class SoulSailItemCompat {
    private SoulSailItemCompat() {}

    public static long getSouls(ItemStack stack) {
        return stack.getOrCreateNbt().getLong(SoulSailKeys.SOULS);
    }

    public static void addSouls(ItemStack stack, long amount, long cap) {
        var nbt = stack.getOrCreateNbt();
        long cur = nbt.getLong(SoulSailKeys.SOULS);
        long next = Math.min(cap, Math.max(0, cur + amount));
        nbt.putLong(SoulSailKeys.SOULS, next);
    }

    public static void addPendingMob(ItemStack stack, String entityId) {
        var nbt = stack.getOrCreateNbt();
        var list = nbt.getList(SoulSailKeys.STORED, NbtElement.STRING_TYPE);
        list.add(net.minecraft.nbt.NbtString.of(entityId));
        nbt.put(SoulSailKeys.STORED, list);
    }

    public static int getPendingCount(ItemStack stack) {
        return stack.getOrCreateNbt().getList(SoulSailKeys.STORED, NbtElement.STRING_TYPE).size();
    }
}
