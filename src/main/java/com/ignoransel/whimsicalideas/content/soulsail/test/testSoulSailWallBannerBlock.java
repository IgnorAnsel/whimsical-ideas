package com.ignoransel.whimsicalideas.content.soulsail.test;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.WallBannerBlock;
import net.minecraft.util.DyeColor;

public class testSoulSailWallBannerBlock extends WallBannerBlock {
    public testSoulSailWallBannerBlock(AbstractBlock.Settings settings) {
        super(DyeColor.BLACK, settings);
    }
}