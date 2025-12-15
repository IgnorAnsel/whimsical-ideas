// WIPatterns.java
package com.ignoransel.whimsicalideas.registry;

import com.ignoransel.whimsicalideas.WhimsicalIdeas;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class WIPatterns {
    public static final Identifier ZUN_SOUL_ID = new Identifier(WhimsicalIdeas.MODID, "zun_soul");

    public static void init() {
        // 关键：BannerPattern 里存的是“pattern id 字符串”
        Registry.register(Registries.BANNER_PATTERN, ZUN_SOUL_ID, new BannerPattern("zun_soul"));
    }
}
