package com.ignoransel.whimsicalideas.client;

import com.ignoransel.whimsicalideas.registry.WINetwork;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;

public final class ClientTimeSync {
    private ClientTimeSync() {}
    private static long offset = 0; // overworldTime - clientWorldTime

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(WINetwork.SYNC_TIME, (client, handler, buf, responseSender) -> {
            long serverOverworldTime = buf.readLong();
            client.execute(() -> {
                if (MinecraftClient.getInstance().world != null) {
                    long clientTime = MinecraftClient.getInstance().world.getTime();
                    offset = serverOverworldTime - clientTime;
                }
            });
        });
    }

    public static long nowOverworldTime() {
        MinecraftClient c = MinecraftClient.getInstance();
        if (c.world == null) return 0;
        return c.world.getTime() + offset;
    }
}
