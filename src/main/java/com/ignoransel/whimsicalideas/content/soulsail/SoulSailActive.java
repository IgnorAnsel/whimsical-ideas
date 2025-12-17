package com.ignoransel.whimsicalideas.content.soulsail;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;

public final class SoulSailActive {
    private SoulSailActive() {}

    public static final String PREFIX = "wi:active_sail:";

    public static void setActive(ServerPlayerEntity sp, ItemStack sail) {
        String id = SoulSailItemCompat.getOrCreateSailId(sail);
        clearActive(sp);
        sp.addCommandTag(PREFIX + id); // :contentReference[oaicite:1]{index=1}
    }

    public static void clearActive(ServerPlayerEntity sp) {
        // 避免遍历 Set 时直接 remove 导致并发修改
        for (String t : new ArrayList<>(sp.getCommandTags())) { // :contentReference[oaicite:2]{index=2}
            if (t.startsWith(PREFIX)) {
                sp.removeScoreboardTag(t); // :contentReference[oaicite:3]{index=3}
            }
        }
    }

    public static String getActiveId(ServerPlayerEntity sp) {
        for (String t : sp.getCommandTags()) { // :contentReference[oaicite:4]{index=4}
            if (t.startsWith(PREFIX)) return t.substring(PREFIX.length());
        }
        return null;
    }

    public static boolean isActiveSail(ServerPlayerEntity sp, ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof SoulSailBannerItem)) return false;
        String active = getActiveId(sp);
        if (active == null || active.isEmpty()) return false;

        String id = SoulSailItemCompat.data(stack).getString(SoulSailKeys.SAIL_ID);
        return active.equals(id);
    }
}
