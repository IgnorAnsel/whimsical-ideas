package com.ignoransel.whimsicalideas.registry;

import com.ignoransel.whimsicalideas.WhimsicalIdeas;
import com.ignoransel.whimsicalideas.recipe.SoulSailMergeRecipe;
import com.ignoransel.whimsicalideas.recipe.SoulSailSwapRecipe;
import com.ignoransel.whimsicalideas.recipe.ZunSoulSailRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class WIRecipes {
    public static final RecipeSerializer<ZunSoulSailRecipe> ZUN_SOUL_SAIL =
            Registry.register(Registries.RECIPE_SERIALIZER,
                    new Identifier(WhimsicalIdeas.MODID, "zun_soul_sail"),
                    new SpecialRecipeSerializer<>(ZunSoulSailRecipe::new));
    public static final RecipeSerializer<SoulSailSwapRecipe> SOUL_SAIL_SWAP =
            Registry.register(
                    Registries.RECIPE_SERIALIZER,
                    new Identifier(WhimsicalIdeas.MODID, "soul_sail_swap"),
                    new SpecialRecipeSerializer<>(SoulSailSwapRecipe::new)
            );
    public static final RecipeSerializer<SoulSailMergeRecipe> SOUL_SAIL_MERGE =
            new SpecialRecipeSerializer<>(SoulSailMergeRecipe::new);
    public static void init() {
        Registry.register(Registries.RECIPE_SERIALIZER,
                new Identifier(WhimsicalIdeas.MODID, "soul_sail_merge"),
                SOUL_SAIL_MERGE);
    }
}
