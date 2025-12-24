package com.ignoransel.whimsicalideas.content.soulsail.render;

import com.ignoransel.whimsicalideas.content.soulsail.block.SoulSiphonBlock;
import com.ignoransel.whimsicalideas.content.soulsail.entity.SoulSiphonBlockEntity;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.hit.BlockHitResult;

public final class SoulSiphonHud implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext ctx, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null || client.textRenderer == null) return;
        if (client.options.hudHidden) return;

        if (!(client.crosshairTarget instanceof BlockHitResult bhr)) return;

        var pos = bhr.getBlockPos();
        var bs = client.world.getBlockState(pos);
        if (!(bs.getBlock() instanceof SoulSiphonBlock)) return;

        var be = client.world.getBlockEntity(pos);
        if (!(be instanceof SoulSiphonBlockEntity siphon)) return;

        long souls = siphon.getBannerSoulsForClient();
        float p = siphon.getAbsorbProgressForClient();

        int x = 8;
        int y = 8;

        // 背板
        int w = 130;
        int h = 36;
        ctx.fill(x - 4, y - 4, x + w, y + h, 0x88000000);

        ctx.drawTextWithShadow(client.textRenderer, "魂幡魂量: " + souls, x, y, 0xFFFFFF);

        // 进度条
        int barX = x;
        int barY = y + 14;
        int barW = 120;
        int barH = 8;

        ctx.fill(barX, barY, barX + barW, barY + barH, 0xFF222222);
        int fill = (int) (barW * Math.max(0f, Math.min(1f, p)));
        ctx.fill(barX, barY, barX + fill, barY + barH, 0xFF33D6FF);

        int percent = (int) (p * 100f + 0.5f);
        ctx.drawTextWithShadow(client.textRenderer, "吸收进度: " + percent + "%", x, y + 24, 0xCCCCCC);
    }
}
