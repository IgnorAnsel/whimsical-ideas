package com.ignoransel.whimsicalideas.mixin;

import com.ignoransel.whimsicalideas.content.soulsail.SoulBannerGrade;
import com.ignoransel.whimsicalideas.content.soulsail.SoulSailBannerData;
import com.ignoransel.whimsicalideas.content.soulsail.SoulSailKeys;
import com.ignoransel.whimsicalideas.registry.WIBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BannerBlockEntity.class)
public class BannerBlockEntityMixin implements SoulSailBannerData {

    @Unique private long whimsicalideas$souls = 0L, whimsicalideas$RawSouls = 0L, whimsicalideas$RefinedSouls = 0L;
    @Unique private String whimsicalideas$SailId = "";
    @Unique private NbtList whimsicalideas$storedMobs = new NbtList();

    @Unique private int whimsicalideas$roomX = 0;
    @Unique private int whimsicalideas$roomZ = 0;

    @Unique private String whimsicalideas$returnDim = "";
    @Unique private double whimsicalideas$returnX = 0, whimsicalideas$returnY = 0, whimsicalideas$returnZ = 0;
    @Unique private float whimsicalideas$returnYaw = 0, whimsicalideas$returnPitch = 0;
    @Unique private int whimsicalideas$bannerGrade = 0; // 默认凡阶(0)
    @Unique private int whimsicalideas$lastRadius = 0;


    @Unique
    private BannerBlockEntity self() {
        return (BannerBlockEntity)(Object)this;
    }

    @Override
    public int wi$getRoomX() {
        return whimsicalideas$roomX;
    }

    @Override
    public int wi$getRoomZ() {
        return whimsicalideas$roomZ;
    }

    @Unique
    @Override
    public boolean wi$isSoulSailBanner() {
        Block b = self().getCachedState().getBlock();
        return b == WIBlocks.ZUN_SOUL_BANNER || b == WIBlocks.ZUN_SOUL_WALL_BANNER;
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    private void whimsicalideas$readCustom(NbtCompound nbt, CallbackInfo ci) {
        if (!wi$isSoulSailBanner()) return;

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
        whimsicalideas$bannerGrade = nbt.getInt(SoulSailKeys.BANNER_GRADE);
        whimsicalideas$lastRadius = nbt.getInt(SoulSailKeys.LAST_RADIUS);
        // 兜底：总魂同步一次（防止旧存档不一致）
        wi$syncTotal();
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    private void whimsicalideas$writeCustom(NbtCompound nbt, CallbackInfo ci) {
        if (!wi$isSoulSailBanner()) return;

        // 写之前再同步一次总魂，保证 Souls = Raw + Refined
        wi$syncTotal();

        nbt.putString(SoulSailKeys.SAIL_ID, whimsicalideas$SailId);

        nbt.putLong(SoulSailKeys.SOULS, whimsicalideas$souls);
        nbt.putLong(SoulSailKeys.RAW_SOULS, whimsicalideas$RawSouls);
        nbt.putLong(SoulSailKeys.REFINED_SOULS, whimsicalideas$RefinedSouls);
        nbt.put(SoulSailKeys.STORED, whimsicalideas$storedMobs);

        nbt.putInt(SoulSailKeys.ROOM_X, whimsicalideas$roomX);
        nbt.putInt(SoulSailKeys.ROOM_Z, whimsicalideas$roomZ);
        nbt.putInt(SoulSailKeys.BANNER_GRADE, whimsicalideas$bannerGrade);
        nbt.putInt(SoulSailKeys.LAST_RADIUS, whimsicalideas$lastRadius);
        if (!whimsicalideas$returnDim.isEmpty()) {
            nbt.putString(SoulSailKeys.RETURN_DIM, whimsicalideas$returnDim);
            nbt.putDouble(SoulSailKeys.RETURN_X, whimsicalideas$returnX);
            nbt.putDouble(SoulSailKeys.RETURN_Y, whimsicalideas$returnY);
            nbt.putDouble(SoulSailKeys.RETURN_Z, whimsicalideas$returnZ);
            nbt.putFloat(SoulSailKeys.RETURN_YAW, whimsicalideas$returnYaw);
            nbt.putFloat(SoulSailKeys.RETURN_PITCH, whimsicalideas$returnPitch);
        }
    }

    // ===== 接口实现：给炼化系统用 =====

    @Override public String wi$getSailId() { return whimsicalideas$SailId; }
    @Override public void wi$setSailId(String id) { whimsicalideas$SailId = id == null ? "" : id; }

    @Override public long wi$getRawSouls() { return whimsicalideas$RawSouls; }
    @Override public long wi$getRefinedSouls() { return whimsicalideas$RefinedSouls; }

    @Override public void wi$setRawSouls(long v) { whimsicalideas$RawSouls = Math.max(0L, v); }
    @Override public void wi$setRefinedSouls(long v) { whimsicalideas$RefinedSouls = Math.max(0L, v); }

    @Override
    public void wi$syncTotal() {
        whimsicalideas$souls = Math.max(0L, whimsicalideas$RawSouls + whimsicalideas$RefinedSouls);
    }

    @Override
    public int wi$getStoredCount() {
        return whimsicalideas$storedMobs == null ? 0 : whimsicalideas$storedMobs.size();
    }

    @Override
    public String wi$popOneStoredMob() {
        if (whimsicalideas$storedMobs == null || whimsicalideas$storedMobs.isEmpty()) return null;
        NbtElement e = whimsicalideas$storedMobs.get(0);
        whimsicalideas$storedMobs.remove(0);
        if (e instanceof NbtString s) return s.asString();
        return e.asString();
    }

    @Override
    public void wi$markDirtyAndSync() {
        BannerBlockEntity be = self();
        be.markDirty();
        World w = be.getWorld();
        if (w != null && !w.isClient) {
            w.updateListeners(be.getPos(), be.getCachedState(), be.getCachedState(), 3);
        }
    }

    @Override
    public SoulBannerGrade wi$getBannerGrade() {
        return SoulBannerGrade.byLevel(whimsicalideas$bannerGrade);
    }

    @Override
    public void wi$setBannerGrade(SoulBannerGrade grade) {
        this.whimsicalideas$bannerGrade = grade.getLevel();
    }
}
