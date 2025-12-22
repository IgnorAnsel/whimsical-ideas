package com.ignoransel.whimsicalideas.content.soulsail;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;

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

        switch (ab) {
            case NONE -> sp.sendMessage(Text.literal("当前无术式").formatted(Formatting.GRAY), true);

            case HEAL -> {
                // 地阶：用魂回血（你可自己调整数值）
                if (grade.getLevel() < SoulBannerGrade.EARTH.getLevel()) return;

                if (sp.getHealth() >= sp.getMaxHealth()) {
                    sp.sendMessage(Text.literal("生命已满").formatted(Formatting.GRAY), true);
                    return;
                }

                // 防连点：给物品CD（2秒）
                if (sp.getItemCooldownManager().isCoolingDown(stack.getItem())) return;

                long cost = 200L;  // 消耗魂
                float heal = 2.0f; // +1颗心

                if (!SoulSailItemCompat.spendRefinedSouls(stack, cost)) {
                    sp.sendMessage(Text.literal("魂魄不足").formatted(Formatting.RED), true);
                    return;
                }

                sp.heal(heal);
                sp.getItemCooldownManager().set(stack.getItem(), 40);

                sp.sendMessage(
                        Text.literal("回春 +" + (heal / 2f) + "❤  (-" + cost + "魂)")
                                .formatted(Formatting.GREEN),
                        true
                );
            }
        }
    }
}
