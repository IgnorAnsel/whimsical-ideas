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
//    Y轴
//      ↑
//     127┌─────────────────────────────────────────────────────────────┐
//        │                                                             │
//        │   [旗面] 破边3区域 (0,113) 到 (7,129)                         │
//     120│  +7px宽×16px高                                               │
//        │  ┌─────────────────┐                                       │
//     110│  │ 破边2 (0,101)   │  (旗面) 主旗面 (0,73) 到 (20,87)          │
//        │  │ 6×12           │      20×14                              │
//     100│  └────┐            │                     [流苏] (30,87) 到   │
//        │       │ 破边1      │                      (33,105) 3×18      │
//      90│       │ (0,87)     │                                        │
//        │       │ 7×14       │                                        │
//      80│       └─────────────┘                                       │
//        │                                                             │
//      70│  (旗面) 上沿包边 (0,70) 到 (20,72) 20×2                        │
//        │                                                             │
//      60│  (横梁) 主体 (0,60) 到 (24,62) 24×2      [右包铁] (5,64)       │
//        │                               [左包铁] (0,64) 2×3  到 (7,67) │
//      50│  (杆子) 中部金属箍 (0,56) 到 (3,59) 3×3                        │
//        │      顶部金属箍 (0,50) 到 (3,53) 3×3                          │
//      40│                                                             │
//        │                                                             │
//      30│                                                             │
//        │                    [符牌] (44,0) 到 (48,4) 4×4               │
//      20│                     [链条] (40,0) 到 (41,10) 1×10            │
//        │                                                             │
//      10│                                                             │
//        │                                                             │
//       0│  (杆子) 主杆 (0,0) 到 (2,48) 2×48   [符珠] 托座 (32,6) 4×4     │
//        │                            珠子 (32,0) 3×3                   │
//        └─────────────────────────────────────────────────────────────┘
//            0   10   20   30   40   50   60   70   80   90   100  110  120 → X轴
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
        flagBuilder.uv(0, 113).cuboid(13.0F, 14.0F, 0.0F, 7.0F, 16.0F, 1.0F);

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
                        .uv(64, 64).cuboid(-10.0F, -30.0F, -2.0F, 20.0F, 18.0F, 1.0F),
                ModelTransform.NONE
        );

        return TexturedModelData.of(modelData, 128, 128);
    }
}