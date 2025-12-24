package com.ignoransel.whimsicalideas.datagen;

import com.ignoransel.whimsicalideas.content.soulsail.SoulBannerGrade;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.function.Consumer;

public class WIAdvancementProvider extends FabricAdvancementProvider {
    public static final String MODID = "whimsical-ideas";

    // 四字+四字标题（用来生成 lang key 对应文本）
    public static final Map<SoulBannerGrade, String[]> TITLES = Map.of(
            SoulBannerGrade.MORTAL,     new String[]{"凡尘初启", "魂幡立誓"},
            SoulBannerGrade.EARTH,      new String[]{"地脉苏醒", "魂纹成形"},
            SoulBannerGrade.HEAVEN,     new String[]{"天游有路", "灵焰凌空"},
            SoulBannerGrade.MYSTERIOUS, new String[]{"玄机暗涌", "幽纹自现"},
            SoulBannerGrade.YELLOW,     new String[]{"黄道昭昭", "祭魂成章"},
            SoulBannerGrade.UNIVERSE,   new String[]{"宇域开阔", "星魂共鸣"},
            SoulBannerGrade.COSMOS,     new String[]{"宙光流转", "苍魂归一"},
            SoulBannerGrade.FLOOD,      new String[]{"洪潮涌动", "万灵汇聚"},
            SoulBannerGrade.WASTELAND,  new String[]{"荒原不灭", "残魂重燃"},
            SoulBannerGrade.IMMORTAL,   new String[]{"仙路已成", "魂幡登极"}
    );

    public WIAdvancementProvider(FabricDataOutput output) {
        super(output);
    }


    @Override
    public void generateAdvancement(Consumer<Advancement> consumer) {

        // root（tab 入口）
        Identifier rootId = id("soulsail/root");
        Advancement root = Advancement.Builder.create()
                .display(
                        itemId("zun_soul_sail_mortal"),
                        Text.translatable("advancements." + MODID + ".soulsail.root.title"),
                        Text.translatable("advancements." + MODID + ".soulsail.root.desc"),
                        new Identifier("minecraft", "textures/block/obsidian.png"),
                        AdvancementFrame.TASK,
                        false, false, false
                )
                .criterion("tick", net.minecraft.advancement.criterion.TickCriterion.Conditions.createTick())

                .build(consumer, rootId.toString());

        // 10 个等级链式解锁
        Advancement parent = root;
        for (SoulBannerGrade g : SoulBannerGrade.values()) {
            String path = "soulsail/grade/" + g.name().toLowerCase();
            Identifier advId = id(path);

            String titleKey = "advancements." + MODID + "." + path + ".title";
            String descKey  = "advancements." + MODID + "." + path + ".desc";

            Advancement.Builder b = Advancement.Builder.create()
                    .parent(parent)
                    .display(
                            itemId("zun_soul_sail_" + g.name().toLowerCase()), // ✅ A方案：每阶一个物品
                            Text.translatable(titleKey),
                            Text.translatable(descKey),
                            null,
                            // 前 3 个 task，后面 goal/challenge 你随便调
                            (g.getLevel() <= 2) ? AdvancementFrame.TASK
                                    : (g.getLevel() <= 6) ? AdvancementFrame.GOAL
                                    : AdvancementFrame.CHALLENGE,
                            true, true, false
                    );


            b.criterion("has_item", InventoryChangedCriterion.Conditions.items(itemId("zun_soul_sail_"+g.name().toLowerCase())));

            Advancement entry = b.build(consumer, advId.toString());
            parent = entry;
        }
    }

    private static Identifier id(String path) {
        return new Identifier(MODID, path);
    }

    private static Item itemId(String itemPath) {
        Identifier it = new Identifier(MODID, itemPath);
        return Registries.ITEM.get(it);
    }
}
