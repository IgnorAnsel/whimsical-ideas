package com.ignoransel.whimsicalideas.content.hex.item;

import com.ignoransel.whimsicalideas.registry.WIItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Random;

public class RandomHexForgeItem extends Item {

    private final Random random = new Random();

    public RandomHexForgeItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack inHand = player.getStackInHand(hand);

        // ✅ 一定要在服务端发放物品
        if (!world.isClient) {
            ItemStack reward;
            int r = random.nextInt(4);
            reward = switch (r) {
                case 0 -> new ItemStack(WIItems.HEX_FORGE_IRON);
                case 1 -> new ItemStack(WIItems.HEX_FORGE_GOLD);
                case 2 -> new ItemStack(WIItems.HEX_FORGE_DIAMOND);
                default -> new ItemStack(WIItems.HEX_FORGE_NETHERITE);
            };

            player.giveItemStack(reward);

            // ✅ 消耗随机锻造器（创造模式不消耗）
            if (!player.getAbilities().creativeMode) {
                inHand.decrement(1);
            }
        }

        return TypedActionResult.success(inHand);
    }
}
