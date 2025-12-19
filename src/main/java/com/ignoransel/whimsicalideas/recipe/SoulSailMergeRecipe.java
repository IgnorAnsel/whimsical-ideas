package com.ignoransel.whimsicalideas.recipe;

import com.ignoransel.whimsicalideas.content.soulsail.SoulSailBannerItem;
import com.ignoransel.whimsicalideas.content.soulsail.SoulSailItemCompat;
import com.ignoransel.whimsicalideas.registry.WIRecipes;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class SoulSailMergeRecipe extends SpecialCraftingRecipe {

    public SoulSailMergeRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    // 检查合成栏中是否正好有两个魂幡
    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        int count = 0;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof SoulSailBannerItem) {
                    count++;
                } else {
                    return false; // 如果有非魂幡物品，则不匹配
                }
            }
        }
        return count == 2;
    }

    // 执行合并逻辑
    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        ItemStack mainBanner = ItemStack.EMPTY; // 主魂幡（接收者）
        ItemStack sacrificeBanner = ItemStack.EMPTY; // 祭品魂幡（提供者）

        // 寻找合成栏里的两个魂幡
        // 逻辑：我们假设在合成栏中，索引较小（左上）的是主魂幡，索引较大（右下）的是被吞噬的魂幡
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty() && stack.getItem() instanceof SoulSailBannerItem) {
                if (mainBanner.isEmpty()) {
                    mainBanner = stack;
                } else {
                    sacrificeBanner = stack;
                    break;
                }
            }
        }

        if (mainBanner.isEmpty() || sacrificeBanner.isEmpty()) {
            return ItemStack.EMPTY;
        }

        // 复制主魂幡作为结果，避免直接修改原物品
        ItemStack resultStack = mainBanner.copy();

        // 1. 获取祭品的数据
        long sourceRaw = SoulSailItemCompat.getRawSouls(sacrificeBanner);
        long sourceRefined = SoulSailItemCompat.getRefinedSouls(sacrificeBanner);
        NbtList sourceMobs = SoulSailItemCompat.getPendingList(sacrificeBanner);

        // 2. 将数据添加到结果魂幡
        // 注意：这里 cap 传入 Long.MAX_VALUE 确保能吸纳所有魂魄，或者你可以限制为 resultStack 的 Tier 上限
        SoulSailItemCompat.addRawSouls(resultStack, sourceRaw, Long.MAX_VALUE);
        SoulSailItemCompat.addRefinedSouls(resultStack, sourceRefined, Long.MAX_VALUE);

        // 3. 转移待收容生物
        // 遍历 NbtList 并添加到结果中
        for (NbtElement element : sourceMobs) {
            String mobId = element.asString();
            SoulSailItemCompat.addPendingMob(resultStack, mobId);
        }

        // 4. 更新总魂魄计数 (Souls = Raw + Refined)
        // SoulSailItemCompat.addRaw/Refined 内部可能只更新了分项，这里确保总数被计算
        // 但根据你提供的代码，addRawSouls 并没有自动调用 syncTotal，
        // 不过 SoulSailItemCompat.getSouls 是动态计算 (raw+refined) 的，所以通常显示没问题。
        // 如果内部有 syncTotal 逻辑（如你代码里的 private 方法），可能需要确保它被触发。
        // 鉴于提供的 add 方法很简单，我们手动再次确认一下也没坏处，
        // 但由于 syncTotal 是 private，我们依赖 getSouls 的动态计算即可。

        return resultStack;
    }


    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        // 返回你在 ModRecipeSerializers 中注册的 Serializer
        return WIRecipes.SOUL_SAIL_MERGE;
    }
}