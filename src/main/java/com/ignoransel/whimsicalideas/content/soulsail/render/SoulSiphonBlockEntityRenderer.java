package com.ignoransel.whimsicalideas.content.soulsail.render;

import com.ignoransel.whimsicalideas.content.soulsail.entity.SoulSiphonBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

public class SoulSiphonBlockEntityRenderer implements BlockEntityRenderer<SoulSiphonBlockEntity> {

    public SoulSiphonBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(SoulSiphonBlockEntity be, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vcp, int light, int overlay) {

        ItemStack banner = be.getBannerStack();
        if (banner.isEmpty()) return;
        // 轻微浮动 + 旋转
        float t = (be.getWorld() == null ? 0 : (be.getWorld().getTime() + tickDelta));
        float bob = (float) (Math.sin(t * 0.10) * 0.03);     // 上下浮动幅度
        float rot = t * 2.5f;                                // 旋转速度

        matrices.push();

        // 位置：方块中心顶部一点
        matrices.translate(0.5, 1.05 + bob, 0.5);

        // 旋转（绕Y轴）
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rot));

        // 缩放：避免太大挡视线（可调 0.6~0.9）
        float s = 0.75f;
        matrices.scale(s, s, s);

        // 渲染 item（GROUND/GUI/FIXED 都行，GROUND看起来像放地上）
        MinecraftClient.getInstance().getItemRenderer().renderItem(
                banner,
                ModelTransformationMode.GROUND,
                light,
                overlay,
                matrices,
                vcp,
                be.getWorld(),
                0
        );

        matrices.pop();
    }
}
