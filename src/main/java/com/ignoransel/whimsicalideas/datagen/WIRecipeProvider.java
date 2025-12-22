package com.ignoransel.whimsicalideas.datagen;

import com.ignoransel.whimsicalideas.WhimsicalIdeas;
import com.ignoransel.whimsicalideas.registry.WIItems;
import com.ignoransel.whimsicalideas.registry.WIRecipes;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.ComplexRecipeJsonBuilder;
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


        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, WIItems.SOUL_SAIL_POLE, 1)
                .pattern("M#M")
                .pattern("IMI")
                .pattern(" M ")
                .input('#', ItemTags.PLANKS)
                .input('M', Items.STICK)
                .input('I', Items.IRON_INGOT)
                .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .criterion("has_planks", conditionsFromTag(ItemTags.PLANKS))
                .offerTo(consumer, new Identifier(WhimsicalIdeas.MODID, "soul_sail_pole"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, WIItems.SOUL_FLAG, 1)
                .pattern("###")
                .pattern("#S#")
                .pattern(" T ")
                .input('#', Items.BLACK_WOOL)
                .input('S', Items.SOUL_SAND)
                .input('T', Items.STICK)
                .criterion("has_soul_sand", conditionsFromItem(Items.SOUL_SAND))
                .criterion("has_black_wool", conditionsFromItem(Items.BLACK_WOOL))
                .offerTo(consumer, new Identifier(WhimsicalIdeas.MODID, "soul_flag"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, WIItems.ZUN_SOUL_SAIL, 1)
                .pattern(" A ")
                .pattern("OFO")
                .pattern("OPO")
                .input('A', Items.AMETHYST_SHARD)
                .input('F', WIItems.SOUL_FLAG)
                .input('P', WIItems.SOUL_SAIL_POLE)
                .input('O', Items.OBSIDIAN)
                .criterion(hasItem(WIItems.SOUL_FLAG), conditionsFromItem(WIItems.SOUL_FLAG))
                .criterion(hasItem(WIItems.SOUL_SAIL_POLE), conditionsFromItem(WIItems.SOUL_SAIL_POLE))
                .offerTo(consumer, new Identifier(WhimsicalIdeas.MODID, "zun_soul_sail"));
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
