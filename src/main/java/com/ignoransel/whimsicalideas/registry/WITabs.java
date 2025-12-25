package com.ignoransel.whimsicalideas.registry;

import com.ignoransel.whimsicalideas.WhimsicalIdeas;
import com.ignoransel.whimsicalideas.content.soulsail.SoulBannerGrade;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class WITabs {
    private WITabs() {}

    public static ItemGroup WHIMSICAL_IDEAS_GROUP;
    public static ItemGroup ZUN_SOUL_SAIL_GROUP;
    public static void init() {

        ZUN_SOUL_SAIL_GROUP = Registry.register(
                Registries.ITEM_GROUP,
                new Identifier(WhimsicalIdeas.MODID, "zun_soul_sail"),
                FabricItemGroup.builder()
                        .displayName(Text.translatable("itemGroup.whimsical-ideas.zun_soul_sail"))
                        .icon(() -> new ItemStack(WIItems.ZUN_SOUL_SAIL))
                        .entries((displayContext, entries) -> {
                            entries.add(WIItems.ZUN_SOUL_SAIL);
                            entries.add(WIItems.TEST_ZUN_SOUL_SAIL);
                            entries.add(WIItems.SOUL_SAIL_POLE);
                            entries.add(WIItems.SOUL_FLAG);
                            entries.add(WIItems.SOUL_SIPHON_ITEM);
                            // 添加品阶魂幡
                            entries.add(WIItems.ZUN_SOUL_SAIL_MORTAL.getDefaultStack());
                            entries.add(WIItems.ZUN_SOUL_SAIL_EARTH.getDefaultStack());
                            entries.add(WIItems.ZUN_SOUL_SAIL_HEAVEN.getDefaultStack());
                            entries.add(WIItems.ZUN_SOUL_SAIL_MYSTERIOUS.getDefaultStack());
                            entries.add(WIItems.ZUN_SOUL_SAIL_YELLOW.getDefaultStack());
                            entries.add(WIItems.ZUN_SOUL_SAIL_UNIVERSE.getDefaultStack());
                            entries.add(WIItems.ZUN_SOUL_SAIL_COSMOS.getDefaultStack());
                            entries.add(WIItems.ZUN_SOUL_SAIL_FLOOD.getDefaultStack());
                            entries.add(WIItems.ZUN_SOUL_SAIL_WASTELAND.getDefaultStack());
                            entries.add(WIItems.ZUN_SOUL_SAIL_IMMORTAL.getDefaultStack());

                            entries.add(WIItems.TEST_ZUN_SOUL_SAIL_MORTAL.getDefaultStack());
                            entries.add(WIItems.TEST_ZUN_SOUL_SAIL_EARTH.getDefaultStack());
                            entries.add(WIItems.TEST_ZUN_SOUL_SAIL_HEAVEN.getDefaultStack());
                            entries.add(WIItems.TEST_ZUN_SOUL_SAIL_MYSTERIOUS.getDefaultStack());
                            entries.add(WIItems.TEST_ZUN_SOUL_SAIL_YELLOW.getDefaultStack());
                            entries.add(WIItems.TEST_ZUN_SOUL_SAIL_UNIVERSE.getDefaultStack());
                            entries.add(WIItems.TEST_ZUN_SOUL_SAIL_COSMOS.getDefaultStack());
                            entries.add(WIItems.TEST_ZUN_SOUL_SAIL_FLOOD.getDefaultStack());
                            entries.add(WIItems.TEST_ZUN_SOUL_SAIL_WASTELAND.getDefaultStack());
                            entries.add(WIItems.TEST_ZUN_SOUL_SAIL_IMMORTAL.getDefaultStack());
                        })
                        .build()
        );

        WHIMSICAL_IDEAS_GROUP = Registry.register(
                Registries.ITEM_GROUP,
                new Identifier(WhimsicalIdeas.MODID, "whimsical_ideas"),
                FabricItemGroup.builder()
                        .displayName(Text.translatable("itemGroup.whimsical-ideas.whimsical_ideas"))
                        .icon(() -> new ItemStack(WIItems.SOUL_TABLET_ITEM))
                        .entries((displayContext, entries) -> {
                            entries.add(WIItems.SOUL_TABLET_ITEM);
                            entries.add(WIItems.SOUL_TABLET_IRON_ITEM);
                            entries.add(WIItems.SOUL_TABLET_GOLD_ITEM);
                            entries.add(WIItems.SOUL_TABLET_DIAMOND_ITEM);
                            entries.add(WIItems.SOUL_TABLET_NETHERITE_ITEM);
                            entries.add(WIItems.HEX_FORGE_IRON);
                            entries.add(WIItems.HEX_FORGE_GOLD);
                            entries.add(WIItems.HEX_FORGE_DIAMOND);
                            entries.add(WIItems.HEX_FORGE_NETHERITE);
                            entries.add(WIItems.HEX_FORGE_RANDOM);
                        })
                        .build()
        );
    }
}
