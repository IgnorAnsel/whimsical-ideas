package com.ignoransel.whimsicalideas.registry;

import com.ignoransel.whimsicalideas.WhimsicalIdeas;
import com.ignoransel.whimsicalideas.content.hex.HexBase;
import com.ignoransel.whimsicalideas.content.hex.HexRegistry;
import com.ignoransel.whimsicalideas.content.soulsail.SoulSailAbilities;
import com.ignoransel.whimsicalideas.content.soulsail.SoulSailBannerItem;
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
    }
}
