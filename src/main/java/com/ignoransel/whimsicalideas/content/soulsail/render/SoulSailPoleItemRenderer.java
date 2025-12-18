package com.ignoransel.whimsicalideas.content.soulsail.render;

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

public class SoulSailPoleItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {

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

        matrices.translate(0.5, 0.5, 0.5);

        if (mode == ModelTransformationMode.GUI) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(20f));
            matrices.scale(0.85f, -0.85f, -0.85f);
        } else {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f));
            matrices.scale(0.80f, -0.80f, -0.80f);
            matrices.translate(0.0, -0.05, 0.0);
        }

        // 只画杆子相关件
        SoulBannerModel m = model();
        VertexConsumer vc = vcp.getBuffer(RenderLayer.getEntityCutoutNoCull(TEX));

        m.pole.render(matrices, vc, light, overlay);
        m.crossbar.render(matrices, vc, light, overlay);
        m.finial.render(matrices, vc, light, overlay);

        matrices.pop();
    }
}
