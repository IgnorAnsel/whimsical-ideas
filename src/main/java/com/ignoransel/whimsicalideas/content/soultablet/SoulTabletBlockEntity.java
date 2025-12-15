package com.ignoransel.whimsicalideas.content.soultablet;

import com.ignoransel.whimsicalideas.registry.WIBlockEntities;
import com.ignoransel.whimsicalideas.registry.WIEvents;
import com.ignoransel.whimsicalideas.util.NbtKeys;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class SoulTabletBlockEntity extends BlockEntity {
    private UUID ownerUuid;
    private String ownerName;
    private void sync() {
        if (world instanceof ServerWorld sw) {
            sw.getChunkManager().markForUpdate(pos); // ✅ 关键：发送 BlockEntityUpdateS2CPacket
        }
    }
    // 客户端渲染用（玩家头像皮肤）
    private GameProfile cachedProfile;

    public SoulTabletBlockEntity(BlockPos pos, BlockState state) {
        super(WIBlockEntities.SOUL_TABLET_BE, pos, state);
    }

    public UUID getOwnerUuid() { return ownerUuid; }
    public String getOwnerName() { return ownerName; }

    public GameProfile getOrCreateProfile() {
        if (ownerUuid == null) return null;
        if (cachedProfile == null) {
            cachedProfile = new GameProfile(ownerUuid, ownerName == null ? "Unknown" : ownerName);
        }
        return cachedProfile;
    }

    public void applyFromItem(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return;

        if (nbt.containsUuid("OwnerUuid")) {
            this.ownerUuid = nbt.getUuid("OwnerUuid");
            this.ownerName = nbt.getString("OwnerName");
            this.cachedProfile = null;

            markDirty();
            sync(); // ✅
        }
    }



    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        ownerUuid = nbt.containsUuid(NbtKeys.OWNER_UUID) ? nbt.getUuid(NbtKeys.OWNER_UUID) : null;
        ownerName = nbt.getString(NbtKeys.OWNER_NAME);
        broken = nbt.getBoolean(NbtKeys.BROKEN);
        lastDeath = nbt.getString(NbtKeys.LAST_DEATH);
        deathReason = nbt.getString(NbtKeys.DEATH_REASON);
        healthRatio = nbt.contains(NbtKeys.HEALTH_RATIO) ? nbt.getFloat(NbtKeys.HEALTH_RATIO) : 1.0f;
        cachedProfile = null;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (ownerUuid != null) nbt.putUuid(NbtKeys.OWNER_UUID, ownerUuid);
        if (ownerName != null) nbt.putString(NbtKeys.OWNER_NAME, ownerName);
        nbt.putBoolean(NbtKeys.BROKEN, broken);
        if (lastDeath != null) nbt.putString(NbtKeys.LAST_DEATH, lastDeath);
        if (deathReason != null) nbt.putString(NbtKeys.DEATH_REASON, deathReason);

        nbt.putFloat(NbtKeys.HEALTH_RATIO, healthRatio);
    }
    public void setOwner(UUID uuid, String name) {
        this.ownerUuid = uuid;
        this.ownerName = name;
        markDirty();
        if (world instanceof ServerWorld sw) {
            sw.getChunkManager().markForUpdate(pos);
        }
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        if (!world.isClient) {
        }
    }

    private boolean broken;
    private String lastDeath; // "x y z (dim)"
    private float healthRatio = 1.0f; // 0~1
    private String deathReason;
    public String getDeathReason() {
        return deathReason;
    }

    public boolean isBroken(){ return broken; }
    public String getLastDeath(){ return lastDeath; }
    public float getHealthRatio(){ return healthRatio; }

    public void setBroken(String lastDeath, String deathReason) {
        this.broken = true;
        this.lastDeath = lastDeath;
        this.deathReason = deathReason;
        markDirty();
        sync();
    }

    public void setHealthRatio(float r) {
        r = Math.max(0f, Math.min(1f, r));
        if (Math.abs(this.healthRatio - r) > 0.01f) {
            this.healthRatio = r;
            markDirty();
            sync();
        }
    }
    public void writeOwnerToStack(ItemStack stack) {
        if (ownerUuid == null) return;
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putUuid(NbtKeys.OWNER_UUID, ownerUuid);
        if (ownerName != null) nbt.putString(NbtKeys.OWNER_NAME, ownerName);
    }
    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

}
