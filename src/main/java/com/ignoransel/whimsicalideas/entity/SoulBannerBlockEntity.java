package com.ignoransel.whimsicalideas.entity;

import com.ignoransel.whimsicalideas.registry.WIBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class SoulBannerBlockEntity extends BlockEntity {
    public SoulBannerBlockEntity(BlockPos pos, BlockState state) {
        super(WIBlockEntities.SOUL_BANNER_BE, pos, state);
    }
}

