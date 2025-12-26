package com.ignoransel.whimsicalideas.client;

import com.ignoransel.whimsicalideas.mixin.ItemRendererAccessor;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.RotationAxis;

public class ProjectionFrameRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();

        // 获取物品的基础模型
        BakedModel baseModel = itemRenderer.getModels().getModel(stack);

        // 渲染相框本体
        matrices.push();
        // 使用 Access Widener 之后，这里不再报错
        VertexConsumer vertices = ItemRenderer.getDirectItemGlintConsumer(
                vertexConsumers,
                RenderLayer.getCutout(),
                true,
                stack.hasGlint()
        );
        ((ItemRendererAccessor) itemRenderer).invokeRenderBakedItemModel(baseModel, stack, light, overlay, matrices, vertices);
        matrices.pop();
        matrices.pop();

        // 渲染内部缩影
        if (stack.hasNbt() && stack.getNbt().contains("CapturedBlocks")) {
            NbtList blockList = stack.getNbt().getList("CapturedBlocks", NbtElement.COMPOUND_TYPE);

            matrices.push();
            // 调整中心位置：相框一般在 0.5 位置
            matrices.translate(0.5, 0.5, 0.5);

            // 缩放
            float scale = 0.05f;
            matrices.scale(scale, scale, scale);

            // 自动旋转
            float rotation = (System.currentTimeMillis() / 20) % 360;
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));

            int limit = Math.min(blockList.size(), 150);
            for (int i = 0; i < limit; i++) {
                NbtCompound tag = blockList.getCompound(i);
                double r = tag.getDouble("r");
                double u = tag.getDouble("u");
                double f = tag.getDouble("f");

                // 1.20.1 推荐的获取方式
                BlockState state = NbtHelper.toBlockState(
                        MinecraftClient.getInstance().world.createCommandRegistryWrapper(Registries.BLOCK.getKey()),
                        tag.getCompound("state")
                );

                matrices.push();
                matrices.translate(r, u, f);

                // 渲染微缩方块
                MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(
                        state, matrices, vertexConsumers, light, overlay
                );

                matrices.pop();
            }
            matrices.pop();
        }
    }
}