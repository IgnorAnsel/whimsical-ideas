package com.ignoransel.whimsicalideas.content.soulsail.test;

import com.ignoransel.whimsicalideas.WhimsicalIdeas;
import com.ignoransel.whimsicalideas.entity.SoulBannerBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class SoulBannerRenderer implements BlockEntityRenderer<SoulBannerBlockEntity> {
    private static final Identifier TEX =
            new Identifier("whimsical-ideas", "textures/entity/soul_banner_purple.png");

    private final SoulBannerModel model;

    public SoulBannerRenderer(BlockEntityRendererFactory.Context ctx) {
        this.model = new SoulBannerModel(ctx.getLayerModelPart(WhimsicalIdeas.SOUL_BANNER_LAYER));
    }

    @Override
    public void render(SoulBannerBlockEntity be, float tickDelta,
                       MatrixStack matrices, VertexConsumerProvider vcp,
                       int light, int overlay) {

        if (be.getWorld() == null) return;

        matrices.push();

        // 方块中心
        matrices.translate(0.5, 0.5, 0.5);

        // 站立旋转
        var state = be.getCachedState();
        if (state.contains(SoulBannerBlock.ROTATION)) {
            int rot = state.get(SoulBannerBlock.ROTATION);
            float yaw = -(rot * 360.0f / 16.0f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yaw));

            // 这个值你之前觉得矮就调小甚至删掉
            matrices.translate(0.0, -0.08, 0.0);
        }

        // 像素模型缩放到世界 + 翻转
        matrices.scale(0.6666667F, -0.6666667F, -0.6666667F);

        // 动画：主旗面摆动
        float t = be.getWorld().getTime() + tickDelta;
        float wave = (float) Math.sin(t * 0.10f) * 0.12f;
        float base = -0.10f;

        model.flag.pitch = base + wave * 0.6f;
        model.flag.yaw = 0.0f;
        model.flag.roll = wave * 0.12f;

        // 流苏/链条跟着轻微动一点
        model.tasselLeft.pitch = wave;
        model.tasselRight.pitch = wave;
        // model.chain.pitch = wave * 0.4f;

        VertexConsumer vc = vcp.getBuffer(RenderLayer.getEntityCutoutNoCull(TEX));

        // ✅ 把所有部件都画出来
        model.pole.render(matrices, vc, light, overlay);
        model.crossbar.render(matrices, vc, light, overlay);
        model.finial.render(matrices, vc, light, overlay);
        // model.chain.render(matrices, vc, light, overlay);
        model.tasselLeft.render(matrices, vc, light, overlay);
        model.tasselRight.render(matrices, vc, light, overlay);
        model.flag.render(matrices, vc, light, overlay);

        matrices.pop();
    }
}
