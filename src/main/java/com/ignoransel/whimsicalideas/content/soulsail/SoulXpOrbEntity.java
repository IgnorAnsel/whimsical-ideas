package com.ignoransel.whimsicalideas.content.soulsail;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;

import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.world.World;

public class SoulXpOrbEntity extends ExperienceOrbEntity {

    public SoulXpOrbEntity(EntityType<? extends ExperienceOrbEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        // 不可吸取
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        // 关键：让客户端按 EntityType 创建你的实体，从而使用你的 Renderer/贴图
        return new EntitySpawnS2CPacket(this);
    }
}
