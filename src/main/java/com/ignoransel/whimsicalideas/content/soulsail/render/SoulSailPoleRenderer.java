package com.ignoransel.whimsicalideas.content.soulsail.render;

import com.ignoransel.whimsicalideas.WhimsicalIdeas;
import com.ignoransel.whimsicalideas.content.soulsail.render.SoulBannerModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class SoulSailPoleRenderer implements BlockEntityRenderer<SoulSailPoleBlockEntity> {

    private static final Identifier TEX =
            new Identifier(WhimsicalIdeas.MODID, "textures/entity/soul_banner_purple.png");

    private final SoulBannerModel model;

    public SoulSailPoleRenderer(BlockEntityRendererFactory.Context ctx) {
        this.model = new SoulBannerModel(ctx.getLayerModelPart(WhimsicalIdeas.SOUL_BANNER_LAYER));
    }

    @Override
    public void render(SoulSailPoleBlockEntity be, float tickDelta,
                       MatrixStack matrices, VertexConsumerProvider vcp,
                       int light, int overlay) {

        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        // 和你 banner 一样：像素单位缩放到世界 + 翻转
        matrices.scale(0.6666667F, -0.6666667F, -0.6666667F);

        VertexConsumer vc = vcp.getBuffer(RenderLayer.getEntityCutoutNoCull(TEX));

        // ✅ 杆子方块：只画结构件（不画旗面）
        model.pole.render(matrices, vc, light, overlay);
        model.crossbar.render(matrices, vc, light, overlay);
        model.finial.render(matrices, vc, light, overlay);
        // model.chain.render(matrices, vc, light, overlay);

        matrices.pop();
    }
}
