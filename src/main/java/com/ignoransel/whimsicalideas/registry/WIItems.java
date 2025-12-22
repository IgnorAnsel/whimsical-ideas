package com.ignoransel.whimsicalideas.registry;

import com.ignoransel.whimsicalideas.WhimsicalIdeas;

import com.ignoransel.whimsicalideas.content.hex.HexRarity;
import com.ignoransel.whimsicalideas.content.hex.item.HexForgeItem;
import com.ignoransel.whimsicalideas.content.hex.item.RandomHexForgeItem;
import com.ignoransel.whimsicalideas.content.soulsail.*;
import com.ignoransel.whimsicalideas.content.soulsail.render.TestSoulSailBannerItem;
import com.ignoransel.whimsicalideas.content.soultablet.SoulTabletItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public final class WIItems {
    private WIItems(){}

    public static Item SOUL_TABLET_ITEM;

    public static Item SOUL_TABLET_IRON_ITEM;
    public static Item SOUL_TABLET_GOLD_ITEM;
    public static Item SOUL_TABLET_DIAMOND_ITEM;
    public static Item SOUL_TABLET_NETHERITE_ITEM;


    public static Item ZUN_SOUL_SAIL_MORTAL = registerGradeBanner(SoulBannerGrade.MORTAL);
    public static Item ZUN_SOUL_SAIL_EARTH = registerGradeBanner(SoulBannerGrade.EARTH);
    public static Item ZUN_SOUL_SAIL_HEAVEN = registerGradeBanner(SoulBannerGrade.HEAVEN);
    public static Item ZUN_SOUL_SAIL_MYSTERIOUS = registerGradeBanner(SoulBannerGrade.MYSTERIOUS);
    public static Item ZUN_SOUL_SAIL_YELLOW = registerGradeBanner(SoulBannerGrade.YELLOW);
    public static Item ZUN_SOUL_SAIL_UNIVERSE = registerGradeBanner(SoulBannerGrade.UNIVERSE);
    public static Item ZUN_SOUL_SAIL_COSMOS = registerGradeBanner(SoulBannerGrade.COSMOS);
    public static Item ZUN_SOUL_SAIL_FLOOD = registerGradeBanner(SoulBannerGrade.FLOOD);
    public static Item ZUN_SOUL_SAIL_WASTELAND = registerGradeBanner(SoulBannerGrade.WASTELAND);
    public static Item ZUN_SOUL_SAIL_IMMORTAL = registerGradeBanner(SoulBannerGrade.IMMORTAL);


    public static Item TEST_ZUN_SOUL_SAIL_MORTAL = registerTestGradeBanner(SoulBannerGrade.MORTAL);
    public static Item TEST_ZUN_SOUL_SAIL_EARTH = registerTestGradeBanner(SoulBannerGrade.EARTH);
    public static Item TEST_ZUN_SOUL_SAIL_HEAVEN = registerTestGradeBanner(SoulBannerGrade.HEAVEN);
    public static Item TEST_ZUN_SOUL_SAIL_MYSTERIOUS = registerTestGradeBanner(SoulBannerGrade.MYSTERIOUS);
    public static Item TEST_ZUN_SOUL_SAIL_YELLOW = registerTestGradeBanner(SoulBannerGrade.YELLOW);
    public static Item TEST_ZUN_SOUL_SAIL_UNIVERSE = registerTestGradeBanner(SoulBannerGrade.UNIVERSE);
    public static Item TEST_ZUN_SOUL_SAIL_COSMOS = registerTestGradeBanner(SoulBannerGrade.COSMOS);
    public static Item TEST_ZUN_SOUL_SAIL_FLOOD = registerTestGradeBanner(SoulBannerGrade.FLOOD);
    public static Item TEST_ZUN_SOUL_SAIL_WASTELAND = registerTestGradeBanner(SoulBannerGrade.WASTELAND);
    public static Item TEST_ZUN_SOUL_SAIL_IMMORTAL = registerTestGradeBanner(SoulBannerGrade.IMMORTAL);

    public static Item HEX_FORGE_IRON;
    public static Item HEX_FORGE_GOLD;
    public static Item HEX_FORGE_DIAMOND;
    public static Item HEX_FORGE_NETHERITE;
    public static Item HEX_FORGE_RANDOM;



    public static Map<SoulBannerGrade, Item> GRADE_BANNERS = new HashMap<>();
    public static Map<SoulBannerGrade, Item> TEST_GRADE_BANNERS = new HashMap<>();
    static {
        // 初始化映射
        GRADE_BANNERS.put(SoulBannerGrade.MORTAL, ZUN_SOUL_SAIL_MORTAL);
        GRADE_BANNERS.put(SoulBannerGrade.EARTH, ZUN_SOUL_SAIL_EARTH);
        GRADE_BANNERS.put(SoulBannerGrade.HEAVEN, ZUN_SOUL_SAIL_HEAVEN);
        GRADE_BANNERS.put(SoulBannerGrade.MYSTERIOUS, ZUN_SOUL_SAIL_MYSTERIOUS);
        GRADE_BANNERS.put(SoulBannerGrade.YELLOW, ZUN_SOUL_SAIL_YELLOW);
        GRADE_BANNERS.put(SoulBannerGrade.UNIVERSE, ZUN_SOUL_SAIL_UNIVERSE);
        GRADE_BANNERS.put(SoulBannerGrade.COSMOS, ZUN_SOUL_SAIL_COSMOS);
        GRADE_BANNERS.put(SoulBannerGrade.FLOOD, ZUN_SOUL_SAIL_FLOOD);
        GRADE_BANNERS.put(SoulBannerGrade.WASTELAND, ZUN_SOUL_SAIL_WASTELAND);
        GRADE_BANNERS.put(SoulBannerGrade.IMMORTAL, ZUN_SOUL_SAIL_IMMORTAL);
    }

    static {
        // 初始化映射
        TEST_GRADE_BANNERS.put(SoulBannerGrade.MORTAL, TEST_ZUN_SOUL_SAIL_MORTAL);
        TEST_GRADE_BANNERS.put(SoulBannerGrade.EARTH, TEST_ZUN_SOUL_SAIL_EARTH);
        TEST_GRADE_BANNERS.put(SoulBannerGrade.HEAVEN, TEST_ZUN_SOUL_SAIL_HEAVEN);
        TEST_GRADE_BANNERS.put(SoulBannerGrade.MYSTERIOUS, TEST_ZUN_SOUL_SAIL_MYSTERIOUS);
        TEST_GRADE_BANNERS.put(SoulBannerGrade.YELLOW, TEST_ZUN_SOUL_SAIL_YELLOW);
        TEST_GRADE_BANNERS.put(SoulBannerGrade.UNIVERSE, TEST_ZUN_SOUL_SAIL_UNIVERSE);
        TEST_GRADE_BANNERS.put(SoulBannerGrade.COSMOS, TEST_ZUN_SOUL_SAIL_COSMOS);
        TEST_GRADE_BANNERS.put(SoulBannerGrade.FLOOD, TEST_ZUN_SOUL_SAIL_FLOOD);
        TEST_GRADE_BANNERS.put(SoulBannerGrade.WASTELAND, TEST_ZUN_SOUL_SAIL_WASTELAND);
        TEST_GRADE_BANNERS.put(SoulBannerGrade.IMMORTAL, TEST_ZUN_SOUL_SAIL_IMMORTAL);
    }

    private static Item registerTestGradeBanner(SoulBannerGrade grade) {
        return Registry.register(
                Registries.ITEM,
                new Identifier(WhimsicalIdeas.MODID, "test_zun_soul_sail_" + grade.name().toLowerCase()),
                new TestSoulSailBannerItem(
                        WIBlocks.TEST_ZUN_SOUL_BANNER,
                        new FabricItemSettings().maxCount(1),
                        SoulSailTier.MORTAL
                ) {
                    @Override
                    public ItemStack getDefaultStack() {
                        ItemStack stack = super.getDefaultStack();
                        SoulSailItemCompat.setBannerGrade(stack, grade);
                        long refinedSouls = getRefinedSoulsForGrade(grade);
                        SoulSailItemCompat.addRefinedSouls(stack, refinedSouls, grade.getSoulCapacity());
                        NbtCompound bet = stack.getOrCreateSubNbt("BlockEntityTag");
                        bet.putInt(SoulSailKeys.BANNER_GRADE, grade.getLevel());
                        setTier(grade.getSoulSailTier());
                        return stack;
                    }


                    // 重写物品名称显示
                    @Override
                    public String getTranslationKey() {
                        return "item.whimsical-ideas.zun_soul_sail";
                    }
                }
        );
    }

    private static Item registerGradeBanner(SoulBannerGrade grade) {
        return Registry.register(
                Registries.ITEM,
                new Identifier(WhimsicalIdeas.MODID, "zun_soul_sail_" + grade.name().toLowerCase()),
                new SoulSailBannerItem(
                        WIBlocks.ZUN_SOUL_BANNER,
                        WIBlocks.ZUN_SOUL_WALL_BANNER,
                        new FabricItemSettings().maxCount(1),
                        SoulSailTier.MORTAL
                ) {
                    @Override
                    public ItemStack getDefaultStack() {
                        ItemStack stack = super.getDefaultStack();
                        SoulSailItemCompat.setBannerGrade(stack, grade);
                        long refinedSouls = getRefinedSoulsForGrade(grade);
                        SoulSailItemCompat.addRefinedSouls(stack, refinedSouls, grade.getSoulCapacity());
                        NbtCompound bet = stack.getOrCreateSubNbt("BlockEntityTag");
                        bet.putInt(SoulSailKeys.BANNER_GRADE, grade.getLevel());
                        setTier(grade.getSoulSailTier());
                        return stack;
                    }


                    // 重写物品名称显示
                    @Override
                    public String getTranslationKey() {
                        return "item.whimsical-ideas.zun_soul_sail";
                    }
                }
        );
    }

    private static void registerHexForge() {
        HEX_FORGE_IRON = Registry.register(
                Registries.ITEM,
                new Identifier(WhimsicalIdeas.MODID, "hex_forge_iron"),
                new HexForgeItem(new FabricItemSettings().maxCount(1), HexRarity.IRON)
        );
        HEX_FORGE_GOLD = Registry.register(
                Registries.ITEM,
                new Identifier(WhimsicalIdeas.MODID, "hex_forge_gold"),
                new HexForgeItem(new FabricItemSettings().maxCount(1), HexRarity.GOLD)
        );
        HEX_FORGE_DIAMOND = Registry.register(
                Registries.ITEM,
                new Identifier(WhimsicalIdeas.MODID, "hex_forge_diamond"),
                new HexForgeItem(new FabricItemSettings().maxCount(1), HexRarity.DIAMOND)
        );
        HEX_FORGE_NETHERITE = Registry.register(
                Registries.ITEM,
                new Identifier(WhimsicalIdeas.MODID, "hex_forge_netherite"),
                new HexForgeItem(new FabricItemSettings().maxCount(1), HexRarity.NETHERITE)
        );
        HEX_FORGE_RANDOM = Registry.register(
                Registries.ITEM,
                new Identifier(WhimsicalIdeas.MODID, "hex_forge_random"),
                new RandomHexForgeItem(new FabricItemSettings().maxCount(16))
        );

    }


    public static final Item ZUN_SOUL_SAIL =
            Registry.register(Registries.ITEM, new Identifier(WhimsicalIdeas.MODID, "zun_soul_sail"),
                    new SoulSailBannerItem(
                            WIBlocks.ZUN_SOUL_BANNER,
                            WIBlocks.ZUN_SOUL_WALL_BANNER,
                            new FabricItemSettings().maxCount(1),
                            SoulSailTier.MORTAL
                    ));

    public static final Item TEST_ZUN_SOUL_SAIL = Registry.register(
            Registries.ITEM,
            new Identifier(WhimsicalIdeas.MODID, "test_zun_soul_sail"),
            new TestSoulSailBannerItem(WIBlocks.TEST_ZUN_SOUL_BANNER, new FabricItemSettings(), SoulSailTier.MORTAL)
    );
    public static final Item SOUL_SAIL_POLE = Registry.register(
            Registries.ITEM,
            id("soul_sail_pole"),
            new BlockItem(WIBlocks.SOUL_SAIL_POLE, new FabricItemSettings())
    );
    public static final Item SOUL_FLAG = Registry.register(
            Registries.ITEM,
            new Identifier(WhimsicalIdeas.MODID, "soul_flag"),
            new Item(new FabricItemSettings().maxCount(64))
    );

    private static Item register(String id, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(WhimsicalIdeas.MODID, id), item);
    }

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
        registerHexForge();
    }

    private static Identifier id(String path){
        return new Identifier(WhimsicalIdeas.MODID, path);
    }

    private static long getRefinedSoulsForGrade(SoulBannerGrade grade) {
        return (long) Math.pow(10, grade.getLevel());
    }


}
