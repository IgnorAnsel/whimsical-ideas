package com.ignoransel.whimsicalideas.registry;

import com.ignoransel.whimsicalideas.WhimsicalIdeas;
import com.ignoransel.whimsicalideas.content.hex.HexBase;
import com.ignoransel.whimsicalideas.content.hex.HexRegistry;
import com.ignoransel.whimsicalideas.content.soulsail.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import static com.ignoransel.whimsicalideas.content.soulsail.SoulSailItemCompat.findSoulSail;

public class WINetwork {
    public static final Identifier SELECT_HEX = new Identifier(WhimsicalIdeas.MODID, "select_hex");
    public static final Identifier CYCLE_ABILITY = new Identifier("whimsical-ideas", "cycle_ability");
    public static final Identifier CAST_ABILITY  = new Identifier("whimsical-ideas", "cast_ability");
    public static final Identifier SYNC_TIME = new Identifier("whimsical-ideas", "sync_time");
    public static final Identifier SET_ABILITY = new Identifier("whimsical-ideas", "set_ability");



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

        ServerPlayNetworking.registerGlobalReceiver(CYCLE_ABILITY, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                ItemStack stack = findSoulSail(player);
                if (!(stack.getItem() instanceof SoulSailBannerItem)) return;
                SoulSailAbilities.cycleAbility((ServerPlayerEntity) player, stack);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(CAST_ABILITY, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                ItemStack stack = findSoulSail(player);
                if (!(stack.getItem() instanceof SoulSailBannerItem)) return;
                SoulSailAbilities.castSelectedAbility((ServerPlayerEntity) player, stack);
            });
        });

        // 例如在某个通用 init 里注册
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if ((server.getTicks() % 20) != 0) return; // 每秒一次
            long t = server.getOverworld().getTime();
            for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
                var buf = PacketByteBufs.create();
                buf.writeLong(t);
                ServerPlayNetworking.send(p, WINetwork.SYNC_TIME, buf);
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(SET_ABILITY, (server, player, handler, buf, responseSender) -> {
            int ord = buf.readVarInt();
            server.execute(() -> {
                ItemStack stack = SoulSailItemCompat.findSoulSail((ServerPlayerEntity) player);
                if (!(stack.getItem() instanceof SoulSailBannerItem)) return;

                SoulSailAbility[] vals = SoulSailAbility.values();
                if (ord < 0 || ord >= vals.length) return;

                SoulSailAbility ab = vals[ord];
                SoulBannerGrade grade = SoulSailItemCompat.getBannerGrade(stack);

                // 只允许 NONE 或已解锁
                if (ab != SoulSailAbility.NONE && !ab.unlockedBy(grade)) return;

                SoulSailItemCompat.setSelectedAbility(stack, ord);
            });
        });

    }
}
