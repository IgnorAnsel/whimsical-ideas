package com.ignoransel.whimsicalideas.registry;

import com.ignoransel.whimsicalideas.WhimsicalIdeas;

import com.ignoransel.whimsicalideas.content.hex.HexRarity;
import com.ignoransel.whimsicalideas.content.hex.item.HexForgeItem;
import com.ignoransel.whimsicalideas.content.hex.item.RandomHexForgeItem;
import com.ignoransel.whimsicalideas.content.soultablet.SoulTabletItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Arrays;

public final class WIItems {
    private WIItems(){}

    public static Item SOUL_TABLET_ITEM;

    public static Item SOUL_TABLET_IRON_ITEM;
    public static Item SOUL_TABLET_GOLD_ITEM;
    public static Item SOUL_TABLET_DIAMOND_ITEM;
    public static Item SOUL_TABLET_NETHERITE_ITEM;

    public static final Item HEX_FORGE_IRON = Registry.register(
            Registries.ITEM,
            new Identifier("whimsical-ideas", "hex_forge_iron"),
            new HexForgeItem(new FabricItemSettings().maxCount(1), HexRarity.IRON)
    );

    public static final Item HEX_FORGE_GOLD = Registry.register(
            Registries.ITEM,
            new Identifier("whimsical-ideas", "hex_forge_gold"),
            new HexForgeItem(new FabricItemSettings().maxCount(1), HexRarity.GOLD)
    );

    public static final Item HEX_FORGE_DIAMOND = Registry.register(
            Registries.ITEM,
            new Identifier("whimsical-ideas", "hex_forge_diamond"),
            new HexForgeItem(new FabricItemSettings().maxCount(1), HexRarity.DIAMOND)
    );

    public static final Item HEX_FORGE_NETHERITE = Registry.register(
            Registries.ITEM,
            new Identifier("whimsical-ideas", "hex_forge_netherite"),
            new HexForgeItem(new FabricItemSettings().maxCount(1), HexRarity.NETHERITE)
    );

    public static final Item HEX_FORGE_RANDOM = Registry.register(
            Registries.ITEM,
            new Identifier("whimsical-ideas", "hex_forge_random"),
            new RandomHexForgeItem(new FabricItemSettings().maxCount(16))
    );
    public static void init() {

        SOUL_TABLET_ITEM = Registry.register(
                Registries.ITEM,
                id("soul_tablet"),
                new SoulTabletItem(WIBlocks.SOUL_TABLET, WIBlocks.SOUL_TABLET_WALL,
                        new FabricItemSettings().maxCount(1))
        );

        SOUL_TABLET_IRON_ITEM = Registry.register(
                Registries.ITEM,
                id("soul_tablet_iron"),
                new SoulTabletItem(WIBlocks.SOUL_TABLET_IRON, WIBlocks.SOUL_TABLET_IRON_WALL,
                        new FabricItemSettings().maxCount(1))
        );

        SOUL_TABLET_GOLD_ITEM = Registry.register(
                Registries.ITEM,
                id("soul_tablet_gold"),
                new SoulTabletItem(WIBlocks.SOUL_TABLET_GOLD, WIBlocks.SOUL_TABLET_GOLD_WALL,
                        new FabricItemSettings().maxCount(1))
        );

        SOUL_TABLET_DIAMOND_ITEM = Registry.register(
                Registries.ITEM,
                id("soul_tablet_diamond"),
                new SoulTabletItem(WIBlocks.SOUL_TABLET_DIAMOND, WIBlocks.SOUL_TABLET_DIAMOND_WALL,
                        new FabricItemSettings().maxCount(1))
        );

        SOUL_TABLET_NETHERITE_ITEM = Registry.register(
                Registries.ITEM,
                id("soul_tablet_netherite"),
                new SoulTabletItem(WIBlocks.SOUL_TABLET_NETHERITE, WIBlocks.SOUL_TABLET_NETHERITE_WALL,
                        new FabricItemSettings().maxCount(1))
        );
    }

    private static Identifier id(String path){
        return new Identifier(WhimsicalIdeas.MODID, path);
    }
}
