package com.ignoransel.whimsicalideas;

import com.ignoransel.whimsicalideas.content.hex.handers.HexRefineHandler;
import com.ignoransel.whimsicalideas.content.soulsail.SoulSailEvents;
import com.ignoransel.whimsicalideas.registry.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.BannerItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static com.ignoransel.whimsicalideas.recipe.ZunSoulSailRecipe.hasZunSoulPattern;

public class WhimsicalIdeas implements ModInitializer, ClientModInitializer {
    public static final String MODID = "whimsical-ideas";
    private static final Item[] RING = new Item[]{
            Items.GHAST_TEAR,     // 0
            Items.SPIDER_EYE,     // 1
            Items.ENDER_PEARL,    // 2
            Items.STRING,         // 3
            null,                 // 4 center
            Items.GUNPOWDER,      // 5
            Items.ROTTEN_FLESH,   // 6
            Items.BONE,           // 7
            Items.SLIME_BALL      // 8
    };
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

        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            if (!(stack.getItem() instanceof BannerItem)) return;
            if (!hasZunSoulPattern(stack)) return;
            if (stack.isOf(WIItems.ZUN_SOUL_SAIL)) return;
            // 标题
            lines.add(Text.translatable("tooltip.whimsical-ideas.zun_soul_banner.title")
                    .formatted(Formatting.GOLD));

            // 未按 Shift：提示展开
            if (!Screen.hasShiftDown()) {
                lines.add(Text.translatable("tooltip.whimsical-ideas.hold_shift")
                        .formatted(Formatting.GRAY, Formatting.ITALIC));
                return;
            }

            // 按住 Shift：显示配方
            lines.add(Text.translatable("tooltip.whimsical-ideas.zun_soul_banner.hint")
                    .formatted(Formatting.GRAY));

            // 九宫格
            lines.add(Text.literal(" ")
                    .append(name(RING[0])).append(Text.literal("  "))
                    .append(name(RING[1])).append(Text.literal("  "))
                    .append(name(RING[2])).formatted(Formatting.DARK_GRAY));
            lines.add(Text.literal(" ")
                    .append(name(RING[3])).append(Text.literal("  "))
                    .append(Text.translatable("tooltip.whimsical-ideas.zun_soul_banner.center"))
                    .append(Text.literal("  "))
                    .append(name(RING[5])).formatted(Formatting.DARK_GRAY));
            lines.add(Text.literal(" ")
                    .append(name(RING[6])).append(Text.literal("  "))
                    .append(name(RING[7])).append(Text.literal("  "))
                    .append(name(RING[8])).formatted(Formatting.DARK_GRAY));
        });

    }
    private static Text name(Item item) {
        return item == null ? Text.empty() : item.getName();
    }
    @Override
    public void onInitializeClient() {
        // 客户端渲染注册
        System.out.println("[WhimsicalIdeas] Client init OK");
        WIBlockEntities.initClient();
    }
}
