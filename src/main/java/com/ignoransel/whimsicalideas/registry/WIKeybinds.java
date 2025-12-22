package com.ignoransel.whimsicalideas.registry;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class WIKeybinds {
    public static KeyBinding CYCLE;
    public static KeyBinding CAST;

    public static void register() {
        CYCLE = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.whimsical-ideas.soulsail_cycle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "category.whimsical-ideas"
        ));

        CAST = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.whimsical-ideas.soulsail_cast",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_J,
                "category.whimsical-ideas"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (CYCLE.wasPressed()) {
                ClientPlayNetworking.send(WINetwork.CYCLE_ABILITY, PacketByteBufs.create());
            }
            while (CAST.wasPressed()) {
                ClientPlayNetworking.send(WINetwork.CAST_ABILITY, PacketByteBufs.create());
            }
        });
    }
}
