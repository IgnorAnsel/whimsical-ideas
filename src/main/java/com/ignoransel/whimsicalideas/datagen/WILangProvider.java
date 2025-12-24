package com.ignoransel.whimsicalideas.datagen;

import com.ignoransel.whimsicalideas.content.soulsail.SoulBannerGrade;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class WILangProvider extends FabricLanguageProvider {
    public static final String MODID = "whimsical-ideas";

    public WILangProvider(FabricDataOutput out) {
        super(out, "zh_cn");
    }

    @Override
    public void generateTranslations(TranslationBuilder tb) {
        tb.add("advancements." + MODID + ".soulsail.root.title", "魂帆之路");
        tb.add("advancements." + MODID + ".soulsail.root.desc",  "灵魂，开始汇聚。");

        for (SoulBannerGrade g : SoulBannerGrade.values()) {
            String path = "soulsail/grade/" + g.name().toLowerCase();
            String[] t = WIAdvancementProvider.TITLES.get(g);

            // 四字 + 四字（中间加空格更好看，也可以不加）
            tb.add("advancements." + MODID + "." + path + ".title", t[0] + " " + t[1]);
            tb.add("advancements." + MODID + "." + path + ".desc",  "获得「" + g.getDisplayName() + "」魂幡阶位。");
        }
    }
}
