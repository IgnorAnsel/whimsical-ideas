package com.ignoransel.whimsicalideas.content.soulsail.render;

import com.ignoransel.whimsicalideas.content.soulsail.SoulSailAbility;
import com.ignoransel.whimsicalideas.content.soulsail.SoulSailBannerItem;
import com.ignoransel.whimsicalideas.content.soulsail.SoulSailItemCompat;
import com.ignoransel.whimsicalideas.content.soulsail.SoulSailKeys;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.List;

public final class SoulSailCooldownHud implements HudRenderCallback {

    // 是否显示“已就绪”的技能（false = 只显示正在冷却的）
    private static final boolean SHOW_READY = false;

    @Override
    public void onHudRender(DrawContext ctx, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.textRenderer == null) return;
        if (client.options.hudHidden) return;

        // 找到玩家手上的魂幡（主手优先，其次副手）
        ItemStack stack = client.player.getMainHandStack();
        if (!(stack.getItem() instanceof SoulSailBannerItem)) {
            stack = client.player.getOffHandStack();
            if (!(stack.getItem() instanceof SoulSailBannerItem)) return;
        }

        // 你服务器端用 sp.age 写入冷却，所以客户端也用 player.age 读
        long now = client.player.age;

        NbtCompound bet = SoulSailItemCompat.data(stack);
        NbtCompound cds = bet.getCompound(SoulSailKeys.ABILITY_CDS);

        // 收集要显示的技能
        List<Row> rows = new ArrayList<>();
        for (SoulSailAbility ab : SoulSailAbility.values()) {
            if (ab == SoulSailAbility.NONE) continue;
            if (ab.cooldownTicks <= 0) continue;

            long until = cds.getLong(ab.name());
            long remaining = Math.max(0L, until - now);

            if (!SHOW_READY && remaining <= 0) continue;

            rows.add(new Row(ab, remaining, until));
        }

        if (rows.isEmpty()) return;

        int screenW = client.getWindow().getScaledWidth();
        int screenH = client.getWindow().getScaledHeight();

        int barW = 140;
        int barH = 8;
        int gap = 6;

        int x = screenW / 2 - barW / 2;
        int yBase = screenH - 60; // 热键栏上方

        // 为了不往下溢出，整体往上堆
        int totalH = rows.size() * (barH + gap);
        int yStart = yBase - totalH + (barH + gap);

        for (int i = 0; i < rows.size(); i++) {
            Row r = rows.get(i);
            SoulSailAbility ab = r.ab;

            int y = yStart + i * (barH + gap);

            // 标题（技能名 + 剩余秒）
            String name = ab.displayName;

            String timeText;
            if (r.remainingTicks > 0) {
                float sec = r.remainingTicks / 20.0f;
                timeText = String.format("%.1fs", sec);
            } else {
                timeText = "就绪";
            }

            ctx.drawTextWithShadow(client.textRenderer, name, x, y - 10, 0xFFFFFF);
            ctx.drawTextWithShadow(client.textRenderer, timeText, x + barW + 6, y - 1, 0xCCCCCC);

            // 进度条
            float progress;
            if (r.remainingTicks <= 0) {
                progress = 1.0f;
            } else {
                progress = 1.0f - (r.remainingTicks / (float) ab.cooldownTicks);
                progress = Math.max(0f, Math.min(1f, progress));
            }
            int fillW = Math.round(barW * progress);

            ctx.fill(x, y, x + barW, y + barH, 0x80000000);       // 背景
            ctx.fill(x, y, x + fillW, y + barH, 0xA0FFFFFF);      // 填充
        }
    }

    private static final class Row {
        final SoulSailAbility ab;
        final long remainingTicks;
        final long until;

        Row(SoulSailAbility ab, long remainingTicks, long until) {
            this.ab = ab;
            this.remainingTicks = remainingTicks;
            this.until = until;
        }
    }
}
