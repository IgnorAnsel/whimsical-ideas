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
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
