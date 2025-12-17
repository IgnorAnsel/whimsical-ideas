package com.ignoransel.whimsicalideas.mixin;

import com.ignoransel.whimsicalideas.content.soulsail.SoulSailBannerData;
import com.ignoransel.whimsicalideas.content.soulsail.SoulSailKeys;
import com.ignoransel.whimsicalideas.registry.WIBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BannerBlockEntity.class)
public class BannerBlockEntityMixin implements SoulSailBannerData {

    @Unique private long whimsicalideas$souls = 0L;
    @Unique private NbtList whimsicalideas$storedMobs = new NbtList();

    @Unique
    private boolean whimsicalideas$isSoulSailBanner() {
        // 只对你自己的旗帜生效，避免污染全世界所有横幅
        Block b = ((BannerBlockEntity)(Object)this).getCachedState().getBlock();
        return b == WIBlocks.ZUN_SOUL_BANNER || b == WIBlocks.ZUN_SOUL_WALL_BANNER;
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    private void whimsicalideas$readCustom(NbtCompound nbt, CallbackInfo ci) {
        if (!whimsicalideas$isSoulSailBanner()) return;

        this.whimsicalideas$souls = nbt.getLong(SoulSailKeys.SOULS);
        this.whimsicalideas$storedMobs = nbt.getList(SoulSailKeys.STORED, NbtElement.STRING_TYPE);
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    private void whimsicalideas$writeCustom(NbtCompound nbt, CallbackInfo ci) {
        if (!whimsicalideas$isSoulSailBanner()) return;

        // 你想一直保留键也行；不想写空值就加判断
        nbt.putLong(SoulSailKeys.SOULS, this.whimsicalideas$souls);
        nbt.put(SoulSailKeys.STORED, this.whimsicalideas$storedMobs);
    }

    @Override public long whimsicalideas$getSouls() { return whimsicalideas$souls; }
    @Override public void whimsicalideas$setSouls(long v) { whimsicalideas$souls = v; }

    @Override public NbtList whimsicalideas$getStoredMobs() { return whimsicalideas$storedMobs; }
    @Override public void whimsicalideas$setStoredMobs(NbtList list) {
        this.whimsicalideas$storedMobs = (list == null) ? new NbtList() : list;
    }
}
