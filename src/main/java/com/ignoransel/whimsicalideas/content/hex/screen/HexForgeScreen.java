package com.ignoransel.whimsicalideas.content.hex.screen;


import com.ignoransel.whimsicalideas.content.hex.*;
import com.ignoransel.whimsicalideas.content.hex.buff.HexBuff;
import com.ignoransel.whimsicalideas.content.hex.hexs.HexAgility;
import com.ignoransel.whimsicalideas.content.hex.hexs.HexMaxMining;
import com.ignoransel.whimsicalideas.content.hex.hexs.HexMight;
import com.ignoransel.whimsicalideas.content.hex.hexs.HexVitality;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;


import java.util.Random;


public class HexForgeScreen extends Screen {


    private HexBase[] options = new HexBase[3];
    private final boolean[] refreshed = new boolean[]{false, false, false};


    private ButtonWidget[] optionButtons = new ButtonWidget[3];
    private ButtonWidget[] refreshButtons = new ButtonWidget[3];


    private final Random random = new Random();


    // 图标资源
    private static final Identifier SILVER_ICON = new Identifier("whimsical-ideas", "textures/gui/hex_silver.png");
    private static final Identifier GOLD_ICON = new Identifier("whimsical-ideas", "textures/gui/hex_gold.png");
    private static final Identifier RAINBOW_ICON = new Identifier("whimsical-ideas", "textures/gui/hex_rainbow.png");


    public HexForgeScreen() {
        super(Text.literal("Hex Forge"));
    }


    @Override
    protected void init() {
        generateOptions();


        int startY = height / 2 - 75;
        int cardHeight = 40;
        int spacing = 10;


        for (int i = 0; i < 3; i++) {
            int index = i;
            int y = startY + i * (cardHeight + spacing);


            optionButtons[i] = addDrawableChild(ButtonWidget.builder(
                    Text.literal(options[i].getName()),
                    btn -> selectHex(index)
            ).dimensions(width / 2 - 100, y, 200, cardHeight).build());


            refreshButtons[i] = addDrawableChild(ButtonWidget.builder(
                    Text.literal("↻"),
                    btn -> refreshHex(index)
            ).dimensions(width / 2 + 105, y + 10, 20, 20).build());
        }
    }


    private void generateOptions() {
        for (int i = 0; i < 3; i++) {
            options[i] = randomHex();
        }
    }


    private HexBase randomHex() {
        int r = random.nextInt(4);
        return switch (r) {
            case 0 -> new HexMight();
            case 1 -> new HexAgility();
            case 2 -> new HexMaxMining();
            default -> new HexVitality();
        };
    }

    private void selectHex(int index) {
        if (client.player != null) {
            HexBase hex = options[index];
            hex.apply(client.player); // StatusEffectInstance 会自动显示图标和 tooltip
        }
        close();
    }


    private void refreshHex(int index) {
        if (refreshed[index]) return;


        HexBase old = options[index];
        HexBase newHex;
        do {
            newHex = randomHex();
        } while (newHex.getClass() == old.getClass());


        options[index] = newHex;
        refreshed[index] = true;
        refreshButtons[index].active = false;
        optionButtons[index].setMessage(Text.literal(newHex.getName()));
    }
}