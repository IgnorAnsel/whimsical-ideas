package com.ignoransel.whimsicalideas.content.soulsail.render;

import com.ignoransel.whimsicalideas.content.soulsail.entity.ColoredLightningEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

import java.util.Random;

public class ColoredLightningRenderer extends EntityRenderer<ColoredLightningEntity> {

    public ColoredLightningRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(ColoredLightningEntity entity) {
        return null;
    }

    @Override
    public boolean shouldRender(ColoredLightningEntity entity, Frustum frustum, double x, double y, double z) {
        return true;
    }

    @Override
    public void render(ColoredLightningEntity e, float yaw, float tickDelta,
                       MatrixStack matrices, VertexConsumerProvider vcp, int light) {

        matrices.push();

        int rgb = e.getColorRgb();
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = (rgb) & 0xFF;

        VertexConsumer vc = vcp.getBuffer(RenderLayer.getLightning());
        Matrix4f mat = matrices.peek().getPositionMatrix();

        long seed = ((long) e.getId() * 3129871L) ^ ((long) e.age * 918271L);
        Random rand = new Random(seed);

        float height = 20.0f;

        float[] xs = new float[8];
        float[] zs = new float[8];
        float x = 0.0f;
        float z = 0.0f;

        for (int i = 7; i >= 0; --i) {
            xs[i] = x;
            zs[i] = z;
            x += (rand.nextFloat() - 0.5f) * 2.0f;
            z += (rand.nextFloat() - 0.5f) * 2.0f;
        }

        // 多次 pass 叠加，做出“粗+亮+分叉”的感觉
        // 这几个参数你可以调：
        // thicknessBase：整体粗细
        // alpha：透明度
        float thicknessBase = 0.18f;

        // 外层偏淡
        renderBoltPass(mat, vc, rand, xs, zs, height, thicknessBase * 1.8f, r, g, b, 90);
        // 中层
        renderBoltPass(mat, vc, rand, xs, zs, height, thicknessBase * 1.2f, r, g, b, 140);
        // 内核最亮
        renderBoltPass(mat, vc, rand, xs, zs, height, thicknessBase, r, g, b, 220);

        matrices.pop();
        super.render(e, yaw, tickDelta, matrices, vcp, light);
    }

    private static void renderBoltPass(Matrix4f mat, VertexConsumer vc, Random rand,
                                       float[] xs, float[] zs, float height,
                                       float thickness, int r, int g, int b, int a) {

        int branches = 3 + rand.nextInt(3);

        for (int br = 0; br < branches; br++) {
            int start = rand.nextInt(4); // 0~3
            int end = 7;

            float bx = (rand.nextFloat() - 0.5f) * 1.5f;
            float bz = (rand.nextFloat() - 0.5f) * 1.5f;

            float prevX = xs[end] + bx;
            float prevZ = zs[end] + bz;

            for (int i = end - 1; i >= start; --i) {
                float curX = xs[i] + bx;
                float curZ = zs[i] + bz;

                float y0 = (end - i) * (height / 7.0f);
                float y1 = (end - (i + 1)) * (height / 7.0f);

                drawThickSegment(mat, vc, prevX, y1, prevZ, curX, y0, curZ, thickness, r, g, b, a);

                prevX = curX;
                prevZ = curZ;
            }
        }
    }

    /**
     * 画一段“粗雷”：用 4 个面片围成一根四棱柱（类似原版粗闪电效果）
     */
    private static void drawThickSegment(Matrix4f mat, VertexConsumer vc,
                                         float x0, float y0, float z0,
                                         float x1, float y1, float z1,
                                         float t, int r, int g, int b, int a) {

        float dx = x1 - x0;
        float dz = z1 - z0;

        float px = -dz;
        float pz = dx;

        float len = (float) Math.sqrt(px * px + pz * pz);
        if (len < 1e-4f) {
            px = 1; pz = 0; len = 1;
        }
        px /= len;
        pz /= len;

        float ox1 = px * t;
        float oz1 = pz * t;
        float ox2 = -pz * t;
        float oz2 = px * t;

        quad(mat, vc,
                x0 - ox1, y0, z0 - oz1,
                x0 + ox1, y0, z0 + oz1,
                x1 + ox1, y1, z1 + oz1,
                x1 - ox1, y1, z1 - oz1,
                r, g, b, a);

        quad(mat, vc,
                x0 - ox2, y0, z0 - oz2,
                x0 + ox2, y0, z0 + oz2,
                x1 + ox2, y1, z1 + oz2,
                x1 - ox2, y1, z1 - oz2,
                r, g, b, a);
    }

    private static void quad(Matrix4f mat, VertexConsumer vc,
                             float x0, float y0, float z0,
                             float x1, float y1, float z1,
                             float x2, float y2, float z2,
                             float x3, float y3, float z3,
                             int r, int g, int b, int a) {
        vc.vertex(mat, x0, y0, z0).color(r, g, b, a).next();
        vc.vertex(mat, x1, y1, z1).color(r, g, b, a).next();
        vc.vertex(mat, x2, y2, z2).color(r, g, b, a).next();
        vc.vertex(mat, x3, y3, z3).color(r, g, b, a).next();
    }
}
