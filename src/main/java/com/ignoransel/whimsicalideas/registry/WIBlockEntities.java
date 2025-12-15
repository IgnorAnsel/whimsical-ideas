package com.ignoransel.whimsicalideas.registry;

import com.ignoransel.whimsicalideas.WhimsicalIdeas;
import com.ignoransel.whimsicalideas.content.soultablet.SoulTabletBlockEntity;
import com.ignoransel.whimsicalideas.content.soultablet.SoulTabletRenderer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class WIBlockEntities {
    private WIBlockEntities(){}

    public static BlockEntityType<SoulTabletBlockEntity> SOUL_TABLET_BE;

    public static void init() {
        SOUL_TABLET_BE = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier(WhimsicalIdeas.MODID, "soul_tablet"),
                FabricBlockEntityTypeBuilder.create(
                        SoulTabletBlockEntity::new,
                        WIBlocks.SOUL_TABLET, WIBlocks.SOUL_TABLET_WALL,
                        WIBlocks.SOUL_TABLET_BROKEN, WIBlocks.SOUL_TABLET_BROKEN_WALL
                ).build()
        );
    }

    public static void initClient() {
        BlockEntityRendererRegistry.register(SOUL_TABLET_BE, SoulTabletRenderer::new);
        BlockRenderLayerMap.INSTANCE.putBlocks(
                RenderLayer.getCutout(),
                WIBlocks.SOUL_TABLET,
                WIBlocks.SOUL_TABLET_WALL,
                WIBlocks.SOUL_TABLET_BROKEN,
                WIBlocks.SOUL_TABLET_BROKEN_WALL
        );
    }
}
