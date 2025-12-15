package com.ignoransel.whimsicalideas.datagen;

import com.ignoransel.whimsicalideas.WhimsicalIdeas;
import com.ignoransel.whimsicalideas.registry.WIItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class WIRecipeProvider extends FabricRecipeProvider {

    public WIRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> consumer) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, WIItems.SOUL_TABLET_ITEM, 1)
                .pattern(" P ")
                .pattern(" R ")
                .pattern("L L")
                .input('P', ItemTags.PLANKS)   // 任意木板
                .input('R', Items.REDSTONE)    // 红石粉
                .input('L', ItemTags.LOGS)     // 任意原木/木头
                .criterion("has_redstone", conditionsFromItem(Items.REDSTONE))
                .offerTo(consumer, new Identifier(WhimsicalIdeas.MODID, "soul_tablet"));

        reinforced(consumer, "reinforced_soul_tablet_iron", WIItems.SOUL_TABLET_IRON_ITEM, Items.IRON_INGOT);
        reinforced(consumer, "reinforced_soul_tablet_gold", WIItems.SOUL_TABLET_GOLD_ITEM, Items.GOLD_INGOT);
        reinforced(consumer, "reinforced_soul_tablet_diamond", WIItems.SOUL_TABLET_DIAMOND_ITEM, Items.DIAMOND);
        reinforced(consumer, "reinforced_soul_tablet_netherite", WIItems.SOUL_TABLET_NETHERITE_ITEM, Items.NETHERITE_INGOT);
    }

    private static void reinforced(Consumer<RecipeJsonProvider> consumer,
                                   String id,
                                   Item output,
                                   Item surround) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, output, 1)
                .pattern(" I ")
                .pattern("ISI")
                .pattern(" I ")
                .input('S', WIItems.SOUL_TABLET_ITEM)
                .input('I', surround)
                .criterion("has_soul_tablet", conditionsFromItem(WIItems.SOUL_TABLET_ITEM))
                .offerTo(consumer, new Identifier(WhimsicalIdeas.MODID, id));
    }
}
