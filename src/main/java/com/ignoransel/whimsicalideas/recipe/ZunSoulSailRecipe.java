package com.ignoransel.whimsicalideas.recipe;

import com.ignoransel.whimsicalideas.registry.WIItems;
import com.ignoransel.whimsicalideas.registry.WIRecipes;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ZunSoulSailRecipe extends SpecialCraftingRecipe {

    public ZunSoulSailRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inv, World world) {
        ItemStack banner = ItemStack.EMPTY;

        for (int i = 0; i < inv.size(); i++) {
            ItemStack st = inv.getStack(i);
            if (st.isEmpty()) continue;

            // 只允许一个物品参与合成：那就是“织好图案的旗帜”
            if (!banner.isEmpty()) return false;
            if (!(st.getItem() instanceof BannerItem)) return false;

            if (!hasZunSoulPattern(st)) return false;
            banner = st;
        }
        return !banner.isEmpty();
    }

    @Override
    public ItemStack craft(RecipeInputInventory inv, DynamicRegistryManager registryManager) {
        ItemStack banner = ItemStack.EMPTY;
        for (int i = 0; i < inv.size(); i++) {
            ItemStack st = inv.getStack(i);
            if (!st.isEmpty()) { banner = st; break; }
        }
        if (banner.isEmpty()) return ItemStack.EMPTY;

        ItemStack out = new ItemStack(WIItems.ZUN_SOUL_SAIL, 1);

        // 复制旗帜图案（确保尊魂帆也显示相同 Patterns）
        NbtCompound bet = banner.getSubNbt("BlockEntityTag");
        if (bet != null) {
            out.getOrCreateSubNbt("BlockEntityTag").copyFrom(bet);
        }

        return out;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public net.minecraft.recipe.RecipeSerializer<?> getSerializer() {
        return WIRecipes.ZUN_SOUL_SAIL;
    }

    private static boolean hasZunSoulPattern(ItemStack banner) {
        NbtCompound bet = banner.getSubNbt("BlockEntityTag");
        if (bet == null) return false;

        NbtList patterns = bet.getList("Patterns", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < patterns.size(); i++) {
            NbtCompound p = patterns.getCompound(i);
            if ("zun_soul".equals(p.getString("Pattern"))) { // 这里和你注册 BannerPattern("zun_soul") 对应
                return true;
            }
        }
        return false;
    }
}
