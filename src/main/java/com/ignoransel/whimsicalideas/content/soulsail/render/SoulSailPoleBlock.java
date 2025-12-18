package com.ignoransel.whimsicalideas.content.soulsail.render;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class SoulSailPoleBlock extends BlockWithEntity {
    public SoulSailPoleBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SoulSailPoleBlockEntity(pos, state);
    }

    // ✅ 让方块本体不走 JSON 烘焙模型渲染，完全交给 BER
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }
}
