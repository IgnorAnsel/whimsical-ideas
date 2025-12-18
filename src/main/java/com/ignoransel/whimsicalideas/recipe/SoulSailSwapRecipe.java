package com.ignoransel.whimsicalideas.recipe;

import com.ignoransel.whimsicalideas.registry.WIItems;
import com.ignoransel.whimsicalideas.registry.WIRecipes;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SoulSailSwapRecipe extends SpecialCraftingRecipe {

    public SoulSailSwapRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inv, World world) {
        int sailCount = 0;

        for (int i = 0; i < inv.size(); i++) {
            ItemStack s = inv.getStack(i);
            if (s.isEmpty()) continue;

            // 只允许出现一个“任意一种魂幡”，其它格子必须为空
            if (isAnySoulSail(s)) {
                sailCount++;
                if (sailCount > 1) return false;
            } else {
                return false;
            }
        }

        return sailCount == 1;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inv, DynamicRegistryManager registryManager) {
        ItemStack in = ItemStack.EMPTY;

        for (int i = 0; i < inv.size(); i++) {
            ItemStack s = inv.getStack(i);
            if (!s.isEmpty() && isAnySoulSail(s)) {
                in = s;
                break;
            }
        }

        if (in.isEmpty()) return ItemStack.EMPTY;

        Item outItem = (in.isOf(WIItems.ZUN_SOUL_SAIL) ? WIItems.TEST_ZUN_SOUL_SAIL : WIItems.ZUN_SOUL_SAIL);

        // ✅ 关键：保留 NBT ——把输入的 tag 整体复制到输出
        ItemStack out = new ItemStack(outItem, 1);

        if (in.hasNbt()) {
            out.setNbt(in.getNbt().copy()); // 直接保留 BlockEntityTag.Patterns / Souls / ReturnX 等
        }

        // ✅ 保留自定义名字
        if (in.hasCustomName()) {
            out.setCustomName(in.getName());
        }

        return out;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return WIRecipes.SOUL_SAIL_SWAP;
    }

    private static boolean isAnySoulSail(ItemStack stack) {
        return stack.isOf(WIItems.ZUN_SOUL_SAIL) || stack.isOf(WIItems.TEST_ZUN_SOUL_SAIL);
    }
}
