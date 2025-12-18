package com.ignoransel.whimsicalideas.render;

import com.ignoransel.whimsicalideas.WhimsicalIdeas;
import com.ignoransel.whimsicalideas.content.soulsail.SoulXpOrbEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

public class SoulXpOrbEntityRenderer extends EntityRenderer<SoulXpOrbEntity> {

    private static final Identifier TEXTURE =
            new Identifier(WhimsicalIdeas.MODID, "textures/entity/soul_xp_orb.png");

    public SoulXpOrbEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.shadowRadius = 0.15f;
    }

    @Override
    public Identifier getTexture(SoulXpOrbEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(SoulXpOrbEntity entity, float yaw, float tickDelta,
                       MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        // 面向摄像机（billboard）
        matrices.multiply(this.dispatcher.getRotation());
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));

        // 大小
        float s = 1f;
        matrices.scale(s, s, s);

        VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE));
        Matrix4f mat = matrices.peek().getPositionMatrix();

        // 如果你希望它“发光更亮”，用 fullBright；不需要就用 light
        int fullBright = LightmapTextureManager.MAX_LIGHT_COORDINATE;

        vc.vertex(mat, -0.5f, -0.5f, 0.0f).color(255,255,255,255).texture(0f,1f)
                .overlay(OverlayTexture.DEFAULT_UV).light(fullBright).normal(0,1,0).next();
        vc.vertex(mat,  0.5f, -0.5f, 0.0f).color(255,255,255,255).texture(1f,1f)
                .overlay(OverlayTexture.DEFAULT_UV).light(fullBright).normal(0,1,0).next();
        vc.vertex(mat,  0.5f,  0.5f, 0.0f).color(255,255,255,255).texture(1f,0f)
                .overlay(OverlayTexture.DEFAULT_UV).light(fullBright).normal(0,1,0).next();
        vc.vertex(mat, -0.5f,  0.5f, 0.0f).color(255,255,255,255).texture(0f,0f)
                .overlay(OverlayTexture.DEFAULT_UV).light(fullBright).normal(0,1,0).next();

        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }
}
