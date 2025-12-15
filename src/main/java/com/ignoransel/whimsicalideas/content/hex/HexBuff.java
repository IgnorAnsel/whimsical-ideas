package com.ignoransel.whimsicalideas.content.hex.buff;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public abstract class HexBuff {

    private final String name;
    private final String description;

    public HexBuff(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /** 应用到玩家 */
    public abstract void apply(PlayerEntity player);

    /** 移除玩家 */
    public abstract void remove(PlayerEntity player);

    /** UI 显示的 tooltip */
    public void appendTooltip(List<Text> tooltip) {
        tooltip.add(Text.literal(name).formatted(Formatting.GOLD));
        tooltip.add(Text.literal(description).formatted(Formatting.GRAY));
    }
}
