package com.ignoransel.whimsicalideas.content.soulsail.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class ColoredLightningEntity extends Entity {
    private static final TrackedData<Integer> COLOR_RGB =
            DataTracker.registerData(ColoredLightningEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> LIFE_TICKS =
            DataTracker.registerData(ColoredLightningEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private float damage = 6.0f;
    private int radius = 3;
    private boolean doFire = false;

    private UUID ownerUuid;

    public ColoredLightningEntity(EntityType<? extends ColoredLightningEntity> type, World world) {
        super(type, world);
        this.noClip = true;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(COLOR_RGB, 0xFFD84A); // 默认金色
        this.dataTracker.startTracking(LIFE_TICKS, 30);
    }

    public void setColorRgb(int rgb) { this.dataTracker.set(COLOR_RGB, rgb & 0xFFFFFF); }
    public int getColorRgb() { return this.dataTracker.get(COLOR_RGB); }

    public void setLifeTicks(int t) { this.dataTracker.set(LIFE_TICKS, t); }
    public int getLifeTicks() { return this.dataTracker.get(LIFE_TICKS); }

    public void setDamage(float dmg) { this.damage = dmg; }
    public float getDamage() { return damage; }

    public void setRadius(int r) { this.radius = r; }
    public int getRadius() { return radius; }

    public void setDoFire(boolean v) { this.doFire = v; }
    public boolean getDoFire() { return doFire; }

    public void setOwner(PlayerEntity p) { this.ownerUuid = p.getUuid(); }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient) {
            if (this.age >= getLifeTicks()) this.discard();
            return;
        }

        ServerWorld sw = (ServerWorld) this.getWorld();

        if (this.age == 0) {
            sw.playSound(null, this.getBlockPos(), net.minecraft.sound.SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
                    net.minecraft.sound.SoundCategory.WEATHER, 8.0f, 1.0f);
            sw.playSound(null, this.getBlockPos(), net.minecraft.sound.SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT,
                    net.minecraft.sound.SoundCategory.WEATHER, 2.0f, 1.0f);
        }

        if (this.age == 1) {
            strike(sw);
        }

        if (this.age >= getLifeTicks()) {
            this.discard();
        }
    }

    private void strike(ServerWorld sw) {
        BlockPos pos = this.getBlockPos();

        // 伤害附近生物（不伤自己 owner 可自行加判断）
        List<LivingEntity> targets = sw.getEntitiesByClass(
                LivingEntity.class,
                this.getBoundingBox().expand(radius),
                LivingEntity::isAlive
        );

        for (LivingEntity e : targets) {
            DamageSource src = sw.getDamageSources().lightningBolt();
            e.damage(src, damage);
            e.onStruckByLightning(sw, null);
        }

        if (doFire && sw.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
            BlockPos firePos = pos.up();
            if (sw.isAir(firePos)) {
                sw.setBlockState(firePos, net.minecraft.block.Blocks.FIRE.getDefaultState());
            }
        }
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("ColorRGB")) setColorRgb(nbt.getInt("ColorRGB"));
        if (nbt.contains("LifeTicks")) setLifeTicks(nbt.getInt("LifeTicks"));
        if (nbt.contains("Damage")) this.damage = nbt.getFloat("Damage");
        if (nbt.contains("Radius")) this.radius = nbt.getInt("Radius");
        if (nbt.contains("DoFire")) this.doFire = nbt.getBoolean("DoFire");
        if (nbt.containsUuid("Owner")) this.ownerUuid = nbt.getUuid("Owner");
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("ColorRGB", getColorRgb());
        nbt.putInt("LifeTicks", getLifeTicks());
        nbt.putFloat("Damage", this.damage);
        nbt.putInt("Radius", this.radius);
        nbt.putBoolean("DoFire", this.doFire);
        if (ownerUuid != null) nbt.putUuid("Owner", ownerUuid);
    }
}
