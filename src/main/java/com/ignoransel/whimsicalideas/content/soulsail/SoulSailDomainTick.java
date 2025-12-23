package com.ignoransel.whimsicalideas.content.soulsail;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;

import static com.ignoransel.whimsicalideas.content.soulsail.SoulSailItemCompat.findSoulSail;

public final class SoulSailDomainTick {
    private SoulSailDomainTick() {}

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(SoulSailDomainTick::onTick);
    }

    private static void onTick(MinecraftServer server) {
        for (ServerPlayerEntity sp : server.getPlayerManager().getPlayerList()) {

            // 找手上魂幡
            ItemStack stack = findSoulSail(sp);

            SoulBannerGrade grade = SoulSailItemCompat.getBannerGrade(stack);
            if (grade.getLevel() < SoulBannerGrade.UNIVERSE.getLevel()) continue;

            if (!SoulSailItemCompat.isSoulDomainEnabled(stack)) continue;
            long perTickCost = 2;
            if (!SoulSailItemCompat.spendRefinedSouls(stack, perTickCost)) {
                SoulSailItemCompat.setSoulDomainEnabled(stack, false);
                sp.sendMessage(Text.literal("魂域关闭：魂魄不足").formatted(Formatting.RED), true);
                continue;
            }

            // ✅ 你获得减伤（抗性 I）
            sp.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 10, 0, true, false, true));

            double radius = 7.0;
            Box box = sp.getBoundingBox().expand(radius, 2.5, radius);

            // DOT：每 0.5 秒一次
            boolean doDot = (sp.age % 10) == 0;
            float dotDamage = 1.0f;   // 0.5❤/0.5s（可调）
            float leechRatio = 0.35f; // 吸血比例（可调）

            for (LivingEntity e : sp.getServerWorld().getEntitiesByClass(
                    LivingEntity.class, box, ent -> ent.isAlive() && ent != sp)) {
                // 先不做 PVP（你要 PVP 我再给你队伍/白名单判断）
                if (e instanceof ServerPlayerEntity) continue;

                // ✅ 控场：减速/虚弱/“力量减少”(用挖掘疲劳模拟综合削弱)
                e.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20, 2, true, false, true));
                e.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 20, 1, true, false, true));
                e.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 20, 1, true, false, true));

                // ✅ “眩晕感”：原版没真眩晕，用失明+反胃叠加
                e.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 20, 0, true, false, true));
                e.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 40, 0, true, false, true));

                // ✅ 持续扣血 + 吸血（不用 mixin，稳定）
                if (doDot) {
                    boolean damaged = e.damage(sp.getServerWorld().getDamageSources().magic(), dotDamage);
                    if (damaged) sp.heal(dotDamage * leechRatio);
                }
            }

            if ((sp.age % 4) == 0) { // 每4tick(0.2s)发一次，别每tick，太费
                var world = sp.getServerWorld();
                double r = radius;

                // 1) 环形粒子（地面一圈）
                int points = 24;
                for (int i = 0; i < points; i++) {
                    double ang = (Math.PI * 2.0) * i / points;
                    double px = sp.getX() + Math.cos(ang) * r;
                    double pz = sp.getZ() + Math.sin(ang) * r;
                    world.spawnParticles(
                            net.minecraft.particle.ParticleTypes.SOUL, // 或 SOUL_FIRE_FLAME
                            px, sp.getY() + 0.15, pz,
                            1, 0, 0, 0, 0.0
                    );
                }

                // 2) 中心上升柱（让你一眼看到“领域中心”）
                world.spawnParticles(
                        net.minecraft.particle.ParticleTypes.SOUL_FIRE_FLAME,
                        sp.getX(), sp.getY() + 0.2, sp.getZ(),
                        6, 0.25, 0.15, 0.25, 0.01
                );
            }
        }
    }
}
