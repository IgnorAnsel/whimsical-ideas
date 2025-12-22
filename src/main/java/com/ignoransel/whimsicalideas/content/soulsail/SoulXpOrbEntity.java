package com.ignoransel.whimsicalideas.content.soulsail;


import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SoulXpOrbEntity extends ExperienceOrbEntity {

    // 漂移方向 & 持续时间
    private Vec3d drift = Vec3d.ZERO;
    private int driftTicks = 0;

    // 一个“锚点”（例如房间中心），避免越飘越远：这里只做演示，建议用DataTracker/NBT保存
    private Vec3d anchor = null;

    public SoulXpOrbEntity(EntityType<? extends ExperienceOrbEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        // 不可吸取
    }

    @Override
    public void tick() {
        // 不要调用 super.tick()，否则会执行经验球靠近玩家/合并等逻辑

        this.baseTick();          // 保留基础更新（年龄/火焰/液体等）
        this.setNoGravity(true);  // 不受重力
        this.noClip = false;

        if (anchor == null) {
            anchor = this.getPos(); // 第一次以出生点为锚点（更推荐你传入房间中心）
        }

        // 每隔一段时间换一个漂移方向
        if (--driftTicks <= 0) {
            driftTicks = 20 + this.random.nextInt(40); // 1~3秒左右
            double dx = (this.random.nextDouble() - 0.5);
            double dy = (this.random.nextDouble() - 0.3) * 0.6;
            double dz = (this.random.nextDouble() - 0.5);
            Vec3d dir = new Vec3d(dx, dy, dz);
            if (dir.lengthSquared() < 1e-6) dir = new Vec3d(1, 0, 0);
            drift = dir.normalize().multiply(0.02); // 漂移速度
        }

        // 轻微上下“呼吸”浮动
        double bob = Math.sin((this.age + this.getId()) * 0.15) * 0.006;

        // 向锚点做一个“弹簧拉回”，避免飘出房间
        Vec3d toAnchor = anchor.subtract(this.getPos());
        Vec3d spring = toAnchor.multiply(0.002); // 拉回力度，越大越“拽得住”

        // 速度 = 惯性衰减 + 漂移 + 上下浮动 + 拉回
        Vec3d v = this.getVelocity().multiply(0.90)
                .add(drift)
                .add(0, bob, 0)
                .add(spring);

        // 限速，防止数值累积变快
        double max = 0.06;
        if (v.lengthSquared() > max * max) v = v.normalize().multiply(max);

        this.setVelocity(v);
        this.move(MovementType.SELF, v);

        // 如果你不想它自己消失：不要写 discard 逻辑
        // 如果你想 N 秒后消失：可以 if (this.age > 20*60) this.discard();
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
