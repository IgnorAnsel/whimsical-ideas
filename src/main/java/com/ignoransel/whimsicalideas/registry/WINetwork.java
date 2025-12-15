package com.ignoransel.whimsicalideas.registry;

import com.ignoransel.whimsicalideas.WhimsicalIdeas;
import com.ignoransel.whimsicalideas.content.hex.HexBase;
import com.ignoransel.whimsicalideas.content.hex.HexRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class WINetwork {
    public static final Identifier SELECT_HEX = new Identifier(WhimsicalIdeas.MODID, "select_hex");

    public static void registerServer() {
        ServerPlayNetworking.registerGlobalReceiver(SELECT_HEX, (server, player, handler, buf, responseSender) -> {
            String hexId = buf.readString(64); // ✅ 限制长度

            server.execute(() -> {
                HexBase hex = HexRegistry.getById(hexId);
                if (hex != null) {
                    hex.apply(player);
                }
            });
        });
    }
}
