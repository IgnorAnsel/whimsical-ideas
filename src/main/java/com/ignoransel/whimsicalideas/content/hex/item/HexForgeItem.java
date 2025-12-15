package com.ignoransel.whimsicalideas.content.hex.item;

import com.ignoransel.whimsicalideas.content.hex.HexRarity;
import com.ignoransel.whimsicalideas.content.hex.screen.HexForgeScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class HexForgeItem extends Item {

    private final HexRarity forgeRarity;

    public HexForgeItem(Settings settings, HexRarity forgeRarity) {
        super(settings);
        this.forgeRarity = forgeRarity;
    }

    public HexRarity getForgeRarity() {
        return forgeRarity;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (world.isClient) {
            MinecraftClient.getInstance().setScreen(new HexForgeScreen(this.forgeRarity));
        }
        return TypedActionResult.success(player.getStackInHand(hand));
    }

}
