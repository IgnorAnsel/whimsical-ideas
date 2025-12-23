package com.ignoransel.whimsicalideas.content.soulsail;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class SoulWave {
    public static void castSoulWave(ServerPlayerEntity sp) {
        var world = sp.getServerWorld();

        // ===== 参数（你可以调）=====
        double range = 12.0;                // 扇形最远距离
        double halfAngleDeg = 40.0;         // 半角：40° => 总扇形 80°
        double cosThreshold = Math.cos(Math.toRadians(halfAngleDeg));
        float baseDamage = 8.0f;            // 4❤
        float leechRatio = 0.25f;           // 吸血比例
        int slowTicks = 60;                 // 3s
        int slowAmp = 2;                    // III
        int weakTicks = 60;                 // 3s
        int weakAmp = 1;                    // II
        double knockBase = 0.9;             // 击退强度
        double up = 0.12;                   // 轻微抬起

        Vec3d origin = sp.getPos().add(0, 1.0, 0);     // 从胸口附近发出
        Vec3d look = sp.getRotationVec(1.0f).normalize();

        // 先取一个大盒子，再用扇形判定筛选
        Box box = sp.getBoundingBox().expand(range, 3.0, range);

        for (LivingEntity e : world.getEntitiesByClass(LivingEntity.class, box, ent -> ent.isAlive() && ent != sp)) {
            if (e instanceof ServerPlayerEntity) continue; // 先不做PVP

            Vec3d to = e.getPos().add(0, e.getHeight() * 0.5, 0).subtract(origin);
            double dist = to.length();
            if (dist < 0.001 || dist > range) continue;

            Vec3d dirTo = to.normalize();
            double dot = look.dotProduct(dirTo);
            if (dot < cosThreshold) continue; // 不在扇形内

            // ===== 伤害 + 吸血 =====
            // 近一点伤害更高（可选）
            float damage = (float)(baseDamage * (0.65 + 0.35 * (1.0 - dist / range)));
            boolean damaged = e.damage(world.getDamageSources().magic(), damage);
            if (damaged) sp.heal(damage * leechRatio);

            // ===== 控制效果 =====
            e.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, slowTicks, slowAmp, true, false, true));
            e.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, weakTicks, weakAmp, true, false, true));

            // ===== 击退（朝远离玩家方向）=====
            double strength = knockBase * (0.5 + 0.5 * (1.0 - dist / range));
            Vec3d push = dirTo.multiply(strength).add(0, up, 0);
            e.setVelocity(e.getVelocity().multiply(0.25).add(push));
            e.velocityModified = true;

            // 目标身上点缀“魂浪”粒子
            ServerWorld sw = sp.getServerWorld();
            sw.spawnParticles(ParticleTypes.SOUL,
                    e.getX(), e.getBodyY(0.6), e.getZ(),
                    8, 0.25, 0.35, 0.25, 0.02);

        }

        // ===== 扇形可视化（非常关键，不然玩家不知道范围）=====
        ServerWorld sw = sp.getServerWorld();
        spawnFanWaveParticles(sw, sp, range, halfAngleDeg);
    }

    private static void spawnFanWaveParticles(ServerWorld sw, ServerPlayerEntity sp, double range, double halfAngleDeg) {
        Vec3d look = sp.getRotationVec(1.0f).normalize();
        double baseYaw = Math.atan2(look.z, look.x); // x-z 平面朝向

        // 用“多层弧线”做出冲击波扇面
        double y = sp.getY() + 0.15;
        double stepR = 0.9;     // 每一层半径间隔
        double stepA = 6.0;     // 角度步进（越小越密）

        for (double r = 1.5; r <= range; r += stepR) {
            double density = 1.0 - (r / range) * 0.6; // 越远越稀
            for (double a = -halfAngleDeg; a <= halfAngleDeg; a += stepA) {
                double theta = baseYaw + Math.toRadians(a);
                double x = sp.getX() + Math.cos(theta) * r;
                double z = sp.getZ() + Math.sin(theta) * r;

                // 魂浪主体
                sw.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME,
                        x, y, z,
                        1, 0.0, 0.0, 0.0, 0.02);

                // 余魂（让边界更显眼）
                if (Math.random() < density) {
                    sw.spawnParticles(ParticleTypes.SOUL,
                            x, y + 0.25, z,
                            1, 0.03, 0.08, 0.03, 0.01);
                }
            }
        }
    }
}
