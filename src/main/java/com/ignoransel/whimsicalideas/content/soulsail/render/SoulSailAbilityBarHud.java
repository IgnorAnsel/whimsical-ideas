package com.ignoransel.whimsicalideas.content.soulsail.render;

import com.ignoransel.whimsicalideas.client.ClientTimeSync;
import com.ignoransel.whimsicalideas.content.soulsail.*;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public final class SoulSailAbilityBarHud implements HudRenderCallback {

    // 你的图标表：16x16 * N，单行
    private static final Identifier ICONS =
            new Identifier("whimsical-ideas", "textures/gui/soulsail/ability_icons.png");
    private static SoulBannerGrade getNextGrade(SoulBannerGrade currentGrade, long refinedSouls) {
        SoulBannerGrade g = currentGrade;

        while (g != SoulBannerGrade.IMMORTAL) {
            long requiredSouls = (long) Math.pow(10, g.getLevel() + 1);
            if (refinedSouls < requiredSouls) break;

            g = switch (g) {
                case MORTAL -> SoulBannerGrade.EARTH;
                case EARTH -> SoulBannerGrade.HEAVEN;
                case HEAVEN -> SoulBannerGrade.MYSTERIOUS;
                case MYSTERIOUS -> SoulBannerGrade.YELLOW;
                case YELLOW -> SoulBannerGrade.UNIVERSE;
                case UNIVERSE -> SoulBannerGrade.COSMOS;
                case COSMOS -> SoulBannerGrade.FLOOD;
                case FLOOD -> SoulBannerGrade.WASTELAND;
                case WASTELAND -> SoulBannerGrade.IMMORTAL;
                default -> g;
            };
        }

        return g;
    }

    @Override
    public void onHudRender(DrawContext ctx, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.textRenderer == null) return;
        if (client.options.hudHidden) return;

        HeldSoulSail held = getBestHeldSoulSailWithSide(client);
        if (held.stack.isEmpty()) return;

        ItemStack stack = held.stack;

        SoulBannerGrade grade = SoulSailItemCompat.getBannerGrade(stack);
        grade = getNextGrade(grade, SoulSailItemCompat.getRefinedSouls(stack));
        // 已解锁技能
        List<SoulSailAbility> list = new ArrayList<>();
        for (SoulSailAbility ab : SoulSailAbility.values()) {
            if (ab == SoulSailAbility.NONE || ab.unlockedBy(grade)) list.add(ab);
        }
        if (list.size() <= 1) return;

        SoulSailAbility selected = SoulSailItemCompat.getSelectedAbilitySafe(stack);

        int sw = client.getWindow().getScaledWidth();

        int slot = 20;
        int gapX = 2;
        int gapY = 2;

        // 3-4-3 行数分配
        int[] rows = build343Rows(list.size());   // e.g. [3,4,3]
        int maxCols = 0;
        for (int c : rows) maxCols = Math.max(maxCols, c);

        int barW = maxCols * slot + (maxCols - 1) * gapX;
        int barH = rows.length * slot + (rows.length - 1) * gapY;

        int margin = 6;
        int x0 = (held.side == Side.LEFT) ? margin : (sw - margin - barW);
        int y0 = 6; // 顶部（你要再往下就改这里，比如 18）

        long now = ClientTimeSync.nowOverworldTime();

        // 按行画
        int idx = 0;
        for (int r = 0; r < rows.length; r++) {
            int cols = rows[r];

            // 让每行“居中对齐”到最大宽度（更像343）
            int rowW = cols * slot + (cols - 1) * gapX;
            int rowX0 = x0 + (barW - rowW) / 2;

            for (int c = 0; c < cols && idx < list.size(); c++, idx++) {
                SoulSailAbility ab = list.get(idx);

                int x = rowX0 + c * (slot + gapX);
                int y = y0 + r * (slot + gapY);

                // ====== 下面这些你原来的绘制逻辑保持不变 ======

                ctx.fill(x, y, x + slot, y + slot, 0x90000000);
                ctx.fill(x, y, x + slot, y + 1, 0x40FFFFFF);
                ctx.fill(x, y + slot - 1, x + slot, y + slot, 0x40000000);

                if (ab == selected) {
                    ctx.fill(x - 1, y - 1, x + slot + 1, y, 0xA0FFFFFF);
                    ctx.fill(x - 1, y + slot, x + slot + 1, y + slot + 1, 0xA0FFFFFF);
                    ctx.fill(x - 1, y, x, y + slot, 0xA0FFFFFF);
                    ctx.fill(x + slot, y, x + slot + 1, y + slot, 0xA0FFFFFF);
                }

                drawAbilityIcon(ctx, x + 2, y + 2, ab);

                if (ab.passive) {
                    boolean on = isPassiveOn(stack, ab);
                    int ccol = on ? 0xFF33FF66 : 0xFF555555;
                    ctx.fill(x + slot - 5, y + 2, x + slot - 2, y + 5, ccol);
                }

                int cd = ab.cooldownTicks;
                if (cd > 0) {
                    long until = SoulSailItemCompat.data(stack)
                            .getCompound(SoulSailKeys.ABILITY_CDS)
                            .getLong(ab.name());

                    long rem = Math.max(0, until - now);
                    if (rem > 0) {
                        float p = rem / (float) cd;
                        p = Math.max(0f, Math.min(1f, p));
                        int h = Math.round(slot * p);

                        ctx.fill(x, y + (slot - h), x + slot, y + slot, 0x88000000);

                        float sec = rem / 20f;
                        String s = String.format("%.0f", Math.ceil(sec));
                        ctx.drawTextWithShadow(client.textRenderer, s, x + 7, y + 6, 0xFFFFFF);
                    }
                }
            }
        }
    }

    private static int[] build343Rows(int n) {
        if (n <= 3) return new int[]{n};
        if (n <= 7) return new int[]{3, n - 3};
        if (n <= 10) return new int[]{3, 4, n - 7};

        // >10：先 3-4-3，用完剩下的每行4
        int left = n - 10;
        int extraRows = (left + 3) / 4;
        int[] rows = new int[3 + extraRows];
        rows[0] = 3; rows[1] = 4; rows[2] = 3;
        for (int i = 0; i < extraRows; i++) {
            rows[3 + i] = Math.min(4, left);
            left -= rows[3 + i];
        }
        return rows;
    }

    private enum Side { LEFT, RIGHT }

    private static final class HeldSoulSail {
        final ItemStack stack;
        final Side side;
        HeldSoulSail(ItemStack stack, Side side) { this.stack = stack; this.side = side; }
    }

    /** 两手都有时取阶级高的，并且 side 跟随那只手（主=右，副=左） */
    private static HeldSoulSail getBestHeldSoulSailWithSide(MinecraftClient client) {
        ItemStack main = client.player.getMainHandStack();
        ItemStack off  = client.player.getOffHandStack();

        boolean m = main.getItem() instanceof SoulSailBannerItem;
        boolean o = off.getItem()  instanceof SoulSailBannerItem;

        if (!m && !o) return new HeldSoulSail(ItemStack.EMPTY, Side.RIGHT);
        if (m && !o)  return new HeldSoulSail(main, Side.RIGHT);
        if (!m)       return new HeldSoulSail(off,  Side.LEFT);

        SoulBannerGrade gm = SoulSailItemCompat.getBannerGrade(main);
        SoulBannerGrade go = SoulSailItemCompat.getBannerGrade(off);

        if (gm.getLevel() >= go.getLevel()) return new HeldSoulSail(main, Side.RIGHT);
        return new HeldSoulSail(off, Side.LEFT);
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

    public static ItemStack getBestHeldSoulSail(MinecraftClient client) {
        ItemStack main = client.player.getMainHandStack();
        ItemStack off  = client.player.getOffHandStack();
        boolean m = main.getItem() instanceof SoulSailBannerItem;
        boolean o = off.getItem()  instanceof SoulSailBannerItem;

        if (!m && !o) return ItemStack.EMPTY;
        if (m && !o) return main;
        if (!m) return off;

        SoulBannerGrade gm = SoulSailItemCompat.getBannerGrade(main);
        SoulBannerGrade go = SoulSailItemCompat.getBannerGrade(off);
        return (gm.getLevel() >= go.getLevel()) ? main : off;
    }
}
