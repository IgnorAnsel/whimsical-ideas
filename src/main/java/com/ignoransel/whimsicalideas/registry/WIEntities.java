package com.ignoransel.whimsicalideas.registry;

import com.ignoransel.whimsicalideas.WhimsicalIdeas;
import com.ignoransel.whimsicalideas.content.soulsail.SoulXpOrbEntity;
import com.ignoransel.whimsicalideas.content.soulsail.entity.ColoredLightningEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class WIEntities {
    public static EntityType<SoulXpOrbEntity> SOUL_XP_ORB;
    public static final EntityType<ColoredLightningEntity> COLORED_LIGHTNING =
            Registry.register(Registries.ENTITY_TYPE,
                    new Identifier(WhimsicalIdeas.MODID, "colored_lightning"),
                    FabricEntityTypeBuilder.create(SpawnGroup.MISC, ColoredLightningEntity::new)
                            .dimensions(EntityDimensions.fixed(1.0f, 20.0f))
                            .trackRangeBlocks(128)
                            .trackedUpdateRate(1)
                            .build()
            );

    public static void init() {
        SOUL_XP_ORB = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(WhimsicalIdeas.MODID, "soul_xp_orb"),
                FabricEntityTypeBuilder.create(SpawnGroup.MISC, SoulXpOrbEntity::new)
                        .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                        .trackRangeBlocks(64)
                        .trackedUpdateRate(10)
                        .build()
        );

    }

}
