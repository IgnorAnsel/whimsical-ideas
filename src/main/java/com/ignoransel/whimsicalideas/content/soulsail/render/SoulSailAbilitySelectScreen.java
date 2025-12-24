package com.ignoransel.whimsicalideas.content.soulsail.render;

import com.ignoransel.whimsicalideas.client.ClientTimeSync;
import com.ignoransel.whimsicalideas.content.soulsail.*;
import com.ignoransel.whimsicalideas.registry.WINetwork;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public final class SoulSailAbilitySelectScreen extends Screen {

    private static final Identifier ICONS =
            new Identifier("whimsical-ideas", "textures/gui/soulsail/ability_icons.png");

    // 布局
    private static final int SLOT = 20;
    private static final int GAPX = 2;
    private static final int GAPY = 2;
    private static final int MARGIN = 6;
    private static final int Y0 = 6;

    private int previewOrd = Integer.MIN_VALUE;
    private double scrollAcc = 0.0;
    private static final double SCROLL_STEP = 1.0;

    private final boolean anchorLeft; // true=左上；false=右上
    private final ItemStack stack;    // 交互用哪个魂幡（最好那只）

    private List<SoulSailAbility> list = new ArrayList<>();
    private int[] rows;
    private int barW;

    public SoulSailAbilitySelectScreen(ItemStack stack, boolean anchorLeft) {
        super(Text.empty());
        this.stack = stack;
        this.anchorLeft = anchorLeft;
    }

    @Override public boolean shouldPause() { return false; }

    @Override
    protected void init() {
        rebuildList();
        SoulSailAbility selected = SoulSailItemCompat.getSelectedAbilitySafe(stack);
        previewOrd = selected.ordinal();
        scrollAcc = 0.0;
    }

    private void rebuildList() {
        list.clear();
        SoulBannerGrade grade = SoulSailItemCompat.getBannerGrade(stack);

        for (SoulSailAbility ab : SoulSailAbility.values()) {
            if (ab == SoulSailAbility.NONE || ab.unlockedBy(grade)) list.add(ab);
        }

        rows = build343Rows(list.size());
        int maxCols = 0;
        for (int c : rows) maxCols = Math.max(maxCols, c);
        barW = maxCols * SLOT + (maxCols - 1) * GAPX;
    }

    private int x0() {
        int sw = this.width;
        return anchorLeft ? MARGIN : (sw - MARGIN - barW);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // 不画黑背景：透明叠加
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        long now = ClientTimeSync.nowOverworldTime();
        SoulSailAbility selected = SoulSailItemCompat.getSelectedAbilitySafe(stack);

        int x0 = x0();
        int idx = 0;
        int maxCols = 0;
        for (int c : rows) maxCols = Math.max(maxCols, c);
        int fullW = maxCols * SLOT + (maxCols - 1) * GAPX;

        for (int r = 0; r < rows.length; r++) {
            int cols = rows[r];
            int rowW = cols * SLOT + (cols - 1) * GAPX;
            int rowX0 = x0 + (fullW - rowW) / 2;

            for (int c = 0; c < cols && idx < list.size(); c++, idx++) {
                SoulSailAbility ab = list.get(idx);

                int x = rowX0 + c * (SLOT + GAPX);
                int y = Y0 + r * (SLOT + GAPY);

                boolean hover = mouseX >= x && mouseX < x + SLOT && mouseY >= y && mouseY < y + SLOT;
                if (hover) {
                    previewOrd = ab.ordinal();
                }

                // 槽位背景
                ctx.fill(x, y, x + SLOT, y + SLOT, hover ? 0xA0222222 : 0x90000000);
                ctx.fill(x, y, x + SLOT, y + 1, 0x40FFFFFF);
                ctx.fill(x, y + SLOT - 1, x + SLOT, y + SLOT, 0x40000000);

                boolean isSelected = (ab == selected);
                boolean isPreview  = (ab.ordinal() == previewOrd);

                // 真正选中：白框
                if (isSelected) {
                    int col = 0xA0FFFFFF;
                    ctx.fill(x - 1, y - 1, x + SLOT + 1, y, col);
                    ctx.fill(x - 1, y + SLOT, x + SLOT + 1, y + SLOT + 1, col);
                    ctx.fill(x - 1, y, x, y + SLOT, col);
                    ctx.fill(x + SLOT, y, x + SLOT + 1, y + SLOT, col);
                }

                // 预览（但不是已选中）：金色框（你可换色）
                if (isPreview && !isSelected) {
                    int col = 0xA0FFD84A; // 金色
                    ctx.fill(x - 1, y - 1, x + SLOT + 1, y, col);
                    ctx.fill(x - 1, y + SLOT, x + SLOT + 1, y + SLOT + 1, col);
                    ctx.fill(x - 1, y, x, y + SLOT, col);
                    ctx.fill(x + SLOT, y, x + SLOT + 1, y + SLOT, col);
                }


                drawAbilityIcon(ctx, x + 2, y + 2, ab);

                // 被动开关点
                if (ab.passive) {
                    boolean on = isPassiveOn(stack, ab);
                    int ccol = on ? 0xFF33FF66 : 0xFF555555;
                    ctx.fill(x + SLOT - 5, y + 2, x + SLOT - 2, y + 5, ccol);
                }

                // 冷却遮罩
                int cd = ab.cooldownTicks;
                if (cd > 0) {
                    long until = SoulSailItemCompat.data(stack)
                            .getCompound(SoulSailKeys.ABILITY_CDS)
                            .getLong(ab.name());

                    long rem = Math.max(0, until - now);
                    if (rem > 0) {
                        float p = rem / (float) cd;
                        p = Math.max(0f, Math.min(1f, p));
                        int h = Math.round(SLOT * p);
                        ctx.fill(x, y + (SLOT - h), x + SLOT, y + SLOT, 0x88000000);
                    }
                }

                // Hover 提示
                if (hover && client.textRenderer != null) {
                    ctx.drawTextWithShadow(client.textRenderer, ab.displayName, x, y + SLOT + 2, 0xFFFFFF);
                }
            }
        }
        if (client.textRenderer != null) {
            ctx.drawTextWithShadow(client.textRenderer,
                    "Alt 模式：滚轮预览\n，左键确认",
                    x0(), Y0 + rows.length * (SLOT + GAPY) + 4,
                    0xCCCCCC);
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);

        int ord = hitTest((int) mouseX, (int) mouseY);
        if (ord != Integer.MIN_VALUE) {
            previewOrd = ord;       // 点击即预览=点击目标
            sendSetAbility(ord);    // ✅ 只有点击才提交
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        scrollAcc -= amount;

        int steps = 0;
        while (scrollAcc >= SCROLL_STEP) { scrollAcc -= SCROLL_STEP; steps++; }
        while (scrollAcc <= -SCROLL_STEP) { scrollAcc += SCROLL_STEP; steps--; }

        if (steps != 0) {
            cyclePreview(steps);
        }
        return true;
    }

    private void cyclePreview(int step) {
        // step > 0 前进，step < 0 后退；一次可能跳多格
        if (list.isEmpty()) return;

        int curOrd = previewOrd;
        if (curOrd == Integer.MIN_VALUE) {
            curOrd = SoulSailItemCompat.getSelectedAbilitySafe(stack).ordinal();
            previewOrd = curOrd;
        }

        // 找到 preview 在 list 里的位置
        int pos = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).ordinal() == curOrd) { pos = i; break; }
        }

        int next = (pos + step) % list.size();
        if (next < 0) next += list.size();

        previewOrd = list.get(next).ordinal();
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT_ALT || keyCode == GLFW.GLFW_KEY_RIGHT_ALT) {
            if (previewOrd != Integer.MIN_VALUE) {
                sendSetAbility(previewOrd);
            }
            MinecraftClient.getInstance().setScreen(null);
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    private int hitTest(int mx, int my) {
        int x0 = x0();
        int maxCols = 0;
        for (int c : rows) maxCols = Math.max(maxCols, c);
        int fullW = maxCols * SLOT + (maxCols - 1) * GAPX;

        int idx = 0;
        for (int r = 0; r < rows.length; r++) {
            int cols = rows[r];
            int rowW = cols * SLOT + (cols - 1) * GAPX;
            int rowX0 = x0 + (fullW - rowW) / 2;

            for (int c = 0; c < cols && idx < list.size(); c++, idx++) {
                int x = rowX0 + c * (SLOT + GAPX);
                int y = Y0 + r * (SLOT + GAPY);
                if (mx >= x && mx < x + SLOT && my >= y && my < y + SLOT) {
                    return list.get(idx).ordinal();
                }
            }
        }
        return Integer.MIN_VALUE;
    }

    private void cycleLocalAndSend(int dir) {
        SoulSailAbility cur = SoulSailItemCompat.getSelectedAbilitySafe(stack);
        int pos = 0;
        for (int i = 0; i < list.size(); i++) if (list.get(i) == cur) { pos = i; break; }
        int next = (pos + dir) % list.size();
        if (next < 0) next += list.size();
        sendSetAbility(list.get(next).ordinal());
    }

    private void sendSetAbility(int ord) {
        PacketByteBuf buf = net.fabricmc.fabric.api.networking.v1.PacketByteBufs.create();
        buf.writeVarInt(ord);
        ClientPlayNetworking.send(WINetwork.SET_ABILITY, buf);
    }

    private static final int ICON_SIZE = 64;      // 图标表每格是 64
    private static final int DRAW_SIZE = 16;      // 在槽里显示 16（你现在 slot=20，所以 16 合适）

    private static void drawAbilityIcon(DrawContext ctx, int x, int y, SoulSailAbility ab) {
        int idx = ab.ordinal();
        int u = idx * ICON_SIZE;

        int texW = ICON_SIZE * SoulSailAbility.values().length; // 10 -> 640
        int texH = ICON_SIZE;                                   // 64

        var matrices = ctx.getMatrices();
        matrices.push();
        matrices.translate(x, y, 0);

        float s = DRAW_SIZE / (float) ICON_SIZE; // 16/64 = 0.25
        matrices.scale(s, s, 1.0f);

        // 注意：现在 drawTexture 的 width/height 是“取图区域大小”，我们取 64×64
        ctx.drawTexture(ICONS, 0, 0, u, 0, ICON_SIZE, ICON_SIZE, texW, texH);

        matrices.pop();
    }


    private static boolean isPassiveOn(ItemStack stack, SoulSailAbility ab) {
        return switch (ab) {
            case SOUL_TOTEM -> SoulSailItemCompat.isSoulTotemEnabled(stack);
            case SOUL_BARRIER -> SoulSailItemCompat.isSoulBarrierEnabled(stack);
            case SOUL_DOMAIN -> SoulSailItemCompat.isSoulDomainEnabled(stack);
            default -> false;
        };
    }

    private static int[] build343Rows(int n) {
        if (n <= 3) return new int[]{n};
        if (n <= 7) return new int[]{3, n - 3};
        if (n <= 10) return new int[]{3, 4, n - 7};

        int left = n - 10;
        int extra = (left + 3) / 4;
        int[] rows = new int[3 + extra];
        rows[0] = 3; rows[1] = 4; rows[2] = 3;
        for (int i = 0; i < extra; i++) {
            rows[3 + i] = Math.min(4, left);
            left -= rows[3 + i];
        }
        return rows;
    }
}
