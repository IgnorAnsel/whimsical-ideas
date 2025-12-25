package com.ignoransel.whimsicalideas.content.soulsail;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public final class SoulSailGradePassiveTick {
    private SoulSailGradePassiveTick() {}

    // 固定UUID：全品阶共用同一个生命上限modifier，避免叠加
    private static final UUID SOULSAIL_MAX_HP_UUID =
            UUID.fromString("7b8b7c5d-8a1f-4f73-9b4e-52f8f6c42e2a");

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(SoulSailGradePassiveTick::onTick);
    }

    private static void onTick(MinecraftServer server) {
        for (ServerPlayerEntity sp : server.getPlayerManager().getPlayerList()) {

            ItemStack stack = SoulSailItemCompat.findSoulSail(sp);
            boolean holding = !stack.isEmpty() && (stack.getItem() instanceof ISoulSailItem);

            SoulBannerGrade grade = SoulBannerGrade.MORTAL;
            if (holding) grade = SoulSailItemCompat.getBannerGrade(stack);

            // 1) 最大生命加成（单位：1❤=2点血）
            double extra = extraMaxHealthByGrade(grade);

            var attr = sp.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
            if (attr != null) {
                var cur = attr.getModifier(SOULSAIL_MAX_HP_UUID);

                double curVal = (cur == null) ? 0.0 : cur.getValue();

                if (extra <= 0.0) {
                    if (cur != null) {
                        attr.removeModifier(SOULSAIL_MAX_HP_UUID);
                        if (sp.getHealth() > sp.getMaxHealth()) sp.setHealth(sp.getMaxHealth());
                    }
                } else {
                    // 值变化才重建modifier（避免每tick都remove/add）
                    if (cur == null || Math.abs(curVal - extra) > 1e-6) {
                        if (cur != null) attr.removeModifier(SOULSAIL_MAX_HP_UUID);
                        attr.addPersistentModifier(new EntityAttributeModifier(
                                SOULSAIL_MAX_HP_UUID,
                                "soul_sail_grade_health",
                                extra,
                                EntityAttributeModifier.Operation.ADDITION
                        ));
                    }
                }
            }

            boolean shouldFly = holding && grade.getLevel() >= SoulBannerGrade.IMMORTAL.getLevel();
            applyFlight(sp, shouldFly);
        }
    }

    // 这里填每一阶的加成（单位：血量点数，=❤*2）
    private static double extraMaxHealthByGrade(SoulBannerGrade g) {
        return switch (g) {
            case MORTAL      -> 0.0;
            case EARTH       -> 2.0;  // +1❤
            case HEAVEN      -> 4.0;  // +2❤
            case MYSTERIOUS  -> 8.0;  // +4❤
            case YELLOW      -> 14.0;  // +7❤
            case UNIVERSE    -> 22.0;  // +11❤
            case COSMOS      -> 32.0;  // +16❤
            case FLOOD       -> 44.0; // +22❤
            case WASTELAND   -> 58.0; // +29❤
            case IMMORTAL    -> 74.0; // +37❤
        };
    }

    private static void applyFlight(ServerPlayerEntity sp, boolean shouldEnable) {
        if (shouldEnable) {
            if (!sp.getAbilities().allowFlying) {
                sp.getAbilities().allowFlying = true;
                sp.sendAbilitiesUpdate();
            }
        } else {
            if (!sp.isCreative() && !sp.isSpectator()) {
                if (sp.getAbilities().allowFlying) {
                    sp.getAbilities().allowFlying = false;
                    sp.getAbilities().flying = false;
                    sp.sendAbilitiesUpdate();
                }
            }
        }
    }
}
