package com.ignoransel.whimsicalideas;

import com.ignoransel.whimsicalideas.content.hex.handers.HexRefineHandler;
import com.ignoransel.whimsicalideas.content.soulsail.SoulSailEvents;
import com.ignoransel.whimsicalideas.content.soulsail.render.*;
import com.ignoransel.whimsicalideas.registry.*;
import com.ignoransel.whimsicalideas.render.SoulXpOrbEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

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
        WIEntities.init();
        WIKeybinds.register();
    }

    public void registerSoulBannerItemRenderer(Item item) {
        BuiltinItemRendererRegistry.INSTANCE.register(
                item,
                new SoulBannerItemRenderer()
                );
    }

    public static final EntityModelLayer SOUL_BANNER_LAYER =
            new EntityModelLayer(new Identifier("whimsical-ideas", "soul_banner"), "main");
    @Override
    public void onInitializeClient() {

        // 客户端渲染注册
        System.out.println("[WhimsicalIdeas] Client init OK");
        WIBlockEntities.initClient();
        EntityRendererRegistry.register(WIEntities.SOUL_XP_ORB, SoulXpOrbEntityRenderer::new);


        EntityModelLayerRegistry.registerModelLayer(
                SOUL_BANNER_LAYER,
                SoulBannerModel::getTexturedModelData
        );

        registerSoulBannerItemRenderer(WIItems.TEST_ZUN_SOUL_SAIL);
        registerSoulBannerItemRenderer(WIItems.TEST_ZUN_SOUL_SAIL_MORTAL);
        registerSoulBannerItemRenderer(WIItems.TEST_ZUN_SOUL_SAIL_EARTH);
        registerSoulBannerItemRenderer(WIItems.TEST_ZUN_SOUL_SAIL_HEAVEN);
        registerSoulBannerItemRenderer(WIItems.TEST_ZUN_SOUL_SAIL_MYSTERIOUS);
        registerSoulBannerItemRenderer(WIItems.TEST_ZUN_SOUL_SAIL_YELLOW);
        registerSoulBannerItemRenderer(WIItems.TEST_ZUN_SOUL_SAIL_UNIVERSE);
        registerSoulBannerItemRenderer(WIItems.TEST_ZUN_SOUL_SAIL_COSMOS);
        registerSoulBannerItemRenderer(WIItems.TEST_ZUN_SOUL_SAIL_FLOOD);
        registerSoulBannerItemRenderer(WIItems.TEST_ZUN_SOUL_SAIL_WASTELAND);
        registerSoulBannerItemRenderer(WIItems.TEST_ZUN_SOUL_SAIL_IMMORTAL);

        BlockEntityRendererFactories.register(
                WIBlockEntities.SOUL_BANNER_BE,
                SoulBannerRenderer::new
        );

        BlockEntityRendererFactories.register(
                WIBlockEntities.SOUL_SAIL_POLE_BE,
                SoulSailPoleRenderer::new
        );

        BuiltinItemRendererRegistry.INSTANCE.register(
                WIItems.SOUL_SAIL_POLE,
                new SoulSailPoleItemRenderer()
        );

        HudRenderCallback.EVENT.register(new SoulSailCooldownHud());
        EntityRendererRegistry.register(WIEntities.COLORED_LIGHTNING, ColoredLightningRenderer::new);

    }
}
