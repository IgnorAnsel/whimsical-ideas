package com.ignoransel.whimsicalideas.registry;

import com.ignoransel.whimsicalideas.WhimsicalIdeas;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class WITabs {
    private WITabs() {}

    public static ItemGroup WHIMSICAL_IDEAS_GROUP;

    public static void init() {
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
                            entries.add(WIItems.ZUN_SOUL_SAIL);
                            entries.add(WIItems.TEST_ZUN_SOUL_SAIL);
                            entries.add(WIItems.SOUL_SAIL_POLE);
                            entries.add(WIItems.SOUL_FLAG);
                        })
                        .build()
        );
    }
}
