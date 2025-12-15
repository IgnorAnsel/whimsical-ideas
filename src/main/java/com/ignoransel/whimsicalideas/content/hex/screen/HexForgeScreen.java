package com.ignoransel.whimsicalideas.content.hex.screen;

import com.ignoransel.whimsicalideas.content.hex.HexBase;
import com.ignoransel.whimsicalideas.content.hex.HexRarity;
import com.ignoransel.whimsicalideas.content.hex.HexRegistry;
import com.ignoransel.whimsicalideas.content.hex.hexs.HexAgility;
import com.ignoransel.whimsicalideas.content.hex.hexs.HexMaxMining;
import com.ignoransel.whimsicalideas.content.hex.hexs.HexMight;
import com.ignoransel.whimsicalideas.content.hex.hexs.HexVitality;
import com.ignoransel.whimsicalideas.registry.WINetwork;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Random;

public class HexForgeScreen extends Screen {

    private final HexRarity forgeRarity;
    private HexBase[] options = new HexBase[3];

    private final boolean[] refreshed = new boolean[]{false, false, false};

    private HexCardWidget[] cardButtons = new HexCardWidget[3];
    private ButtonWidget[] refreshButtons = new ButtonWidget[3];

    private final Random random = new Random();
    private boolean optionsGenerated = false;

    // 卡面背景（你放资源包路径）
    private static final Identifier CARD_IRON =
            new Identifier("whimsical-ideas", "textures/gui/card_iron.png");
    private static final Identifier CARD_GOLD =
            new Identifier("whimsical-ideas", "textures/gui/card_gold.png");
    private static final Identifier CARD_DIAMOND =
            new Identifier("whimsical-ideas", "textures/gui/card_diamond.png");
    private static final Identifier CARD_NETHERITE =
            new Identifier("whimsical-ideas", "textures/gui/card_netherite.png");

    public HexForgeScreen(HexRarity forgeRarity) {
        super(Text.literal("Hex Forge"));
        this.forgeRarity = forgeRarity;
        generateOptions();
        optionsGenerated = true;
    }

    @Override
    protected void init() {
//        // 1) 只在第一次打开界面时生成选项，避免窗口变动导致“自动刷新”
//        if (!optionsGenerated) {
//            generateOptions();
//            optionsGenerated = true;
//        }

        // 2) resize 时会重新 init，先清空旧控件，避免叠加
        clearChildren();

        // ====== 布局参数 ======
        final int columns = 3;

        final int paddingX = 20;  // 屏幕左右留白
        final int paddingY = 20;  // 屏幕上下留白

        final int gapX = 18;      // 卡牌横向间距
        final int gapY = 8;       // 卡牌与按钮的纵向间距

        // 卡牌宽高限制（你可以按自己 UI 再调）
        final int minCardW = 120;
        final int maxCardW = 200;

        final int minCardH = 170;
        final int maxCardH = 260;

        // 按钮高度固定，宽度随卡牌变化（但有上下限）
        final int btnH = 20;
        final int minBtnW = 56;
        final int maxBtnW = 120;

        // ====== 计算卡牌宽度（根据屏幕自适应 + clamp） ======
        int availableW = width - paddingX * 2 - gapX * (columns - 1);
        int cardW = availableW / columns;
        cardW = Math.max(minCardW, Math.min(maxCardW, cardW));

        // ====== 计算卡牌高度（按比例算 + clamp） ======
        // 让卡牌高于宽：建议比例 4:3 或 3:2，这里用 4/3
        int cardH = (int) Math.round(cardW * 4.0 / 3.0);
        cardH = Math.max(minCardH, Math.min(maxCardH, cardH));

        // ====== 按钮宽度：不超过卡牌，并 clamp ======
        int btnW = (int) Math.round(cardW * 0.60);
        btnW = Math.max(minBtnW, Math.min(maxBtnW, btnW));
        btnW = Math.min(btnW, cardW - 12); // 绝不比卡牌还宽（留点边）

        // ====== 计算整体居中 ======
        int rowW = columns * cardW + (columns - 1) * gapX;
        int startX = (width - rowW) / 2;

        int totalH = cardH + gapY + btnH;
        int startY = (height - totalH) / 2;

        // 防止极端小窗口顶到边：再 clamp 一下起点
        startX = Math.max(paddingX, startX);
        startY = Math.max(paddingY, startY);

        // ====== 创建控件（左/中/右） ======
        for (int i = 0; i < 3; i++) {
            int index = i;

            int cardX = startX + i * (cardW + gapX);
            int cardY = startY;

            // 卡牌：点击选择
            cardButtons[i] = addDrawableChild(new HexCardWidget(
                    cardX, cardY, cardW, cardH,
                    options[i],
                    textureFor(options[i]),
                    btn -> selectHex(index)
            ));

            // 刷新按钮：卡牌下面居中
            int refreshX = cardX + (cardW - btnW) / 2;
            int refreshY = cardY + cardH + gapY;

            refreshButtons[i] = addDrawableChild(ButtonWidget.builder(
                    Text.literal("↻"),
                    btn -> refreshHex(index)
            ).dimensions(refreshX, refreshY, btnW, btnH).build());

            refreshButtons[i].active = !refreshed[i];
        }
    }



    private void generateOptions() {
        for (int i = 0; i < 3; i++) {
            options[i] = HexRegistry.randomFromRarity(forgeRarity, random);
        }
    }

    private HexBase randomHex() {
        int r = random.nextInt(4);
        return switch (r) {
            case 0 -> new HexMight();      // 你也要给它们加描述（见 HexBase 改造）
            case 1 -> new HexAgility();
            case 2 -> new HexMaxMining();
            default -> new HexVitality();
        };
    }

    private Identifier textureFor(HexBase hex) {
        return switch (hex.getRarity()) {
            case IRON -> CARD_IRON;
            case GOLD -> CARD_GOLD;
            case DIAMOND -> CARD_DIAMOND;
            case NETHERITE -> CARD_NETHERITE;
        };
    }

    private void selectHex(int index) {
        if (client == null || client.player == null) return;

        HexBase hex = options[index];
        if (hex == null) return;

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(hex.getId()); // ✅ 发 hexId

        ClientPlayNetworking.send(WINetwork.SELECT_HEX, buf);
        close();
    }

    private void refreshHex(int index) {
        if (refreshed[index]) return;

        HexBase old = options[index];
        HexBase newHex;

        do {
            newHex = HexRegistry.randomFromRarity(forgeRarity, random);
        } while (newHex != null && old != null && newHex.getId().equals(old.getId()));

        options[index] = newHex;
        refreshed[index] = true;

        refreshButtons[index].active = false;
        cardButtons[index].setHex(newHex, textureFor(newHex));
    }
}
