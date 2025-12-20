package com.ignoransel.whimsicalideas.content.soulsail.render;

import com.ignoransel.whimsicalideas.WhimsicalIdeas;
import com.ignoransel.whimsicalideas.entity.SoulBannerBlockEntity;
import net.minecraft.client.render.LightmapTextureManager;
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
    private static final Identifier GLOW_TEX =
            new Identifier("whimsical-ideas", "textures/entity/soul_banner_purple_glow.png");

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


        // ===== 灵珠动画：上下浮动 + 旋转 =====
        // finial 的原始 pivotY 是 -32.0（因为你 cuboid 在 -33/-34 附近）
        // 这里用 “像素单位” 做浮动：0.8 像素左右看起来比较自然
        float t2 = (be.getWorld().getTime() + tickDelta);

        // 上下浮动（像素单位）
        float bob = (float)Math.sin(t2 * 0.10f) * 0.8f;

        // 旋转（弧度，ModelPart 角度是 rad）
        float spin = t2 * 0.06f;

        // 每帧重置到基准值，再叠加动画
        model.finial.pivotY = -2 + bob;  // 你可以微调 -32 让它处在横梁上方
        model.finial.pivotX = 0.0f;
        model.finial.pivotZ = 0.0f;

        // 旋转：推荐绕 Y 轴转，再加一点 roll 更“灵”
        model.finial.yaw  = spin;
        model.finial.pitch = 0.0f;
        model.finial.roll  = 0.0f;



        float t3 = (be.getWorld().getTime() + tickDelta);

        VertexConsumer vaporVc = vcp.getBuffer(RenderLayer.getEntityTranslucent(TEX));
        matrices.push();

        matrices.translate(0.0, -22.0 / 16.0, 0.0);

        float orbitAngle = t3 * 2.0f;
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(orbitAngle));

        float radius = 0.8f;
        float radiusBreath = radius + (float)Math.sin(t3 * 0.05f) * 0.1f;
        matrices.translate(0.0, 0.0, radiusBreath);

        float bob2 = (float)Math.sin(t3 * 0.15f) * 0.1f;
        matrices.translate(0.0, bob2, 0.0);


        float spinAngle = t3 * 3.0f;
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(spinAngle));

        // 3. 渲染第一片
        model.vapor.render(matrices, vaporVc, light, overlay);

        // 4. 渲染第二片 (旋转90度交叉)，形成立体感
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
        model.vapor.render(matrices, vaporVc, light, overlay);

        matrices.pop();

        VertexConsumer vc = vcp.getBuffer(RenderLayer.getEntityCutoutNoCull(TEX));

        // 把所有部件都画出来
        model.pole.render(matrices, vc, light, overlay);
        model.crossbar.render(matrices, vc, light, overlay);
        model.finial.render(matrices, vc, light, overlay);
        // model.chain.render(matrices, vc, light, overlay);
        model.tasselLeft.render(matrices, vc, light, overlay);
        model.tasselRight.render(matrices, vc, light, overlay);
        model.flag.render(matrices, vc, light, overlay);

        VertexConsumer glowVc = vcp.getBuffer(RenderLayer.getEyes(GLOW_TEX));

        // 满亮度（不受环境光影响）
        int fullBright = LightmapTextureManager.MAX_LIGHT_COORDINATE;

        // 只渲染灵石（finial）发光层
        model.finial.render(matrices, glowVc, fullBright, overlay);
        matrices.pop();
    }
}
