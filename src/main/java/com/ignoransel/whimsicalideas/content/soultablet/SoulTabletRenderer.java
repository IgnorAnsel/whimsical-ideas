package com.ignoransel.whimsicalideas.content.soultablet;

import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class SoulTabletRenderer implements BlockEntityRenderer<SoulTabletBlockEntity> {

    private final TextRenderer textRenderer;

    public SoulTabletRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    @Override
    public void render(
            SoulTabletBlockEntity be,
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertices,
            int light,
            int overlay
    ) {

        if (be.getOwnerName() == null) return;

        BlockState state = be.getCachedState();

        matrices.push();

        // =========================
        // 位置：魂牌正面
        // =========================
        matrices.translate(0.5, 0.75, 0.5);

        // =========================
        // 朝向修正（区分插地 / 挂墙）
        // =========================
        if (state.getBlock() instanceof WallSignBlock) {
            Direction facing = state.get(Properties.HORIZONTAL_FACING);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
            matrices.translate(0.0, 0.0, 0.4375); // 贴到墙前
        } else if (state.getBlock() instanceof SignBlock) {
            int rot = state.get(Properties.ROTATION);
            float yaw = -rot * 360.0f / 16.0f;
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yaw));
            matrices.translate(0.0, 0.0, 0.4375);
        }

        // =========================
        // 文本缩放（MC 文本单位）
        // =========================
        matrices.scale(0.01f, -0.01f, 0.01f);

        if (be.isBroken()) {
            renderBroken(be, matrices, vertices, light);
        } else {
            renderNormal(be, matrices, vertices, light);
        }

        matrices.pop();
    }

    /* =========================
       正常魂牌渲染
       ========================= */
    private void renderNormal(
            SoulTabletBlockEntity be,
            MatrixStack matrices,
            VertexConsumerProvider vertices,
            int light
    ) {
        String name = be.getOwnerName();
        Text text = Text.literal(name);

        int color = healthColor(be.getHealthRatio());
        float width = textRenderer.getWidth(text);

        textRenderer.draw(
                text,
                -width / 2f,
                0,
                color,
                false,
                matrices.peek().getPositionMatrix(),
                vertices,
                TextRenderer.TextLayerType.NORMAL,
                0,
                light
        );
    }

    /* =========================
       毁坏魂牌渲染
       ========================= */
    private void renderBroken(
            SoulTabletBlockEntity be,
            MatrixStack matrices,
            VertexConsumerProvider vertices,
            int light
    ) {
        // ① 绑定者名字（主标题）
        String owner = be.getOwnerName();
        Text line0 = Text.literal(owner == null ? "无名之魂" : owner)
                .formatted(Formatting.DARK_RED);

        // ② 状态
        Text line1 = Text.literal("魂牌已毁")
                .formatted(Formatting.DARK_RED);

        // ③ 最后死亡位置
        String death = be.getLastDeath();
        boolean unknownPos = (death == null) || death.isBlank();
        Text line2 = Text.literal("最后死于: " + (unknownPos ? "未知" : death))
                .formatted(Formatting.GRAY);

        // ④ 死亡原因
        String reason = be.getDeathReason();
        boolean unknownReason = (reason == null) || reason.isBlank();
        Text line3 = Text.literal("死因: " + (unknownReason ? "未知" : reason))
                .formatted(Formatting.DARK_GRAY);

        float w0 = textRenderer.getWidth(line0);
        float w1 = textRenderer.getWidth(line1);
        float w2 = textRenderer.getWidth(line2);
        float w3 = textRenderer.getWidth(line3);

        // 行距（你可以微调）
        int y0 = -20;
        int y1 = -8;
        int y2 = 6;
        int y3 = 20;

        textRenderer.draw(
                line0,
                -w0 / 2f,
                y0,
                0xAA0000,
                false,
                matrices.peek().getPositionMatrix(),
                vertices,
                TextRenderer.TextLayerType.NORMAL,
                0,
                light
        );

        textRenderer.draw(
                line1,
                -w1 / 2f,
                y1,
                0x880000,
                false,
                matrices.peek().getPositionMatrix(),
                vertices,
                TextRenderer.TextLayerType.NORMAL,
                0,
                light
        );

        textRenderer.draw(
                line2,
                -w2 / 2f,
                y2,
                0xAAAAAA,
                false,
                matrices.peek().getPositionMatrix(),
                vertices,
                TextRenderer.TextLayerType.NORMAL,
                0,
                light
        );

        textRenderer.draw(
                line3,
                -w3 / 2f,
                y3,
                0x888888,
                false,
                matrices.peek().getPositionMatrix(),
                vertices,
                TextRenderer.TextLayerType.NORMAL,
                0,
                light
        );
    }


    /* =========================
       血量 → 颜色
       ========================= */
    private static int healthColor(float r) {
        if (r >= 0.75f) return 0x55FF55; // 绿
        if (r >= 0.35f) return 0xFFFF55; // 黄
        return 0xFF5555;                // 红
    }

    private void renderDebugText(String s, BlockState state, MatrixStack matrices,
                                 VertexConsumerProvider vertices, int light) {
        matrices.push();
        matrices.translate(0.5, 1.0, 0.5);

        // 面向处理（和你原来一致就行，这里简化）
        if (state.getBlock() instanceof WallSignBlock) {
            Direction facing = state.get(Properties.HORIZONTAL_FACING);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
            matrices.translate(0, 0, 0.4375);
        } else if (state.getBlock() instanceof SignBlock) {
            int rot = state.get(Properties.ROTATION);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-rot * 360f / 16f));
            matrices.translate(0, 0, 0.4375);
        }

        matrices.scale(0.02f, -0.02f, 0.02f);
        Text t = Text.literal(s);
        float w = textRenderer.getWidth(t);
        textRenderer.draw(t, -w/2f, 0, 0xFFFFFF, false,
                matrices.peek().getPositionMatrix(), vertices,
                TextRenderer.TextLayerType.NORMAL, 0, light);
        matrices.pop();
    }

}
