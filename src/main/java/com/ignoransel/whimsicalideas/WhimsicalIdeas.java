package com.ignoransel.whimsicalideas;

import com.ignoransel.whimsicalideas.content.hex.handers.HexRefineHandler;
import com.ignoransel.whimsicalideas.content.soulsail.SoulSailEvents;
import com.ignoransel.whimsicalideas.registry.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

public class WhimsicalIdeas implements ModInitializer, ClientModInitializer {
    public static final String MODID = "whimsical-ideas";

    @Override
    public void onInitialize() {
        WIBlocks.init();
        WIBlockEntities.init();
        WIItems.init();
        WITabs.init();
        WIEvents.init();
        WINetwork.registerServer();
        HexRefineHandler.register();
        SoulSailEvents.register();
        WIPatterns.init();
        WIStatusEffects.init();
        WIRecipes.init();


    }
    @Override
    public void onInitializeClient() {
        // 客户端渲染注册
        System.out.println("[WhimsicalIdeas] Client init OK");
        WIBlockEntities.initClient();
    }
}
