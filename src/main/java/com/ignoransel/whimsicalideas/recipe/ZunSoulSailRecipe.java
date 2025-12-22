//package com.ignoransel.whimsicalideas.recipe;
//
//import com.ignoransel.whimsicalideas.registry.WIItems;
//import com.ignoransel.whimsicalideas.registry.WIRecipes;
//import net.minecraft.inventory.RecipeInputInventory;
//import net.minecraft.item.BannerItem;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.Items;
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.nbt.NbtElement;
//import net.minecraft.nbt.NbtList;
//import net.minecraft.recipe.Ingredient;
//import net.minecraft.recipe.SpecialCraftingRecipe;
//import net.minecraft.recipe.book.CraftingRecipeCategory;
//import net.minecraft.registry.DynamicRegistryManager;
//import net.minecraft.util.Identifier;
//import net.minecraft.world.World;
//
//public class ZunSoulSailRecipe extends SpecialCraftingRecipe {
//
//    // 3x3 九宫格索引：
//    // 0 1 2
//    // 3 4 5
//    // 6 7 8
//    // 你可以按需要替换成你想要的“怪物掉落物”
//    private static final Ingredient[] RING = new Ingredient[]{
//            Ingredient.ofItems(Items.GHAST_TEAR),    // 0
//            Ingredient.ofItems(Items.SPIDER_EYE),    // 1
//            Ingredient.ofItems(Items.ENDER_PEARL),   // 2
//            Ingredient.ofItems(Items.STRING),        // 3
//            Ingredient.EMPTY,                        // 4 (中心：旗帜，单独检查)
//            Ingredient.ofItems(Items.GUNPOWDER),     // 5
//            Ingredient.ofItems(Items.ROTTEN_FLESH),  // 6
//            Ingredient.ofItems(Items.BONE),          // 7
//            Ingredient.ofItems(Items.SLIME_BALL)     // 8
//    };
//
//    public ZunSoulSailRecipe(Identifier id, CraftingRecipeCategory category) {
//        super(id, category);
//    }
//
//    @Override
//    public boolean matches(RecipeInputInventory inv, World world) {
//        // 强制 3x3（工作台）；玩家 2x2 不允许
//        if (inv.getWidth() != 3 || inv.getHeight() != 3) return false;
//
//        // 1) 中心必须是“带 zun_soul 图案的旗帜”
//        ItemStack center = inv.getStack(4);
//        if (center.isEmpty()) return false;
//        if (!(center.getItem() instanceof BannerItem)) return false;
//        if (!hasZunSoulPattern(center)) return false;
//
//        // 2) 周围 8 格必须严格匹配指定掉落物
//        for (int i = 0; i < 9; i++) {
//            if (i == 4) continue;
//
//            ItemStack st = inv.getStack(i);
//            Ingredient ing = RING[i];
//
//            // 这里要求“必须放对且不能为空”
//            if (st.isEmpty()) return false;
//            if (!ing.test(st)) return false;
//        }
//
//        return true;
//    }
//
//    @Override
//    public ItemStack craft(RecipeInputInventory inv, DynamicRegistryManager registryManager) {
//        if (inv.getWidth() != 3 || inv.getHeight() != 3) return ItemStack.EMPTY;
//
//        ItemStack banner = inv.getStack(4);
//        if (banner.isEmpty() || !(banner.getItem() instanceof BannerItem)) return ItemStack.EMPTY;
//        if (!hasZunSoulPattern(banner)) return ItemStack.EMPTY;
//
//        ItemStack out = new ItemStack(WIItems.ZUN_SOUL_SAIL, 1);
//
//        // 复制旗帜图案（确保尊魂帆也显示相同 Patterns）
//        NbtCompound bet = banner.getSubNbt("BlockEntityTag");
//        if (bet != null) {
//            out.getOrCreateSubNbt("BlockEntityTag").copyFrom(bet);
//        }
//
//        return out;
//    }
//
//    @Override
//    public boolean fits(int width, int height) {
//        // 只允许 3x3 的有序合成
//        return width == 3 && height == 3;
//    }
//
//    @Override
//    public net.minecraft.recipe.RecipeSerializer<?> getSerializer() {
//        return WIRecipes.ZUN_SOUL_SAIL;
//    }
//
//    public static boolean hasZunSoulPattern(ItemStack banner) {
//        NbtCompound bet = banner.getSubNbt("BlockEntityTag");
//        if (bet == null) return false;
//
//        NbtList patterns = bet.getList("Patterns", NbtElement.COMPOUND_TYPE);
//        for (int i = 0; i < patterns.size(); i++) {
//            NbtCompound p = patterns.getCompound(i);
//            if ("zun_soul".equals(p.getString("Pattern"))) {
//                return true;
//            }
//        }
//        return false;
//    }
//}
