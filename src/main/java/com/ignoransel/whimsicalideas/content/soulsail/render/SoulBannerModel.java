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

    public SoulBannerModel(ModelPart root) {
        this.root = root;
        this.pole = root.getChild("pole");
        this.crossbar = root.getChild("crossbar");
        this.finial = root.getChild("finial");
        this.flag = root.getChild("flag");
        this.tasselLeft = root.getChild("tassel_left");
        this.tasselRight = root.getChild("tassel_right");
        this.chain = root.getChild("chain");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        // =========================
        // 杆子（变细）
        // =========================
        root.addChild("pole",
                ModelPartBuilder.create()
                        // 2x48x2
                        .uv(0, 0).cuboid(-1.0F, -32.0F, -1.0F, 2.0F, 48.0F, 2.0F)
                        // 金属箍（顶部）：3x3x3
                        .uv(0, 40).cuboid(-1.5F, -30.0F, -1.5F, 3.0F, 3.0F, 3.0F)
                        // 金属箍（中部）：3x3x3
                        .uv(0, 48).cuboid(-1.5F, -10.0F, -1.5F, 3.0F, 3.0F, 3.0F),
                ModelTransform.NONE
        );

        // =========================
        // 顶端横梁（变薄）
        // =========================
        root.addChild("crossbar",
                ModelPartBuilder.create()
                        // 横梁：24x2x2（原来 x2x4 太厚）
                        .uv(0, 56).cuboid(-12.0F, -30.0F, -1.0F, 24.0F, 2.0F, 2.0F)
                        // 两端包铁：2x3x3（原来 5 太厚）
                        .uv(0, 62).cuboid(-12.5F, -30.5F, -1.5F, 2.0F, 3.0F, 3.0F)
                        .uv(0, 62).cuboid(10.5F, -30.5F, -1.5F, 2.0F, 3.0F, 3.0F),
                ModelTransform.NONE
        );

        // =========================
        // 顶部符珠/灵石（变小）
        // =========================
        root.addChild("finial",
                ModelPartBuilder.create()
                        // 珠子：3x3x3
                        .uv(32, 0).cuboid(-1.5F, -33.0F, -1.5F, 3.0F, 3.0F, 3.0F)
                        // 托座：4x4x4
                        .uv(32, 8).cuboid(-2.0F, -33.5F, -2.0F, 4.0F, 4.0F, 4.0F),
                ModelTransform.NONE
        );

        // =========================
        // 旗面（更像布：薄）
        // =========================
        ModelPartBuilder flagBuilder = ModelPartBuilder.create();

        // 上沿包边：厚度从 2 改 1
        flagBuilder.uv(0, 52).cuboid(0.0F, -2.0F, 0.0F, 20.0F, 2.0F, 1.0F);

        // 主旗面：保持 1
        flagBuilder.uv(0, 52).cuboid(0.0F, 0.0F, 0.0F, 20.0F, 14.0F, 1.0F);

        // 破边：保持 1
        flagBuilder.uv(0, 66).cuboid(0.0F, 14.0F, 0.0F, 7.0F, 14.0F, 1.0F);
        flagBuilder.uv(0, 82).cuboid(7.0F, 14.0F, 0.0F, 6.0F, 12.0F, 1.0F);
        flagBuilder.uv(0, 96).cuboid(13.0F, 14.0F, 0.0F, 7.0F, 16.0F, 1.0F);

        // ✅ 侧边加厚建议直接删掉（最容易显“厚”和穿模）
        // flagBuilder.uv(0, 66).cuboid(-0.5F, 0.0F, 0.0F, 1.0F, 18.0F, 1.0F);
        // flagBuilder.uv(0, 66).cuboid(19.5F, 0.0F, 0.0F, 1.0F, 18.0F, 1.0F);

        // 旗面整体离杆更近一点：-3 → -1.5（避免“浮出来太多”）
        root.addChild("flag", flagBuilder, ModelTransform.pivot(-10.0F, -28.0F, -1.5F));

        // =========================
        // 流苏（更薄、更贴布）
        // =========================
        root.addChild("tassel_left",
                ModelPartBuilder.create()
                        .uv(0, 96).cuboid(-2.0F, 0.0F, 0.2F, 3.0F, 18.0F, 1.0F),
                ModelTransform.pivot(-8.0F, -28.0F, -1.2F)
        );

        root.addChild("tassel_right",
                ModelPartBuilder.create()
                        .uv(0, 96).cuboid(-1.0F, 0.0F, 0.2F, 3.0F, 16.0F, 1.0F),
                ModelTransform.pivot(8.0F, -28.0F, -1.2F)
        );

        // =========================
        // 链条（也别太厚，尽量贴近）
        // =========================
        root.addChild("chain",
                ModelPartBuilder.create()
                        .uv(40, 20).cuboid(-0.5F, -28.0F, 0.8F, 1.0F, 10.0F, 1.0F)
                        .uv(44, 20).cuboid(-2.0F, -18.5F, 0.6F, 4.0F, 4.0F, 1.0F),
                ModelTransform.NONE
        );

        return TexturedModelData.of(modelData, 128, 128);
    }

}
