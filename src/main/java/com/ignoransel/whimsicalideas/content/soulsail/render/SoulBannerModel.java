package com.ignoransel.whimsicalideas.content.soulsail.render;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;

public class SoulBannerModel {
    public final ModelPart root;
    public final ModelPart pole;
    public final ModelPart crossbar;
    public final ModelPart finial;
    public final ModelPart flag;
    public final ModelPart tasselLeft;
    public final ModelPart tasselRight;
    public final ModelPart chain;
    public final ModelPart vapor;

    public SoulBannerModel(ModelPart root) {
        this.root = root;
        this.pole = root.getChild("pole");
        this.crossbar = root.getChild("crossbar");
        this.finial = root.getChild("finial");
        this.flag = root.getChild("flag");
        this.tasselLeft = root.getChild("tassel_left");
        this.tasselRight = root.getChild("tassel_right");
        this.chain = root.getChild("chain");
        this.vapor = root.getChild("vapor");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        // =========================
        // 杆子（变细）
        // =========================
        root.addChild("pole", ModelPartBuilder.create()
                        // 2x48x2
                        .uv(0, 0).cuboid(-1.0F, -32.0F, -1.0F, 2.0F, 48.0F, 2.0F)
                        // 金属箍（顶部）：3x3x3
                        .uv(0, 50).cuboid(-1.5F, -30.0F, -1.5F, 3.0F, 3.0F, 3.0F)
                        // 金属箍（中部）：3x3x3
                        .uv(0, 56).cuboid(-1.5F, -10.0F, -1.5F, 3.0F, 3.0F, 3.0F),
                ModelTransform.NONE
        );

        // =========================
        // 顶端横梁（变薄）
        // =========================
        root.addChild("crossbar", ModelPartBuilder.create()
                        // 横梁：24x2x2
                        .uv(0, 60).cuboid(-12.0F, -30.0F, -1.0F, 24.0F, 2.0F, 2.0F)
                        // 两端包铁：2x3x3
                        .uv(0, 64).cuboid(-12.5F, -30.5F, -1.5F, 2.0F, 3.0F, 3.0F)
                        .uv(5, 64).cuboid(10.5F, -30.5F, -1.5F, 2.0F, 3.0F, 3.0F),
                ModelTransform.NONE
        );

        // =========================
        // 顶部符珠/灵石（变小）
        // =========================
        root.addChild("finial", ModelPartBuilder.create()
                        // 珠子：3x3x3
                        .uv(32, 0).cuboid(-1.5F, -33.0F, -1.5F, 3.0F, 3.0F, 3.0F)
                        // 托座：4x4x4
                        .uv(32, 6).cuboid(-2.0F, -33.5F, -2.0F, 4.0F, 4.0F, 4.0F),
                ModelTransform.NONE
        );

        // =========================
        // 旗面
        // =========================
        ModelPartBuilder flagBuilder = ModelPartBuilder.create();
        // 上沿包边
        flagBuilder.uv(0, 70).cuboid(0.0F, -2.0F, 0.0F, 20.0F, 2.0F, 1.0F);
        // 主旗面
        flagBuilder.uv(0, 73).cuboid(0.0F, 0.0F, 0.0F, 20.0F, 14.0F, 1.0F);
        // 破边
        flagBuilder.uv(0, 87).cuboid(0.0F, 14.0F, 0.0F, 7.0F, 14.0F, 1.0F);
        flagBuilder.uv(0, 101).cuboid(7.0F, 14.0F, 0.0F, 6.0F, 12.0F, 1.0F);
        flagBuilder.uv(48, 96).cuboid(13.0F, 14.0F, 0.0F, 7.0F, 16.0F, 1.0F);

        root.addChild("flag", flagBuilder, ModelTransform.pivot(-10.0F, -28.0F, -1.5F));

        // =========================
        // 流苏
        // =========================
        root.addChild("tassel_left", ModelPartBuilder.create()
                        .uv(30, 87).cuboid(-2.0F, 0.0F, 0.2F, 3.0F, 18.0F, 1.0F),
                ModelTransform.pivot(-8.0F, -28.0F, -1.2F)
        );

        root.addChild("tassel_right", ModelPartBuilder.create()
                        .uv(30, 87).cuboid(-1.0F, 0.0F, 0.2F, 3.0F, 16.0F, 1.0F),
                ModelTransform.pivot(8.0F, -28.0F, -1.2F)
        );

        // =========================
        // 链条
        // =========================
        root.addChild("chain", ModelPartBuilder.create()
                        .uv(40, 0).cuboid(-0.5F, -28.0F, 0.8F, 1.0F, 10.0F, 1.0F)
                        .uv(44, 0).cuboid(-2.0F, -18.5F, 0.6F, 4.0F, 4.0F, 1.0F),
                ModelTransform.NONE
        );

        root.addChild("vapor", ModelPartBuilder.create()
                        .uv(64, 64).cuboid(1, 1, -0.5F, 2.0F, 2.0F, 1.0F),
                ModelTransform.NONE
        );

        return TexturedModelData.of(modelData, 128, 128);
    }
}