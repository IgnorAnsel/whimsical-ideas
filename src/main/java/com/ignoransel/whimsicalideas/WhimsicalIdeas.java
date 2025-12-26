package com.ignoransel.whimsicalideas;

import com.ignoransel.whimsicalideas.client.ClientTimeSync;
import com.ignoransel.whimsicalideas.client.ProjectionFrameRenderer;
import com.ignoransel.whimsicalideas.content.hex.handers.HexRefineHandler;
import com.ignoransel.whimsicalideas.content.soulsail.*;
import com.ignoransel.whimsicalideas.content.soulsail.render.*;
import com.ignoransel.whimsicalideas.registry.*;
import com.ignoransel.whimsicalideas.render.SoulXpOrbEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import static com.ignoransel.whimsicalideas.content.soulsail.render.SoulSailAbilityBarHud.getBestHeldSoulSail;

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
        SoulSailDomainTick.register();
        SoulSailGraspTick.register();
        SoulSailMaelstromTick.register();
        SoulSailJudgmentTick.register();
        SoulSailGradePassiveTick.register();

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
        HudRenderCallback.EVENT.register(new SoulSailAbilityBarHud());
        ClientTimeSync.register();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            boolean altDown = InputUtil.isKeyPressed(client.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_ALT)
                    || InputUtil.isKeyPressed(client.getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_ALT);

            // 只有手上有魂幡才允许
            ItemStack best = getBestHeldSoulSail(client); // 你已有的那套取高阶
            boolean holding = !best.isEmpty() && (best.getItem() instanceof ISoulSailItem);

            if (altDown && holding) {
                // 如果当前没有界面，打开我们的透明选择界面
                if (client.currentScreen == null) {
                    boolean anchorLeft = isOffhandBest(client, best); // 副手=左；主手=右
                    client.setScreen(new SoulSailAbilitySelectScreen(best, anchorLeft));
                }
            } else {
                // 松开Alt：如果当前就是我们的界面就关掉
                if (client.currentScreen instanceof SoulSailAbilitySelectScreen) {
                    client.setScreen(null);
                }
            }
        });
        HudRenderCallback.EVENT.register(new SoulSiphonHud());
        BlockEntityRendererFactories.register(
                WIBlockEntities.SOUL_SIPHON,
                com.ignoransel.whimsicalideas.content.soulsail.render.SoulSiphonBlockEntityRenderer::new
        );
        BlockRenderLayerMap.INSTANCE.putBlock(WIBlocks.SOUL_SIPHON, RenderLayer.getCutout());
        BuiltinItemRendererRegistry.INSTANCE.register(WIItems.PROJECTION_FRAME, new ProjectionFrameRenderer());

        BlockRenderLayerMap.INSTANCE.putBlock(WIBlocks.TUNING_FORK_BLOCK, RenderLayer.getCutout());

    }
    private static boolean isOffhandBest(MinecraftClient client, ItemStack best) {
        return client.player.getOffHandStack() == best;
    }
}
