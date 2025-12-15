package com.ignoransel.whimsicalideas.content.hex.screen;

import com.ignoransel.whimsicalideas.content.hex.HexBase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class HexCardWidget extends ButtonWidget {

    private HexBase hex;
    private Identifier cardTexture;

    public HexCardWidget(int x, int y, int w, int h, HexBase hex, Identifier cardTexture, PressAction onPress) {
        super(x, y, w, h, Text.empty(), onPress, DEFAULT_NARRATION_SUPPLIER);
        this.hex = hex;
        this.cardTexture = cardTexture;
    }

    public void setHex(HexBase hex, Identifier cardTexture) {
        this.hex = hex;
        this.cardTexture = cardTexture;
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        // 1) 画卡面背景图片（你可以做成 256x256 或任意大小，反正会缩放到按钮区域）
        context.drawTexture(cardTexture, this.getX(), this.getY(), 0, 0, this.width, this.height, this.width, this.height);

        // 2) hover 轻微高亮（可选：画一层半透明）
        if (this.isHovered()) {
            context.fill(getX(), getY(), getX() + width, getY() + height, 0x22000000);
        }

        // 3) 文本
        var tr = MinecraftClient.getInstance().textRenderer;

        // 标题
        context.drawText(tr, "HEX", getX() + 10, getY() + 8, 0xFFEFEFEF, false);

        // 名字（更大一点）
        int nameColor = hex.getRarity().getColor();
        context.drawText(tr, hex.getName(), getX() + 10, getY() + 22, nameColor, false);


        // 描述（略暗）
        context.drawText(tr, hex.getDescription(), getX() + 10, getY() + 38, 0xFFDDDDDD, false);
    }
}
