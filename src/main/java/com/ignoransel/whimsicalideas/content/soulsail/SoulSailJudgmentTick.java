package com.ignoransel.whimsicalideas.content.soulsail;

import com.ignoransel.whimsicalideas.content.soulsail.entity.ColoredLightningEntity;
import com.ignoransel.whimsicalideas.registry.WIEntities;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class SoulSailJudgmentTick {
    private SoulSailJudgmentTick() {}

    // ====== 参数（你可按感觉调） ======
    private static final double MAX_RADIUS = 28.0;         // 波最大半径
    private static final double EXPAND_PER_TICK = 0.55;    // 扩散速度（每tick增长）
    private static final double SHELL_THICKNESS = 1.6;     // 波前厚度（边界范围）
    private static final int STRIKE_INTERVAL = 4;          // 雷罚频率（tick）4=0.2s一次 很凶
    private static final int BLOCK_EAT_SAMPLES = 160;      // 每tick吞噬采样点数（越大吞噬越“实”但更吃性能）
    private static final int VFX_SAMPLES = 120;            // 每tick球面粒子采样点数

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(SoulSailJudgmentTick::onTick);
    }

    private static void onTick(MinecraftServer server) {
        long now = server.getOverworld().getTime();

        for (ServerPlayerEntity sp : server.getPlayerManager().getPlayerList()) {
            ItemStack stack = SoulSailItemCompat.findSoulSail(sp);
            if (stack.isEmpty() || !(stack.getItem() instanceof ISoulSailItem)) continue;

            SoulBannerGrade grade = SoulSailItemCompat.getBannerGrade(stack);
            if (grade.getLevel() < SoulBannerGrade.IMMORTAL.getLevel()) continue;

            if (!SoulSailItemCompat.isJudgmentActive(stack, now)) continue;

            ServerWorld world = sp.getServerWorld();
            long start = SoulSailItemCompat.getJudgmentStart(stack);
            long until = SoulSailItemCompat.getJudgmentUntil(stack);
            Vec3d center = SoulSailItemCompat.getJudgmentPos(stack);

            if (start <= 0) start = now;

            // ====== 当前半径 ======
            double r = (now - start) * EXPAND_PER_TICK;
            if (r > MAX_RADIUS) r = MAX_RADIUS;

            // ====== 结束 ======
            if (now >= until || r >= MAX_RADIUS) {
                SoulSailItemCompat.clearJudgment(stack);
                // 结束时可加一次“终结爆散特效”，你要的话我再补
                continue;
            }

            // ====== 1) 球面VFX：让玩家看到波前在哪 ======
            spawnSphereVfx(world, center, r, VFX_SAMPLES);

            // ====== 2) 波前shell：触碰生物 -> 加入雷罚名单 ======
            double outer = r + SHELL_THICKNESS;
            double inner = Math.max(0.0, r - SHELL_THICKNESS);

            Box box = new Box(center.x - outer, center.y - outer, center.z - outer,
                    center.x + outer, center.y + outer, center.z + outer);

            for (LivingEntity e : world.getEntitiesByClass(LivingEntity.class, box, ent -> ent.isAlive() && ent != sp)) {
                // if (e instanceof ServerPlayerEntity) continue; // 不做PVP先
                double d = e.getPos().distanceTo(center);
                if (d >= inner && d <= outer) {
                    SoulSailItemCompat.addJudgmentPunished(stack, e.getUuid());
                }
            }

            // ====== 3) 吞噬方块：采样球面点，碰到方块就“吞掉” ======
            eatBlocksOnSphere(world, center, r, BLOCK_EAT_SAMPLES);

            // ====== 4) 雷罚名单：直到死（每 STRIKE_INTERVAL tick 丢一次雷）=====
            if ((now % STRIKE_INTERVAL) == 0) {
                doPunishStrikes(server, world, sp, stack, now);
            }
        }
    }

    private static void doPunishStrikes(MinecraftServer server, ServerWorld world,
                                        ServerPlayerEntity owner, ItemStack stack, long now) {

        Set<UUID> punished = SoulSailItemCompat.getJudgmentPunished(stack);
        if (punished.isEmpty()) return;

        Set<UUID> keep = new HashSet<>();

        for (UUID id : punished) {
            var ent = world.getEntity(id);
            if (!(ent instanceof LivingEntity target) || !target.isAlive()) {
                continue;
            }
            keep.add(id);

            // 给目标一点“被锁定”的魂粒子（可选）
            world.spawnParticles(net.minecraft.particle.ParticleTypes.SOUL,
                    target.getX(), target.getBodyY(0.6), target.getZ(),
                    6, 0.25, 0.35, 0.25, 0.01);

            // 彩雷（你的实体）
            strikeColored(world, owner, target.getPos());
        }

        // 清理死掉的
        if (keep.size() != punished.size()) {
            SoulSailItemCompat.setJudgmentPunished(stack, keep);
        }
    }

    private static void strikeColored(ServerWorld world, ServerPlayerEntity owner, Vec3d pos) {
        ColoredLightningEntity l = new ColoredLightningEntity(WIEntities.COLORED_LIGHTNING, world);
        l.refreshPositionAndAngles(pos.x, pos.y, pos.z, 0f, 0f);

        l.setOwner(owner);
        l.setDamage(7.0f);   // 你要“直到死”就别太低
        l.setRadius(3);
        l.setDoFire(false);
        l.setLifeTicks(30);

        // 初始颜色随机（每tick彩虹变色要改实体 tick，下面有）
        l.setColorRgb(world.random.nextInt(0x1000000));

        world.spawnEntity(l);
    }

    // ====== 球面粒子：告诉玩家波前边界 ======
    private static void spawnSphereVfx(ServerWorld world, Vec3d c, double r, int samples) {
        for (int i = 0; i < samples; i++) {
            // 随机方向（均匀球面）
            double u = world.random.nextDouble();
            double v = world.random.nextDouble();
            double theta = 2 * Math.PI * u;
            double phi = Math.acos(2 * v - 1);
            double x = Math.sin(phi) * Math.cos(theta);
            double y = Math.cos(phi);
            double z = Math.sin(phi) * Math.sin(theta);

            Vec3d p = c.add(x * r, y * r, z * r);
            world.spawnParticles(net.minecraft.particle.ParticleTypes.SOUL_FIRE_FLAME,
                    p.x, p.y, p.z, 1, 0, 0, 0, 0.0);
        }
    }

    // ====== 吞噬方块：采样球面点，把那一圈打成空气 ======
    private static void eatBlocksOnSphere(ServerWorld world, Vec3d c, double r, int samples) {
        for (int i = 0; i < samples; i++) {
            double u = world.random.nextDouble();
            double v = world.random.nextDouble();
            double theta = 2 * Math.PI * u;
            double phi = Math.acos(2 * v - 1);

            double dx = Math.sin(phi) * Math.cos(theta);
            double dy = Math.cos(phi);
            double dz = Math.sin(phi) * Math.sin(theta);

            // 在波前附近吃掉（做一点厚度）
            for (int t = -1; t <= 1; t++) {
                double rr = r + t * 0.6;
                BlockPos pos = BlockPos.ofFloored(c.x + dx * rr, c.y + dy * rr, c.z + dz * rr);

                BlockState state = world.getBlockState(pos);
                if (state.isAir()) continue;

                // 保护不可破坏的
                if (state.isOf(Blocks.BEDROCK) || state.isOf(Blocks.BARRIER)) continue;

                // “吞噬”：直接变空气，不掉落
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
            }
        }
    }
}
