package com.ignoransel.whimsicalideas.mixin;

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
public class BannerBlockEntityMixin {

    @Unique private long whimsicalideas$souls = 0L, whimsicalideas$RawSouls = 0L, whimsicalideas$RefinedSouls = 0L;
    @Unique private String whimsicalideas$SailId = "";
    @Unique private NbtList whimsicalideas$storedMobs = new NbtList();

    @Unique private int whimsicalideas$roomX = 0;
    @Unique private int whimsicalideas$roomZ = 0;

    @Unique private String whimsicalideas$returnDim = "";
    @Unique private double whimsicalideas$returnX = 0, whimsicalideas$returnY = 0, whimsicalideas$returnZ = 0;
    @Unique private float whimsicalideas$returnYaw = 0, whimsicalideas$returnPitch = 0;

    @Unique
    private boolean whimsicalideas$isSoulSailBanner() {
        Block b = ((BannerBlockEntity)(Object)this).getCachedState().getBlock();
        return b == WIBlocks.ZUN_SOUL_BANNER || b == WIBlocks.ZUN_SOUL_WALL_BANNER;
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    private void whimsicalideas$readCustom(NbtCompound nbt, CallbackInfo ci) {
        if (!whimsicalideas$isSoulSailBanner()) return;

        whimsicalideas$souls = nbt.getLong(SoulSailKeys.SOULS);
        whimsicalideas$RawSouls = nbt.getLong(SoulSailKeys.RAW_SOULS);
        whimsicalideas$RefinedSouls = nbt.getLong(SoulSailKeys.REFINED_SOULS);
        whimsicalideas$storedMobs = nbt.getList(SoulSailKeys.STORED, NbtElement.STRING_TYPE);
        whimsicalideas$SailId = nbt.getString(SoulSailKeys.SAIL_ID);
        whimsicalideas$roomX = nbt.getInt(SoulSailKeys.ROOM_X);
        whimsicalideas$roomZ = nbt.getInt(SoulSailKeys.ROOM_Z);

        whimsicalideas$returnDim = nbt.getString(SoulSailKeys.RETURN_DIM);
        whimsicalideas$returnX = nbt.getDouble(SoulSailKeys.RETURN_X);
        whimsicalideas$returnY = nbt.getDouble(SoulSailKeys.RETURN_Y);
        whimsicalideas$returnZ = nbt.getDouble(SoulSailKeys.RETURN_Z);
        whimsicalideas$returnYaw = nbt.getFloat(SoulSailKeys.RETURN_YAW);
        whimsicalideas$returnPitch = nbt.getFloat(SoulSailKeys.RETURN_PITCH);
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    private void whimsicalideas$writeCustom(NbtCompound nbt, CallbackInfo ci) {
        if (!whimsicalideas$isSoulSailBanner()) return;

        nbt.putString(SoulSailKeys.SAIL_ID, whimsicalideas$SailId);

        nbt.putLong(SoulSailKeys.SOULS, whimsicalideas$souls);
        nbt.putLong(SoulSailKeys.RAW_SOULS, whimsicalideas$RawSouls);
        nbt.putLong(SoulSailKeys.REFINED_SOULS, whimsicalideas$RefinedSouls);
        nbt.put(SoulSailKeys.STORED, whimsicalideas$storedMobs);

        nbt.putInt(SoulSailKeys.ROOM_X, whimsicalideas$roomX);
        nbt.putInt(SoulSailKeys.ROOM_Z, whimsicalideas$roomZ);

        if (!whimsicalideas$returnDim.isEmpty()) {
            nbt.putString(SoulSailKeys.RETURN_DIM, whimsicalideas$returnDim);
            nbt.putDouble(SoulSailKeys.RETURN_X, whimsicalideas$returnX);
            nbt.putDouble(SoulSailKeys.RETURN_Y, whimsicalideas$returnY);
            nbt.putDouble(SoulSailKeys.RETURN_Z, whimsicalideas$returnZ);
            nbt.putFloat(SoulSailKeys.RETURN_YAW, whimsicalideas$returnYaw);
            nbt.putFloat(SoulSailKeys.RETURN_PITCH, whimsicalideas$returnPitch);
        }
    }
}
