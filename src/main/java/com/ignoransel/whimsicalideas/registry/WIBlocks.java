package com.ignoransel.whimsicalideas.registry;

import com.ignoransel.whimsicalideas.WhimsicalIdeas;
import com.ignoransel.whimsicalideas.content.soultablet.*;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class WIBlocks {
    private WIBlocks(){}

    public static Block SOUL_TABLET;
    public static Block SOUL_TABLET_WALL;
    public static Block SOUL_TABLET_BROKEN;
    public static Block SOUL_TABLET_BROKEN_WALL;

    // ===== 强化：铁 =====
    public static Block SOUL_TABLET_IRON;
    public static Block SOUL_TABLET_IRON_WALL;
    public static Block SOUL_TABLET_IRON_BROKEN;
    public static Block SOUL_TABLET_IRON_BROKEN_WALL;

    // ===== 强化：金 =====
    public static Block SOUL_TABLET_GOLD;
    public static Block SOUL_TABLET_GOLD_WALL;
    public static Block SOUL_TABLET_GOLD_BROKEN;
    public static Block SOUL_TABLET_GOLD_BROKEN_WALL;

    // ===== 强化：钻石 =====
    public static Block SOUL_TABLET_DIAMOND;
    public static Block SOUL_TABLET_DIAMOND_WALL;
    public static Block SOUL_TABLET_DIAMOND_BROKEN;
    public static Block SOUL_TABLET_DIAMOND_BROKEN_WALL;

    // ===== 强化：下界合金 =====
    public static Block SOUL_TABLET_NETHERITE;
    public static Block SOUL_TABLET_NETHERITE_WALL;
    public static Block SOUL_TABLET_NETHERITE_BROKEN;
    public static Block SOUL_TABLET_NETHERITE_BROKEN_WALL;

    public static void init() {
        var settings = AbstractBlock.Settings.copy(Blocks.OAK_SIGN)
                .nonOpaque()
                .noCollision();

        // 普通
        SOUL_TABLET = reg("soul_tablet", new SoulTabletBlock(settings));
        SOUL_TABLET_WALL = reg("soul_tablet_wall", new SoulTabletWallBlock(settings));
        SOUL_TABLET_BROKEN = reg("soul_tablet_broken", new SoulTabletBrokenBlock(settings));
        SOUL_TABLET_BROKEN_WALL = reg("soul_tablet_broken_wall", new SoulTabletBrokenWallBlock(settings));

        // 铁
        SOUL_TABLET_IRON = reg("soul_tablet_iron", new SoulTabletBlock(settings));
        SOUL_TABLET_IRON_WALL = reg("soul_tablet_iron_wall", new SoulTabletWallBlock(settings));
        SOUL_TABLET_IRON_BROKEN = reg("soul_tablet_iron_broken", new SoulTabletBrokenBlock(settings));
        SOUL_TABLET_IRON_BROKEN_WALL = reg("soul_tablet_iron_broken_wall", new SoulTabletBrokenWallBlock(settings));

        // 金
        SOUL_TABLET_GOLD = reg("soul_tablet_gold", new SoulTabletBlock(settings));
        SOUL_TABLET_GOLD_WALL = reg("soul_tablet_gold_wall", new SoulTabletWallBlock(settings));
        SOUL_TABLET_GOLD_BROKEN = reg("soul_tablet_gold_broken", new SoulTabletBrokenBlock(settings));
        SOUL_TABLET_GOLD_BROKEN_WALL = reg("soul_tablet_gold_broken_wall", new SoulTabletBrokenWallBlock(settings));

        // 钻石
        SOUL_TABLET_DIAMOND = reg("soul_tablet_diamond", new SoulTabletBlock(settings));
        SOUL_TABLET_DIAMOND_WALL = reg("soul_tablet_diamond_wall", new SoulTabletWallBlock(settings));
        SOUL_TABLET_DIAMOND_BROKEN = reg("soul_tablet_diamond_broken", new SoulTabletBrokenBlock(settings));
        SOUL_TABLET_DIAMOND_BROKEN_WALL = reg("soul_tablet_diamond_broken_wall", new SoulTabletBrokenWallBlock(settings));

        // 下界合金
        SOUL_TABLET_NETHERITE = reg("soul_tablet_netherite", new SoulTabletBlock(settings));
        SOUL_TABLET_NETHERITE_WALL = reg("soul_tablet_netherite_wall", new SoulTabletWallBlock(settings));
        SOUL_TABLET_NETHERITE_BROKEN = reg("soul_tablet_netherite_broken", new SoulTabletBrokenBlock(settings));
        SOUL_TABLET_NETHERITE_BROKEN_WALL = reg("soul_tablet_netherite_broken_wall", new SoulTabletBrokenWallBlock(settings));
    }

    private static Block reg(String path, Block block) {
        return Registry.register(Registries.BLOCK, id(path), block);
    }

    private static Identifier id(String path){
        return new Identifier(WhimsicalIdeas.MODID, path);
    }
}
