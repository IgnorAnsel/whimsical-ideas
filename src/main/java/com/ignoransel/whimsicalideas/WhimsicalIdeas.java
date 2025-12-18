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
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.model.EntityModelLayer;
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
        BuiltinItemRendererRegistry.INSTANCE.register(
                WIItems.TEST_ZUN_SOUL_SAIL,     // 你的旗帜物品 Item 实例
                new SoulBannerItemRenderer()
        );
        BlockEntityRendererFactories.register(
                WIBlockEntities.SOUL_BANNER_BE,
                ctx -> new SoulBannerRenderer(ctx)
        );


        BlockEntityRendererFactories.register(
                WIBlockEntities.SOUL_SAIL_POLE_BE,
                SoulSailPoleRenderer::new
        );

        BuiltinItemRendererRegistry.INSTANCE.register(
                WIItems.SOUL_SAIL_POLE,
                new SoulSailPoleItemRenderer()
        );
    }
}
