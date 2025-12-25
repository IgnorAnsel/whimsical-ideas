package com.ignoransel.whimsicalideas.content.soulsail;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public final class SoulSailMaelstromTick {
    private SoulSailMaelstromTick() {}

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(SoulSailMaelstromTick::onTick);
    }

    private static void onTick(MinecraftServer server) {
        for (ServerPlayerEntity sp : server.getPlayerManager().getPlayerList()) {
            ItemStack stack = SoulSailItemCompat.findSoulSail(sp);
            if (stack.isEmpty() || !(stack.getItem() instanceof ISoulSailItem)) continue;

            ServerWorld world = sp.getServerWorld();
            long now = world.getTime();

            long until = SoulSailItemCompat.getMaelstromUntil(stack);
            if (until <= 0) continue;

            long start = SoulSailItemCompat.getMaelstromStart(stack);
            if (start <= 0) start = now; // 防御：没写 start 就当现在开始

            Vec3d center = SoulSailItemCompat.getMaelstromPos(stack);

            // 离中心太远就中断（可选）
            if (sp.squaredDistanceTo(center.x, center.y, center.z) > 50 * 50) {
                SoulSailItemCompat.clearMaelstrom(stack);
                continue;
            }

            // ===== 进度：0 -> 1（带缓动）=====
            int total = (int) Math.max(1L, until - start);
            int elapsed = (int) Math.max(0L, Math.min(until - start, now - start));
            float t = elapsed / (float) total;
            float ease = smoothstep(t); // 形成过程：前慢后快

            // ===== 动态半径：小 -> 大 =====
            double Rmax = 18.0; // 想更巨大：20~22
            double radius = 2.0 + (Rmax - 2.0) * ease;

            // ===== 结束：终结魂爆 =====
            if (now >= until) {
                doMaelstromBurst(sp, center, Rmax); // 用最大半径结算
                SoulSailItemCompat.clearMaelstrom(stack);
                sp.sendMessage(Text.literal("归葬·魂爆").formatted(Formatting.AQUA), true);
                continue;
            }

            // ===== 影响范围盒子（跟着变大）=====
            Box box = new Box(center.x - radius, center.y - 4.0, center.z - radius,
                    center.x + radius, center.y + 6.0, center.z + radius);

            // DOT：每 0.5 秒一次
            boolean doDot = (sp.age % 10) == 0;
            float dot = 1.0f;     // 0.5❤/0.5s
            float leech = 0.20f;

            // 吸入与旋转：随形成进度增强
            double pullBase = 0.05 + 0.08 * ease;    // 0.05 -> 0.13
            double pullExtra = 0.12 + 0.20 * ease;   // 0.12 -> 0.32
            double swirlStrength = 0.03 + 0.09 * ease; // 0.03 -> 0.12

            for (LivingEntity e : world.getEntitiesByClass(LivingEntity.class, box, ent -> ent.isAlive() && ent != sp)) {
                // if (e instanceof ServerPlayerEntity) continue; // 先不做 PVP

                Vec3d p = e.getPos().add(0, e.getHeight() * 0.5, 0);
                Vec3d toCenter = center.add(0, 0.6, 0).subtract(p);
                double dist = toCenter.length();
                if (dist > radius || dist < 0.001) continue;

                Vec3d dir = toCenter.normalize();

                // 越远吸得越狠（避免贴脸抖）
                double pull = pullBase + pullExtra * (dist / radius);

                // 旋转分量（让它像漩涡绕着转）
                Vec3d swirl = new Vec3d(-dir.z, 0, dir.x).multiply(swirlStrength);

                e.setVelocity(e.getVelocity().multiply(0.35).add(dir.multiply(pull)).add(swirl));
                e.velocityModified = true;

                // 控制（持续刷新短 duration）
                e.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20, 2, true, false, true));
                e.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 20, 1, true, false, true));
                e.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 20, 1, true, false, true));

                // DOT + 吸血
                if (doDot) {
                    boolean damaged = e.damage(world.getDamageSources().magic(), dot);
                    if (damaged) sp.heal(dot * leech);
                }

                // 目标缠魂：形成后更明显
                if ((sp.age % 6) == 0) {
                    int count = (ease < 0.5f) ? 4 : 8;
                    world.spawnParticles(ParticleTypes.SOUL,
                            e.getX(), e.getBodyY(0.6), e.getZ(),
                            count, 0.25, 0.35, 0.25, 0.01);
                }
            }

            // 玩家获得一点“站在漩涡里更硬”的感觉
            world.getServer().execute(() -> {}); // no-op，占位避免你误删 world 引用（可删）
            sp.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 10, 0, true, false, true));

            // ===== 漩涡视觉：2tick 刷一次更丝滑 =====
            if ((sp.age % 2) == 0) {
                spawnMaelstromVfx(world, center, radius, now, ease);
            }
        }
    }

    // smoothstep：0->1 前慢后快
    private static float smoothstep(float x) {
        x = Math.max(0f, Math.min(1f, x));
        return x * x * (3f - 2f * x);
    }

    private static void spawnMaelstromVfx(ServerWorld sw, Vec3d center, double radius, long nowTick, float ease) {
        // ===== 基础参数 =====
        double height = 8.0 + 24.0 * ease;          // 更高一点：8 -> 32
        int layers = (int) (16 + 30 * ease);        // 层数：16 -> 46

        // 旋转速度：明显一些（别太小）
        double rotBase = nowTick * (0.35 + 0.30 * ease);

        // 漏斗型：底部细、上面粗
        double bottomR = Math.max(0.8, radius * 0.15);
        double topR    = Math.max(2.2, radius * 1.00);

        // 关键：每层只画一段弧，而不是整圈
        // 弧长度（弧度）：越后期越长，视觉更“厚”
        double arc = (Math.PI * 0.8) + (Math.PI * 0.6) * ease; // 约 144° -> 252°

        // 弧段粒子数量（每层）
        int arcPtsMin = 10;
        int arcPtsMax = 26;

        // ===== 龙卷风主体 =====
        for (int ly = 0; ly <= layers; ly++) {
            double u = ly / (double) layers; // 0底->1顶
            double y = center.y + 0.15 + u * height;

            // 半径随高度变大（漏斗）
            double k = Math.pow(u, 1.15);
            double r = lerp(bottomR, topR, k);

            // 上面转更快一点 + 高度相位偏移更大（扭）
            double rot = rotBase * (0.7 + 1.1 * u) + u * 10.0;

            // “只画弧段”：弧段中心角会随 rot 转动
            // 让弧段中心在 rot 上，并且每层再抖一点点，形成“涡丝”
            double startAng = rot - arc * 0.5;

            int arcPts = (int) (arcPtsMin + (arcPtsMax - arcPtsMin) * (0.3 + 0.7 * ease) + r * 0.25);
            arcPts = Math.min(40, Math.max(8, arcPts));

            for (int i = 0; i < arcPts; i++) {
                double t = i / (double) (arcPts - 1);
                double ang = startAng + arc * t;

                // 让弧段不是完全光滑：加一点径向抖动（增强“旋转丝带”感）
                double jitter = (Math.sin((nowTick + ly) * 0.25 + i) * 0.10) * (0.4 + 0.6 * ease);
                double rr = r * (1.0 + jitter);

                double x = center.x + Math.cos(ang) * rr;
                double z = center.z + Math.sin(ang) * rr;

                // 主体魂粒子：用 SOUL 或 SOUL_FIRE_FLAME 都行
                sw.spawnParticles(ParticleTypes.SOUL, x, y, z, 1, 0, 0, 0, 0.0);

                // 每隔几颗点一个火焰，让旋转“边缘”更明显
                if ((i % 6) == 0 && (ly % 2) == 0) {
                    sw.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y + 0.10, z, 1, 0, 0, 0, 0.01);
                }
            }

            // 中心上升柱：强调吸上去
            if ((ly % 3) == 0) {
                sw.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME,
                        center.x, y, center.z,
                        2, r * 0.06, 0.06, r * 0.06, 0.01);
            }
        }

        // ===== 地面“卷起感”（底部小一点）=====
        int ground = (int) Math.min(120, 26 + radius * 4);
        sw.spawnParticles(ParticleTypes.SOUL,
                center.x, center.y + 0.05, center.z,
                ground, radius * 0.22, 0.02, radius * 0.22, 0.01);

        // ===== 顶部散开：让高度更明显 =====
        sw.spawnParticles(ParticleTypes.SOUL,
                center.x, center.y + height, center.z,
                (int) (18 + 30 * ease),
                topR * 0.30, 0.35, topR * 0.30, 0.02);
    }

    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }



    private static void doMaelstromBurst(ServerPlayerEntity sp, Vec3d center, double radius) {
        ServerWorld sw = sp.getServerWorld();

        Box box = new Box(center.x - radius, center.y - 4.0, center.z - radius,
                center.x + radius, center.y + 6.0, center.z + radius);

        float burstDmg = 16.0f; // 8❤
        float leech = 0.25f;

        for (LivingEntity e : sw.getEntitiesByClass(LivingEntity.class, box, ent -> ent.isAlive() && ent != sp)) {
            // if (e instanceof ServerPlayerEntity) continue;

            Vec3d p = e.getPos().add(0, e.getHeight() * 0.5, 0);
            Vec3d fromCenter = p.subtract(center.add(0, 0.6, 0));
            double dist = fromCenter.length();
            if (dist > radius || dist < 0.001) continue;

            Vec3d dir = fromCenter.normalize();
            double strength = 1.4 * (1.0 - dist / radius); // 越近越强

            e.setVelocity(e.getVelocity().multiply(0.2).add(dir.multiply(strength)).add(0, 0.28, 0));
            e.velocityModified = true;

            boolean damaged = e.damage(sw.getDamageSources().magic(), burstDmg);
            if (damaged) sp.heal(burstDmg * leech);
        }

        // 爆散粒子：巨大爆开
        sw.spawnParticles(ParticleTypes.SOUL,
                center.x, center.y + 0.6, center.z,
                240, 1.2, 0.9, 1.2, 0.20);
        sw.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME,
                center.x, center.y + 0.6, center.z,
                95, 0.95, 0.75, 0.95, 0.14);
        sw.spawnParticles(ParticleTypes.SOUL,
                center.x, center.y + 0.6, center.z,
                120, 0.9, 1.2, 0.9, 0.03);
    }
}
