package com.ignoransel.whimsicalideas.content.soulsail.entity;

import com.ignoransel.whimsicalideas.content.soulsail.ISoulSailItem;
import com.ignoransel.whimsicalideas.content.soulsail.SoulSailItemCompat;
import com.ignoransel.whimsicalideas.content.soulsail.SoulSailKeys;
import com.ignoransel.whimsicalideas.registry.WIBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SoulSiphonBlockEntity extends BlockEntity {

    // 只存 1 个魂幡
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);

    // ====== 蔓延吸取参数 ======
    private static final double MAX_RANGE = 20;         // 特定范围：感染半径
    private static final double SPREAD_SPEED = 0.08;     // 每 tick 扩散多少格（越大越快）
    private static final int   PULSE_COOLDOWN_TICKS = 20; // 一圈结束后等一下再开始下一圈
    private static final int   VFX_EVERY_TICKS = 2;       // 粒子频率
    private static final int MAX_ABSORB_PER_TICK = 24; // 每tick最多吞24个方块
    // ====== 状态 ======
    private boolean pulseActive = false;
    private double spreadRadius = 0.0;     // 当前感染波前半径
    private double lastRadius = 0.0;       // 上一次半径（只处理波前“新增那一圈”）
    private int cooldown = 0;
    private int vfxTick = 0;

    private static final String NBT_BANNER = "Banner";
    public SoulSiphonBlockEntity(BlockPos pos, BlockState state) {
        super(WIBlockEntities.SOUL_SIPHON, pos, state);

    }
    public ItemStack getBannerStack() {
        return items.get(0);
    }

    public static void tick(World world, BlockPos pos, BlockState state, SoulSiphonBlockEntity be) {
        if (world.isClient) return;

        ItemStack banner = be.items.get(0);
        if (banner.isEmpty() || !(banner.getItem() instanceof ISoulSailItem)) {
            // 没魂幡就停
            be.setActiveState(false);
            be.pulseActive = false;
            be.spreadRadius = 0;
            be.lastRadius = 0;
            be.cooldown = 0;
            return;
        }

        if (be.cooldown > 0) {
            be.cooldown--;
            return;
        }

        // 如果当前没在脉冲，就尝试启动一个新的“感染波”
        if (!be.pulseActive) {
            if (be.hasSoulBlocksInRange((ServerWorld) world, pos, MAX_RANGE)) {
                be.setActiveState(true);
                be.pulseActive = true;
                be.spreadRadius = 0.0;
                be.lastRadius = 0.0;
                be.sync(); // 让客户端 HUD 立刻看到进度开始
            } else {
                return;
            }
        }

        // 扩散推进
        be.lastRadius = be.spreadRadius;
        be.spreadRadius = Math.min(MAX_RANGE, be.spreadRadius + SPREAD_SPEED);

        // 只吸“新增的波前那一圈”，非常像感染蔓延
        be.absorbWaveFront((ServerWorld) world, pos, banner, be.lastRadius, be.spreadRadius);

        // 波前特效（边界圈）
        be.vfxTick++;
        if (be.vfxTick >= VFX_EVERY_TICKS) {
            be.vfxTick = 0;
            be.spawnWaveFrontVfx((ServerWorld) world, pos, be.spreadRadius);
        }

        // 结束一圈
        if (be.spreadRadius >= MAX_RANGE - 1e-6) {
            be.setActiveState(false);
            be.pulseActive = false;
            be.cooldown = PULSE_COOLDOWN_TICKS;
            be.sync();
        } else {
            // 每 tick 进度变了，发一次同步（你也可以改成每2tick同步）
            be.sync();
        }

        be.markDirty();
    }

    private boolean hasSoulBlocksInRange(ServerWorld world, BlockPos center, double range) {
        int r = (int) Math.ceil(range);
        BlockPos.Mutable m = new BlockPos.Mutable();
        for (int dy = -1; dy <= 3; dy++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    m.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    BlockState bs = world.getBlockState(m);
                    if (ExtractRule.match(bs) != null) return true;
                }
            }
        }
        return false;
    }

    /** 感染波前：处理距离在 (r0, r1] 的方块 */
    private void absorbWaveFront(ServerWorld world, BlockPos center, ItemStack banner, double r0, double r1) {
        int minX = (int) Math.floor(center.getX() - r1);
        int maxX = (int) Math.ceil(center.getX() + r1);
        int minZ = (int) Math.floor(center.getZ() - r1);
        int maxZ = (int) Math.ceil(center.getZ() + r1);

        // 高度范围：你想吸地上+一点上方
        int minY = center.getY() - 1;
        int maxY = center.getY() + 3;
        int absorbed = 0;

        double r0s = r0 * r0;
        double r1s = r1 * r1;

        BlockPos.Mutable p = new BlockPos.Mutable();

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    double dx = (x + 0.5) - (center.getX() + 0.5);
                    double dz = (z + 0.5) - (center.getZ() + 0.5);
                    double d2 = dx * dx + dz * dz;

                    // 只处理新增波前那一圈
                    if (d2 <= r0s || d2 > r1s) continue;

                    p.set(x, y, z);
                    BlockState bs = world.getBlockState(p);
                    ExtractRule rule = ExtractRule.match(bs);
                    if (rule == null) continue;

                    // 1) 加魂
                    addRefinedSouls(banner, rule.soulAmount);

                    // 2) 替换方块（保留朝向等共有属性）
                    BlockState replaced = rule.replacement.getDefaultState();
                    replaced = copyCommonProperties(bs, replaced);
                    world.setBlockState(p, replaced, Block.NOTIFY_ALL);

                    // 3) “感染蔓延”视觉：从中心抽一条魂线过去
                    spawnInfectLinkVfx(world, center, p.toImmutable());
                    absorbed++;
                    if (absorbed >= MAX_ABSORB_PER_TICK) return;
                }
            }
        }
    }

    private void spawnWaveFrontVfx(ServerWorld world, BlockPos center, double radius) {
        // 边界圈：让玩家看到“蔓延到了哪里”
        int points = Math.max(18, (int) (radius * 10));
        double cy = center.getY() + 0.6;

        double rot = world.getTime() * 0.15; // 让圈有点旋转感
        for (int i = 0; i < points; i++) {
            double ang = rot + (Math.PI * 2.0) * i / points;
            double x = center.getX() + 0.5 + Math.cos(ang) * radius;
            double z = center.getZ() + 0.5 + Math.sin(ang) * radius;

            world.spawnParticles(ParticleTypes.SOUL, x, cy, z, 1, 0, 0.02, 0, 0.0);

            if ((i % 6) == 0) {
                world.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, x, cy + 0.2, z, 1, 0, 0, 0, 0.01);
            }
        }
    }

    private void spawnInfectLinkVfx(ServerWorld world, BlockPos from, BlockPos to) {
        double x0 = from.getX() + 0.5, y0 = from.getY() + 0.9, z0 = from.getZ() + 0.5;
        double x1 = to.getX() + 0.5,   y1 = to.getY() + 0.6,   z1 = to.getZ() + 0.5;

        int seg = 8;
        for (int i = 0; i <= seg; i++) {
            double t = i / (double) seg;
            double x = x0 + (x1 - x0) * t;
            double y = y0 + (y1 - y0) * t;
            double z = z0 + (z1 - z0) * t;
            world.spawnParticles(ParticleTypes.SOUL, x, y, z, 1, 0, 0, 0, 0.0);
        }
    }

    private static void addRefinedSouls(ItemStack banner, long add) {
        SoulSailItemCompat.addRefinedSouls(banner, add);
    }


    @SuppressWarnings({"unchecked","rawtypes"})
    private static BlockState copyCommonProperties(BlockState from, BlockState to) {
        for (Property prop : from.getProperties()) {
            if (to.contains(prop)) {
                to = to.with(prop, from.get(prop));
            }
        }
        return to;
    }

    // ====== 交互：放入/取出魂幡 ======
    public ItemStack tryInsertBanner(ItemStack inHand) {
        if (inHand.isEmpty() || !(inHand.getItem() instanceof ISoulSailItem)) return inHand;
        if (!items.get(0).isEmpty()) return inHand;

        ItemStack one = inHand.copy();
        one.setCount(1);
        items.set(0, one);

        ItemStack remain = inHand.copy();
        remain.decrement(1);

        // 插入后立刻尝试启动感染波
        this.pulseActive = false;
        this.cooldown = 0;
        this.spreadRadius = 0;
        this.lastRadius = 0;

        markDirty();
        syncNow();
        return remain;
    }

    public ItemStack tryExtractBanner() {
        ItemStack cur = items.get(0);
        if (cur.isEmpty()) return ItemStack.EMPTY;

        items.set(0, ItemStack.EMPTY);

        this.pulseActive = false;
        this.cooldown = 0;
        this.spreadRadius = 0;
        this.lastRadius = 0;

        markDirty();
        syncNow();
        return cur;
    }

    private void setActiveState(boolean v) {
        if (this.world == null) return;
        BlockState st = this.world.getBlockState(this.pos);
        if (st.getBlock() instanceof com.ignoransel.whimsicalideas.content.soulsail.block.SoulSiphonBlock) {
            boolean cur = st.get(com.ignoransel.whimsicalideas.content.soulsail.block.SoulSiphonBlock.ACTIVE);
            if (cur != v) {
                this.world.setBlockState(this.pos, st.with(com.ignoransel.whimsicalideas.content.soulsail.block.SoulSiphonBlock.ACTIVE, v),
                        Block.NOTIFY_ALL);
            }
        }
    }

    public void dropContents(World world, BlockPos pos) {
        ItemStack cur = items.get(0);
        if (!cur.isEmpty()) {
            ItemEntity it = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, cur);
            world.spawnEntity(it);
            items.set(0, ItemStack.EMPTY);
        }
    }

    // ====== HUD 要用到的“同步字段” ======
    public long getBannerSoulsForClient() {
        ItemStack banner = items.get(0);
        if (banner.isEmpty()) return 0L;
        return SoulSailItemCompat.data(banner).getLong(SoulSailKeys.REFINED_SOULS);
    }

    public float getAbsorbProgressForClient() {
        if (!pulseActive) return 0f;
        return (float) Math.max(0.0, Math.min(1.0, spreadRadius / MAX_RANGE));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        // 强制清空，避免残留
        items.set(0, ItemStack.EMPTY);

        if (nbt.contains(NBT_BANNER, NbtCompound.COMPOUND_TYPE)) {
            items.set(0, ItemStack.fromNbt(nbt.getCompound(NBT_BANNER)));
        }
        else if (nbt.contains("Items", NbtElement.LIST_TYPE)) {
            DefaultedList<ItemStack> tmp = DefaultedList.ofSize(1, ItemStack.EMPTY);
            Inventories.readNbt(nbt, tmp);
            items.set(0, tmp.get(0));
        }

        pulseActive  = nbt.getBoolean("PulseActive");
        spreadRadius = nbt.getDouble("SpreadRadius");
        cooldown     = nbt.getInt("Cooldown");
        lastRadius   = nbt.contains("LastRadius") ? nbt.getDouble("LastRadius") : spreadRadius;
    }


    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        Inventories.writeNbt(nbt, items);

        ItemStack s = items.get(0);
        if (!s.isEmpty()) {
            NbtCompound tag = new NbtCompound();
            s.writeNbt(tag);
            nbt.put(NBT_BANNER, tag);
        } else {
            nbt.remove(NBT_BANNER);
        }

        nbt.putBoolean("PulseActive", pulseActive);
        nbt.putDouble("SpreadRadius", spreadRadius);
        nbt.putDouble("LastRadius", lastRadius);
        nbt.putInt("Cooldown", cooldown);
    }



    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }



    // ====== 同步到客户端（不用自定义包） ======
    private void sync() {
        if (this.world instanceof ServerWorld sw) {
            sw.getChunkManager().markForUpdate(this.pos);
        }
    }
    public void syncNow() {
        if (this.world == null || this.world.isClient) return;

        this.markDirty();

        BlockState st = this.world.getBlockState(this.pos);
        this.world.updateListeners(this.pos, st, st, Block.NOTIFY_ALL);

        if (this.world instanceof ServerWorld sw) {
            sw.getChunkManager().markForUpdate(this.pos);
        }
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }


    private enum ExtractRule {
        SOUL_SAND(Blocks.SOUL_SAND, Blocks.SAND, 20),
        SOUL_SOIL(Blocks.SOUL_SOIL, Blocks.DIRT, 20),
        SOUL_TORCH(Blocks.SOUL_TORCH, Blocks.TORCH, 40),
        SOUL_WALL_TORCH(Blocks.SOUL_WALL_TORCH, Blocks.WALL_TORCH, 40),
        SOUL_LANTERN(Blocks.SOUL_LANTERN, Blocks.LANTERN, 60),
        SOUL_CAMPFIRE(Blocks.SOUL_CAMPFIRE, Blocks.CAMPFIRE, 80),
        SOUL_FIRE(Blocks.SOUL_FIRE, Blocks.AIR, 10);

        final Block source;
        final Block replacement;
        final long soulAmount;

        ExtractRule(Block source, Block replacement, long soulAmount) {
            this.source = source;
            this.replacement = replacement;
            this.soulAmount = soulAmount;
        }

        static ExtractRule match(BlockState bs) {
            Block b = bs.getBlock();
            for (ExtractRule r : values()) {
                if (r.source == b) return r;
            }
            return null;
        }
    }
}
