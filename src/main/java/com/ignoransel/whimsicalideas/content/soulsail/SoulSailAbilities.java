package com.ignoransel.whimsicalideas.content.soulsail;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class SoulSailAbilities {
    private SoulSailAbilities() {}

    /** 切换到下一个已解锁技能（按品阶过滤） */
    public static void cycleAbility(ServerPlayerEntity sp, ItemStack stack) {
        SoulBannerGrade grade = SoulSailItemCompat.getBannerGrade(stack);
        SoulSailAbility[] all = SoulSailAbility.values();
        int[] unlockedIdx = java.util.stream.IntStream.range(0, all.length)
                .filter(i -> all[i].unlockedBy(grade))
                .toArray();

        if (unlockedIdx.length == 0) {
            SoulSailItemCompat.setSelectedAbility(stack, SoulSailAbility.NONE.ordinal());
            sp.sendMessage(Text.literal("当前无可用术式").formatted(Formatting.GRAY), true);
            return;
        }

        int cur = SoulSailItemCompat.getSelectedAbility(stack);

        // 找当前在 unlocked 列表中的位置；找不到就从第一个开始
        int pos = -1;
        for (int i = 0; i < unlockedIdx.length; i++) {
            if (unlockedIdx[i] == cur) { pos = i; break; }
        }
        int nextPos = (pos == -1) ? 0 : (pos + 1) % unlockedIdx.length;
        int next = unlockedIdx[nextPos];

        SoulSailItemCompat.setSelectedAbility(stack, next);
        sp.sendMessage(Text.literal("术式切换为: " + all[next].displayName).formatted(Formatting.AQUA), true);
    }

    /** 释放当前选择的技能（只在按键触发时消耗魂） */
    public static void castSelectedAbility(ServerPlayerEntity sp, ItemStack stack) {
        SoulBannerGrade grade = SoulSailItemCompat.getBannerGrade(stack);
        SoulSailAbility ab = SoulSailItemCompat.getSelectedAbilitySafe(stack);

        // 选择的技能不满足品阶，就回退为 NONE
        if (!ab.unlockedBy(grade)) {
            SoulSailItemCompat.setSelectedAbility(stack, SoulSailAbility.NONE.ordinal());
            sp.sendMessage(Text.literal("该术式未解锁").formatted(Formatting.RED), true);
            return;
        }

        long now = sp.age;
        if (ab.cooldownTicks > 0 && SoulSailItemCompat.isAbilityOnCooldown(stack, ab, now)) {
            return;
        }
        if (ab.passive) {
            switch (ab) {
                case SOUL_TOTEM -> {
                    boolean on = SoulSailItemCompat.toggleSoulTotem(stack);
                    sp.sendMessage(Text.literal("魂替: " + (on ? "开启" : "关闭"))
                            .formatted(on ? Formatting.GREEN : Formatting.DARK_GRAY), true);
                }
                case SOUL_BARRIER -> {
                    boolean on = SoulSailItemCompat.toggleSoulBarrier(stack);
                    sp.sendMessage(Text.literal("魂御: " + (on ? "开启" : "关闭"))
                            .formatted(on ? Formatting.GREEN : Formatting.DARK_GRAY), true);
                }
                default -> {}
            }
            return;
        }
        switch (ab) {
            case NONE -> sp.sendMessage(Text.literal("当前无术式").formatted(Formatting.GRAY), true);

            case HEAL -> {
                if (sp.getHealth() >= sp.getMaxHealth()) {
                    sp.sendMessage(Text.literal("生命已满").formatted(Formatting.GRAY), true);
                    return;
                }
                if (!consumeAndCooldown(sp, stack, ab)) return;

                float heal = 2.0f; // 1颗心
                sp.heal(heal);
                sp.sendMessage(Text.literal("回春 +" + (heal / 2f) + "❤").formatted(Formatting.GREEN), true);
            }

        }
    }

    /** 统一：扣魂 + 设置物品CD */
    private static boolean consumeAndCooldown(ServerPlayerEntity sp, ItemStack stack, SoulSailAbility ab) {
        if (ab.costSouls > 0 && !SoulSailItemCompat.spendRefinedSouls(stack, ab.costSouls)) {
            sp.sendMessage(Text.literal("魂魄不足 (-" + ab.costSouls + "魂)").formatted(Formatting.RED), true);
            return false;
        }

        long now = sp.age;
        SoulSailItemCompat.setAbilityCooldown(stack, ab, now, ab.cooldownTicks);
        return true;
    }

}
