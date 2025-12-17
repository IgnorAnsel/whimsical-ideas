package com.ignoransel.whimsicalideas.content.soulsail;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class SoulXpOrbEntity extends ExperienceOrbEntity {
    public SoulXpOrbEntity(EntityType<? extends ExperienceOrbEntity> type, World world) {
        super(type, world);
    }

    public SoulXpOrbEntity(World world, double x, double y, double z, int amount) {
        super(world, x, y, z, amount);
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        // 什么都不做 => 不加经验、不消失
    }
}