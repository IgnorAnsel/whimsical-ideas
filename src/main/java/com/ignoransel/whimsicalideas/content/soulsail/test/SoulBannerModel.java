package com.ignoransel.whimsicalideas.content.soulsail.test;

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
        // 杆子（加粗 + 金属箍）
        // =========================
        // 主杆：稍微加粗点（3x48x3）
        root.addChild("pole",
                ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-1.5F, -32.0F, -1.5F, 3.0F, 48.0F, 3.0F)
                        // 金属箍（顶部）
                        .uv(0, 40).cuboid(-2.0F, -30.0F, -2.0F, 4.0F, 3.0F, 4.0F)
                        // 金属箍（中部）
                        .uv(0, 48).cuboid(-2.0F, -10.0F, -2.0F, 4.0F, 3.0F, 4.0F),
                ModelTransform.NONE
        );

        // =========================
        // 顶端横梁（魂幡常见结构）
        // =========================
        root.addChild("crossbar",
                ModelPartBuilder.create()
                        // 横梁：长一点，挂布更像幡
                        .uv(0, 56).cuboid(-12.0F, -30.0F, -2.0F, 24.0F, 2.0F, 4.0F)
                        // 两端小包铁
                        .uv(0, 62).cuboid(-12.5F, -30.5F, -2.5F, 2.0F, 3.0F, 5.0F)
                        .uv(0, 62).cuboid(10.5F, -30.5F, -2.5F, 2.0F, 3.0F, 5.0F),
                ModelTransform.NONE
        );

        // =========================
        // 顶部符珠/灵石（紫色发光核心）
        // =========================
        root.addChild("finial",
                ModelPartBuilder.create()
                        // 放在横梁中间上方一个小“珠子”
                        .uv(32, 0).cuboid(-2.0F, -34.0F, -2.0F, 4.0F, 4.0F, 4.0F)
                        // 外圈（像金属托座）
                        .uv(32, 8).cuboid(-2.5F, -33.5F, -2.5F, 5.0F, 5.0F, 5.0F),
                ModelTransform.NONE
        );

        // =========================
        // 旗面（上沿包边 + 破碎下摆）
        // pivot：挂点在上沿靠杆
        // =========================
        ModelPartBuilder flagBuilder = ModelPartBuilder.create();

        // 上沿包边（让它看起来“挂在横梁上”）
        flagBuilder.uv(0, 52).cuboid(0.0F, -2.0F, 0.0F, 20.0F, 2.0F, 2.0F);

        // 主旗面（20x14x1）
        flagBuilder.uv(0, 52).cuboid(0.0F, 0.0F, 0.0F, 20.0F, 14.0F, 1.0F);

        // 下半：参差破边
        flagBuilder.uv(0, 66).cuboid(0.0F, 14.0F, 0.0F, 7.0F, 14.0F, 1.0F);
        flagBuilder.uv(0, 82).cuboid(7.0F, 14.0F, 0.0F, 6.0F, 12.0F, 1.0F);
        flagBuilder.uv(0, 96).cuboid(13.0F, 14.0F, 0.0F, 7.0F, 16.0F, 1.0F);

        // 旗面侧边轻微加厚（更立体，但很薄）
        flagBuilder.uv(0, 66).cuboid(-0.5F, 0.0F, 0.0F, 1.0F, 18.0F, 1.0F);
        flagBuilder.uv(0, 66).cuboid(19.5F, 0.0F, 0.0F, 1.0F, 18.0F, 1.0F);

        // pivot 放在上沿挂点；你之前用 -28，很合适
        root.addChild("flag", flagBuilder, ModelTransform.pivot(-10.0F, -28.0F, -3.0F));


        // =========================
        // 流苏：挂在横梁下面（独立 pivot）
        // =========================
        root.addChild("tassel_left",
                ModelPartBuilder.create()
                        // 这里 cuboid 的 y 从 0 往下长，因为 pivot 已经在横梁下面了
                        .uv(0, 96).cuboid(-2.0F, 0.0F, 0.6F, 3.0F, 18.0F, 1.0F),
                // pivot：横梁下方、左侧（你可以调 x）
                ModelTransform.pivot(-8.0F, -28.0F, 0.0F)
        );

        root.addChild("tassel_right",
                ModelPartBuilder.create()
                        .uv(0, 96).cuboid(-1.0F, 0.0F, 0.6F, 3.0F, 16.0F, 1.0F),
                // pivot：横梁下方、右侧
                ModelTransform.pivot(8.0F, -28.0F, 0.0F)
        );


        // =========================
        // 小链条挂件（挂在横梁下方）
        // =========================
        root.addChild("chain",
                ModelPartBuilder.create()
                        // 细链条
                        .uv(40, 20).cuboid(-0.5F, -28.0F, 1.5F, 1.0F, 10.0F, 1.0F)
                        // 末端小符牌
                        .uv(44, 20).cuboid(-2.0F, -18.5F, 1.0F, 4.0F, 4.0F, 1.0F),
                ModelTransform.NONE
        );

        return TexturedModelData.of(modelData, 128, 128);
    }
}
