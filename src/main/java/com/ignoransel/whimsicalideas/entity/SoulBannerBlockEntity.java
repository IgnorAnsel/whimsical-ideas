package com.ignoransel.whimsicalideas.entity;

import com.ignoransel.whimsicalideas.content.soulsail.SoulBannerGrade;
import com.ignoransel.whimsicalideas.content.soulsail.SoulSailBannerData;
import com.ignoransel.whimsicalideas.content.soulsail.SoulSailKeys;
import com.ignoransel.whimsicalideas.registry.WIBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SoulBannerBlockEntity extends BlockEntity implements SoulSailBannerData {

    // ====== Souls ======
    private long souls = 0L;
    private long rawSouls = 0L;
    private long refinedSouls = 0L;

    // ====== Identity / Stored ======
    private String sailId = "";
    private NbtList storedMobs = new NbtList();

    // ====== Room ======
    private int roomX = 0;
    private int roomZ = 0;

    // ====== Return ======
    private String returnDim = "";
    private double returnX = 0, returnY = 0, returnZ = 0;
    private float returnYaw = 0, returnPitch = 0;

    // ====== Grade / Range ======
    private int bannerGrade = 0; // 0~9
    private int lastRadius = 0;

    // ====== Ability ======
    private int selectedAbility = 0; // 默认无(0)
    private NbtCompound abilityCooldowns = new NbtCompound();

    // ====== Passive ======
    private boolean passiveSoulTotem = false;
    private boolean passiveSoulBarrier = false;
    private boolean passiveSoulDomain = false;

    // ====== Grasp ======
    private long graspUntil = 0L;
    private String graspTarget = "";

    public SoulBannerBlockEntity(BlockPos pos, BlockState state) {
        super(WIBlockEntities.SOUL_BANNER_BE, pos, state);
    }

    // =========================
    // SoulSailBannerData 接口实现
    // =========================

    @Override
    public int wi$getRoomX() {
        return roomX;
    }

    @Override
    public int wi$getRoomZ() {
        return roomZ;
    }

    @Override
    public boolean wi$isSoulSailBanner() {
        // 你是自定义 BE，这里直接 true 即可
        // 如果你想更严谨：判断 block 是否是你的魂幡方块
        return true;
    }

    @Override
    public String wi$getSailId() {
        return sailId;
    }

    @Override
    public void wi$setSailId(String id) {
        this.sailId = (id == null) ? "" : id;
        wi$markDirtyAndSync();
    }

    @Override
    public long wi$getRawSouls() {
        return rawSouls;
    }

    @Override
    public long wi$getRefinedSouls() {
        return refinedSouls;
    }

    @Override
    public void wi$setRawSouls(long v) {
        this.rawSouls = Math.max(0L, v);
        wi$syncTotal();
        wi$markDirtyAndSync();
    }

    @Override
    public void wi$setRefinedSouls(long v) {
        this.refinedSouls = Math.max(0L, v);
        wi$syncTotal();
        wi$markDirtyAndSync();
    }

    @Override
    public void wi$syncTotal() {
        this.souls = Math.max(0L, this.rawSouls + this.refinedSouls);
    }

    @Override
    public int wi$getStoredCount() {
        return storedMobs == null ? 0 : storedMobs.size();
    }

    @Override
    public String wi$popOneStoredMob() {
        if (storedMobs == null || storedMobs.isEmpty()) return null;

        NbtElement e = storedMobs.get(0);
        storedMobs.remove(0);

        wi$markDirtyAndSync();

        if (e instanceof NbtString s) return s.asString();
        return e.asString();
    }

    @Override
    public void wi$markDirtyAndSync() {
        markDirty();

        World w = getWorld();
        if (w != null && !w.isClient) {
            // 触发客户端更新（会调用 toUpdatePacket）
            w.updateListeners(getPos(), getCachedState(), getCachedState(), 3);
        }
    }

    @Override
    public SoulBannerGrade wi$getBannerGrade() {
        return SoulBannerGrade.byLevel(bannerGrade);
    }

    @Override
    public void wi$setBannerGrade(SoulBannerGrade grade) {
        this.bannerGrade = (grade == null) ? 0 : grade.getLevel();
        wi$markDirtyAndSync();
    }

    // =========================
    // NBT 读写：存档 + 同步用
    // =========================

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        // souls
        this.souls = nbt.getLong(SoulSailKeys.SOULS);
        this.rawSouls = nbt.getLong(SoulSailKeys.RAW_SOULS);
        this.refinedSouls = nbt.getLong(SoulSailKeys.REFINED_SOULS);

        // identity
        this.sailId = nbt.getString(SoulSailKeys.SAIL_ID);

        // room
        this.roomX = nbt.getInt(SoulSailKeys.ROOM_X);
        this.roomZ = nbt.getInt(SoulSailKeys.ROOM_Z);

        // return
        this.returnDim = nbt.getString(SoulSailKeys.RETURN_DIM);
        this.returnX = nbt.getDouble(SoulSailKeys.RETURN_X);
        this.returnY = nbt.getDouble(SoulSailKeys.RETURN_Y);
        this.returnZ = nbt.getDouble(SoulSailKeys.RETURN_Z);
        this.returnYaw = nbt.getFloat(SoulSailKeys.RETURN_YAW);
        this.returnPitch = nbt.getFloat(SoulSailKeys.RETURN_PITCH);

        // grade/radius
        this.bannerGrade = nbt.getInt(SoulSailKeys.BANNER_GRADE);
        this.lastRadius = nbt.getInt(SoulSailKeys.LAST_RADIUS);

        // ability
        this.selectedAbility = nbt.getInt(SoulSailKeys.SELECTED_ABILITY);
        this.abilityCooldowns = nbt.getCompound(SoulSailKeys.ABILITY_CDS);

        // passive
        this.passiveSoulTotem = nbt.getBoolean(SoulSailKeys.PASSIVE_SOUL_TOTEM);
        this.passiveSoulBarrier = nbt.getBoolean(SoulSailKeys.PASSIVE_SOUL_BARRIER);
        this.passiveSoulDomain = nbt.getBoolean(SoulSailKeys.PASSIVE_SOUL_DOMAIN);

        // grasp
        this.graspUntil = nbt.getLong(SoulSailKeys.GRASP_UNTIL);
        this.graspTarget = nbt.getString(SoulSailKeys.GRASP_TARGET);

        // stored
        if (nbt.contains(SoulSailKeys.STORED, NbtElement.LIST_TYPE)) {
            this.storedMobs = nbt.getList(SoulSailKeys.STORED, NbtElement.STRING_TYPE);
        } else {
            this.storedMobs = new NbtList();
        }

        // 兜底：确保 total 正确
        wi$syncTotal();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        wi$syncTotal();

        // souls
        nbt.putLong(SoulSailKeys.SOULS, souls);
        nbt.putLong(SoulSailKeys.RAW_SOULS, rawSouls);
        nbt.putLong(SoulSailKeys.REFINED_SOULS, refinedSouls);

        // identity
        nbt.putString(SoulSailKeys.SAIL_ID, sailId);

        // room
        nbt.putInt(SoulSailKeys.ROOM_X, roomX);
        nbt.putInt(SoulSailKeys.ROOM_Z, roomZ);

        // return（只有有维度才写也行；这里按你的 mixin 习惯写）
        if (returnDim != null && !returnDim.isEmpty()) {
            nbt.putString(SoulSailKeys.RETURN_DIM, returnDim);
            nbt.putDouble(SoulSailKeys.RETURN_X, returnX);
            nbt.putDouble(SoulSailKeys.RETURN_Y, returnY);
            nbt.putDouble(SoulSailKeys.RETURN_Z, returnZ);
            nbt.putFloat(SoulSailKeys.RETURN_YAW, returnYaw);
            nbt.putFloat(SoulSailKeys.RETURN_PITCH, returnPitch);
        }

        // grade/radius
        nbt.putInt(SoulSailKeys.BANNER_GRADE, bannerGrade);
        nbt.putInt(SoulSailKeys.LAST_RADIUS, lastRadius);

        // ability
        nbt.putInt(SoulSailKeys.SELECTED_ABILITY, selectedAbility);
        nbt.put(SoulSailKeys.ABILITY_CDS, abilityCooldowns);

        // passive
        nbt.putBoolean(SoulSailKeys.PASSIVE_SOUL_TOTEM, passiveSoulTotem);
        nbt.putBoolean(SoulSailKeys.PASSIVE_SOUL_BARRIER, passiveSoulBarrier);
        nbt.putBoolean(SoulSailKeys.PASSIVE_SOUL_DOMAIN, passiveSoulDomain);

        // grasp
        nbt.putLong(SoulSailKeys.GRASP_UNTIL, graspUntil);
        nbt.putString(SoulSailKeys.GRASP_TARGET, graspTarget == null ? "" : graspTarget);

        // stored
        if (storedMobs != null) {
            nbt.put(SoulSailKeys.STORED, storedMobs);
        }
    }

    // =========================
    // 客户端同步（渲染端读取 grade/ability 等必须）
    // =========================

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    // =========================
    // 额外：给你用的 getter/setter（渲染/逻辑都方便）
    // =========================

    public long getSouls() { return souls; }
    public long getRawSouls() { return rawSouls; }
    public long getRefinedSouls() { return refinedSouls; }

    public int getLastRadius() { return lastRadius; }
    public void setLastRadius(int r) { this.lastRadius = Math.max(0, r); wi$markDirtyAndSync(); }

    public int getSelectedAbility() { return selectedAbility; }
    public void setSelectedAbility(int id) { this.selectedAbility = Math.max(0, id); wi$markDirtyAndSync(); }

    public NbtCompound getAbilityCooldowns() { return abilityCooldowns; }
    public void setAbilityCooldowns(NbtCompound cds) {
        this.abilityCooldowns = (cds == null) ? new NbtCompound() : cds;
        wi$markDirtyAndSync();
    }

    public boolean isPassiveSoulTotem() { return passiveSoulTotem; }
    public void setPassiveSoulTotem(boolean v) { this.passiveSoulTotem = v; wi$markDirtyAndSync(); }

    public boolean isPassiveSoulBarrier() { return passiveSoulBarrier; }
    public void setPassiveSoulBarrier(boolean v) { this.passiveSoulBarrier = v; wi$markDirtyAndSync(); }

    public boolean isPassiveSoulDomain() { return passiveSoulDomain; }
    public void setPassiveSoulDomain(boolean v) { this.passiveSoulDomain = v; wi$markDirtyAndSync(); }

    public long getGraspUntil() { return graspUntil; }
    public String getGraspTarget() { return graspTarget; }
    public void setGrasp(long until, String target) {
        this.graspUntil = until;
        this.graspTarget = target == null ? "" : target;
        wi$markDirtyAndSync();
    }

    public String getReturnDim() { return returnDim; }
    public void setReturn(String dim, double x, double y, double z, float yaw, float pitch) {
        this.returnDim = dim == null ? "" : dim;
        this.returnX = x; this.returnY = y; this.returnZ = z;
        this.returnYaw = yaw; this.returnPitch = pitch;
        wi$markDirtyAndSync();
    }

    public void clearReturn() {
        this.returnDim = "";
        this.returnX = this.returnY = this.returnZ = 0;
        this.returnYaw = this.returnPitch = 0;
        wi$markDirtyAndSync();
    }

    public void setRoom(int x, int z) {
        this.roomX = x;
        this.roomZ = z;
        wi$markDirtyAndSync();
    }

    public void pushStoredMob(String id) {
        if (id == null || id.isEmpty()) return;
        if (storedMobs == null) storedMobs = new NbtList();
        storedMobs.add(NbtString.of(id));
        wi$markDirtyAndSync();
    }

    public int getBannerGradeRaw() {
        return bannerGrade;
    }
}
