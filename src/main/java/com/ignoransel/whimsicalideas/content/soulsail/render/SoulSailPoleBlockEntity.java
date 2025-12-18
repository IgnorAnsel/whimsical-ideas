package com.ignoransel.whimsicalideas.content.soulsail.render;

import com.ignoransel.whimsicalideas.registry.WIBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class SoulSailPoleBlockEntity extends BlockEntity {
    public SoulSailPoleBlockEntity(BlockPos pos, BlockState state) {
        super(WIBlockEntities.SOUL_SAIL_POLE_BE, pos, state);
    }
}
