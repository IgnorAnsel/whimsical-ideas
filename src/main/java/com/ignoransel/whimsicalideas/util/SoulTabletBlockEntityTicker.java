package com.ignoransel.whimsicalideas.util;

import com.ignoransel.whimsicalideas.content.soultablet.SoulTabletBlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;

public final class SoulTabletBlockEntityTicker {
    private SoulTabletBlockEntityTicker(){}

    public static void tickServer(ServerWorld world, SoulTabletBlockEntity be) {
        if (be.isBroken()) return;
        UUID owner = be.getOwnerUuid();
        if (owner == null) return;

        ServerPlayerEntity p = world.getServer().getPlayerManager().getPlayer(owner);
        if (p == null) return;

        float max = p.getMaxHealth();
        float cur = p.getHealth();
        if (max <= 0f) return;
        be.setHealthRatio(cur / max);
    }
}
