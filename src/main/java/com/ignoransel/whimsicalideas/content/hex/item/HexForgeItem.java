package com.ignoransel.whimsicalideas.content.hex.item;


import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;


public class HexForgeItem extends Item {


    public HexForgeItem(Settings settings) {
        super(settings);
    }


    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (world.isClient) {
            MinecraftClient.getInstance().setScreen(new com.ignoransel.whimsicalideas.content.hex.screen.HexForgeScreen());
        }
        return TypedActionResult.success(player.getStackInHand(hand));
    }
}