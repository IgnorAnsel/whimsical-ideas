package com.ignoransel.whimsicalideas.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class WhimsicalIdeasDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(WIRecipeProvider::new);
        // pack.addProvider(WILootTableProvider::new);
        pack.addProvider(WIAdvancementProvider::new);
        // pack.addProvider(WILangProvider::new);
	}
}
