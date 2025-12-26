package com.ignoransel.whimsicalideas.content.soulsail;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public final class SoulSailGraspTick {
    private SoulSailGraspTick() {}

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(SoulSailGraspTick::onTick);
    }

    private static void onTick(MinecraftServer server) {
        long now = server.getOverworld().getTime();

        for (ServerPlayerEntity sp : server.getPlayerManager().getPlayerList()) {
            ItemStack stack = SoulSailItemCompat.findSoulSail(sp);
            if (stack.isEmpty() || !(stack.getItem() instanceof ISoulSailItem)) continue;

            // if (!SoulSailItemCompat.isGraspActive(stack, now)) continue;

            var uuid = SoulSailItemCompat.getGraspTarget(stack);
            if (uuid == null) { SoulSailItemCompat.clearGrasp(stack); continue; }

            var ent = sp.getServerWorld().getEntity(uuid);
            if (!(ent instanceof LivingEntity target) || !target.isAlive()) {
                SoulSailItemCompat.clearGrasp(stack);
                continue;
            }

            long until = SoulSailItemCompat.getGraspUntil(stack);

            // 超出距离/看不见也可以中断（可选）
            if (sp.squaredDistanceTo(target) > 30 * 30) {
                SoulSailItemCompat.clearGrasp(stack);
                continue;
            }
            if (sp.getWorld() instanceof ServerWorld sw && (sp.age % 2) == 0) {
                // 身体周围一圈魂粒子（围绕高度）
                sw.spawnParticles(ParticleTypes.SOUL,
                        target.getX(), target.getBodyY(0.6), target.getZ(),
                        10,                 // 数量
                        0.35, 0.45, 0.35,   // 扩散：x,y,z
                        0.01                // speed（小一点像飘）
                );

                // 点缀一点更亮的魂焰
                sw.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME,
                        target.getX(), target.getBodyY(0.6), target.getZ(),
                        3,
                        0.25, 0.35, 0.25,
                        0.005
                );
            }
            // 束缚感：超高减速 + 虚弱
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 10, 4, true, false, true));
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 10, 1, true, false, true));
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 10, 0, true, false, true));

            // 拉拽：每2tick拉一下
            if ((sp.age % 2) == 0) {
                Vec3d pull = sp.getPos().add(0, 0.8, 0).subtract(target.getPos().add(0, 0.8, 0));
                double dist = pull.length();
                if (dist > 0.001) {
                    Vec3d dir = pull.normalize();
                    double strength = 0.28; // 拉拽强度可调
                    target.setVelocity(target.getVelocity().multiply(0.2).add(dir.multiply(strength)));
                    target.velocityModified = true;
                }
            }

            // 粒子链：每4tick画一条线
            if (sp.getWorld() instanceof ServerWorld sw && (sp.age % 4) == 0) {
                spawnChain(sw, sp.getPos().add(0, 1.1, 0), target.getPos().add(0, target.getHeight() * 0.6, 0));
            }

            // 结束：魂爆 + 吸血
            if (now >= until) {
                SoulSailItemCompat.clearGrasp(stack);
                if (sp.getWorld() instanceof ServerWorld sw) {
                    double x = target.getX();
                    double y = target.getBodyY(0.6);
                    double z = target.getZ();

                    sw.spawnParticles(ParticleTypes.SOUL,
                            x, y, z,
                            120,
                            0.9, 0.7, 0.9,
                            0.18
                    );

                    sw.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME,
                            x, y, z,
                            45,
                            0.7, 0.5, 0.7,
                            0.12
                    );

                    sw.spawnParticles(ParticleTypes.SOUL,
                            x, y, z,
                            60,
                            0.6, 0.8, 0.6,
                            0.02
                    );
                }

                float dmg = 6.0f; // 3❤ 可调
                boolean damaged = target.damage(sp.getServerWorld().getDamageSources().magic(), dmg);
                if (damaged) {
                    sp.heal(dmg * 0.35f);
                }

                if (sp.getWorld() instanceof ServerWorld sw) {
                    if (target.isDead())
                        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.invoker().afterKilledOtherEntity(sw, sp, target);
                    sw.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME,
                            target.getX(), target.getBodyY(0.5), target.getZ(),
                            18, 0.35, 0.25, 0.35, 0.02);
                    sw.spawnParticles(ParticleTypes.SOUL,
                            target.getX(), target.getBodyY(0.5), target.getZ(),
                            30, 0.5, 0.35, 0.5, 0.02);
                }
            }
        }
    }

    private static void spawnChain(ServerWorld sw, Vec3d from, Vec3d to) {
        Vec3d d = to.subtract(from);
        int steps = 14;
        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;
            Vec3d p = from.add(d.multiply(t));
            sw.spawnParticles(ParticleTypes.SOUL, p.x, p.y, p.z, 1, 0, 0, 0, 0.0);
        }
    }
}
