package com.ignoransel.whimsicalideas.content.soulsail.test;

import com.ignoransel.whimsicalideas.WhimsicalIdeas;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class SoulBannerItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    private static final Identifier TEX =
            new Identifier(WhimsicalIdeas.MODID, "textures/entity/soul_banner_purple.png");

    private SoulBannerModel model;

    private SoulBannerModel model() {
        if (model == null) {
            var loader = MinecraftClient.getInstance().getEntityModelLoader();
            model = new SoulBannerModel(loader.getModelPart(WhimsicalIdeas.SOUL_BANNER_LAYER));
        }
        return model;
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode,
                       MatrixStack matrices, VertexConsumerProvider vcp, int light, int overlay) {

        matrices.push();

        // 统一以方块中心为原点（物品渲染里也习惯这样做）
        matrices.translate(0.5, 0.5, 0.5);

        // 根据模式做点差异化（你后面想精调就改这些数）
        if (mode == ModelTransformationMode.GUI) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(20f));
            matrices.scale(0.85f, -0.85f, -0.85f);
        } else if (mode == ModelTransformationMode.FIRST_PERSON_RIGHT_HAND
                || mode == ModelTransformationMode.FIRST_PERSON_LEFT_HAND) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f));
            matrices.scale(0.80f, -0.80f, -0.80f);
            matrices.translate(0.0, -0.05, 0.0);
        } else {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f));
            matrices.scale(0.80f, -0.80f, -0.80f);
        }

        SoulBannerModel m = model();

        // 物品不需要像方块那样一直摆动：给一个“微微飘”的静态姿态
        float base = -0.10f;
        m.flag.pitch = base;
        m.flag.yaw = 0.0f;
        m.flag.roll = 0.0f;

        m.tasselLeft.pitch = base * 0.6f;
        m.tasselRight.pitch = base * 0.6f;
        m.chain.pitch = base * 0.4f;

        VertexConsumer vc = vcp.getBuffer(RenderLayer.getEntityCutoutNoCull(TEX));

        // ✅ 物品也把所有部件画出来
        m.pole.render(matrices, vc, light, overlay);
        m.crossbar.render(matrices, vc, light, overlay);
        m.finial.render(matrices, vc, light, overlay);
//        m.chain.render(matrices, vc, light, overlay);
//        m.tasselLeft.render(matrices, vc, light, overlay);
//        m.tasselRight.render(matrices, vc, light, overlay);
        m.flag.render(matrices, vc, light, overlay);

        matrices.pop();
    }
}
