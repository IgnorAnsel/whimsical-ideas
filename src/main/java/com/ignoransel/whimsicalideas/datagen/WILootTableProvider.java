package com.ignoransel.whimsicalideas.datagen;

import com.ignoransel.whimsicalideas.registry.WIBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;

public class WILootTableProvider extends FabricBlockLootTableProvider {
    public WILootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        addDrop(WIBlocks.ZUN_SOUL_BANNER);
    }
}
