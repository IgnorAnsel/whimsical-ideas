package com.ignoransel.whimsicalideas.registry;

import com.ignoransel.whimsicalideas.WhimsicalIdeas;
import com.ignoransel.whimsicalideas.content.soulsail.SoulSailBannerBlock;
import com.ignoransel.whimsicalideas.content.soulsail.SoulSailWallBannerBlock;
import com.ignoransel.whimsicalideas.content.soulsail.block.SoulSiphonBlock;
import com.ignoransel.whimsicalideas.content.soulsail.render.SoulBannerBlock;
import com.ignoransel.whimsicalideas.content.soulsail.render.SoulSailPoleBlock;
import com.ignoransel.whimsicalideas.content.soultablet.*;
import com.ignoransel.whimsicalideas.content.tuningfork.TuningForkBlock;
import com.ignoransel.whimsicalideas.mixin.BlockEntityTypeAccessor;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

public final class WIBlocks {
    private WIBlocks(){}
    public static final Block TUNING_FORK_BLOCK = Registry.register(
            Registries.BLOCK,
            id("tuning_fork"),
            new TuningForkBlock(
                    AbstractBlock.Settings.create()
                            .strength(0.2f)
                            .nonOpaque()
            )
            );

    public static final Block ZUN_SOUL_BANNER =
            register("zun_soul_banner", new SoulSailBannerBlock(AbstractBlock.Settings.copy(Blocks.BLACK_BANNER)));

    public static final Block ZUN_SOUL_WALL_BANNER =
            register("zun_soul_wall_banner", new SoulSailWallBannerBlock(AbstractBlock.Settings.copy(Blocks.BLACK_WALL_BANNER)));
    public static final Block TEST_ZUN_SOUL_BANNER = Registry.register(
            Registries.BLOCK,
            new Identifier(WhimsicalIdeas.MODID, "test_zun_soul_banner"),
            new SoulBannerBlock(FabricBlockSettings.copyOf(Blocks.BLACK_BANNER).nonOpaque())
    );

    public static final Block TEST_ZUN_SOUL_WALL_BANNER =
            register("test_zun_soul_wall_banner", new SoulSailWallBannerBlock(AbstractBlock.Settings.copy(Blocks.BLACK_WALL_BANNER)));
    public static final Block SOUL_SAIL_POLE = Registry.register(
            Registries.BLOCK,
            id("soul_sail_pole"),
            new SoulSailPoleBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE).nonOpaque())
    );

    public static final Block SOUL_SIPHON = Registry.register(Registries.BLOCK,
            id("soul_siphon"),
            new SoulSiphonBlock(FabricBlockSettings.copyOf(Blocks.OBSIDIAN).strength(3.5f).nonOpaque()));

    private static Block register(String id, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier(WhimsicalIdeas.MODID, id), block);
    }

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

        BlockEntityTypeAccessor acc = (BlockEntityTypeAccessor) (Object) BlockEntityType.BANNER;

        Set<Block> newSet = new HashSet<>(acc.whimsicalideas$getBlocks());
        newSet.add(WIBlocks.ZUN_SOUL_BANNER);
        newSet.add(WIBlocks.ZUN_SOUL_WALL_BANNER);
        newSet.add(WIBlocks.TEST_ZUN_SOUL_BANNER);
        newSet.add(WIBlocks.TEST_ZUN_SOUL_WALL_BANNER);
        acc.whimsicalideas$setBlocks(newSet);

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
