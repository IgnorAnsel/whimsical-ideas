package com.ignoransel.whimsicalideas.registry;

import com.ignoransel.whimsicalideas.WhimsicalIdeas;

import com.ignoransel.whimsicalideas.content.soulsail.entity.SoulSiphonBlockEntity;
import com.ignoransel.whimsicalideas.content.soulsail.render.SoulSailPoleBlockEntity;
import com.ignoransel.whimsicalideas.content.soultablet.SoulTabletBlockEntity;
import com.ignoransel.whimsicalideas.content.soultablet.SoulTabletRenderer;
import com.ignoransel.whimsicalideas.content.tuningfork.TuningForkBlockEntity;
import com.ignoransel.whimsicalideas.entity.SoulBannerBlockEntity;
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
    public static final BlockEntityType<TuningForkBlockEntity> TUNING_FORK_BE =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    new Identifier(WhimsicalIdeas.MODID, "tuning_fork"),
                    FabricBlockEntityTypeBuilder.create(TuningForkBlockEntity::new, WIBlocks.TUNING_FORK_BLOCK).build()
            );

    public static BlockEntityType<SoulTabletBlockEntity> SOUL_TABLET_BE;
    public static BlockEntityType<SoulBannerBlockEntity> SOUL_BANNER_BE;
    public static final BlockEntityType<SoulSiphonBlockEntity> SOUL_SIPHON =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(WhimsicalIdeas.MODID, "soul_siphon"),
                    FabricBlockEntityTypeBuilder.create(SoulSiphonBlockEntity::new, WIBlocks.SOUL_SIPHON).build());
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

        SOUL_BANNER_BE = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier(WhimsicalIdeas.MODID, "soul_banner"),
                FabricBlockEntityTypeBuilder.create(SoulBannerBlockEntity::new,
                        WIBlocks.TEST_ZUN_SOUL_BANNER,
                        WIBlocks.TEST_ZUN_SOUL_WALL_BANNER
                ).build()
        );

    }
    public static final BlockEntityType<SoulSailPoleBlockEntity> SOUL_SAIL_POLE_BE =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    new Identifier(WhimsicalIdeas.MODID, "soul_sail_pole"),
                    BlockEntityType.Builder.create(SoulSailPoleBlockEntity::new, WIBlocks.SOUL_SAIL_POLE).build(null)
            );

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
