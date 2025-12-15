package com.ignoransel.whimsicalideas.registry;



import com.ignoransel.whimsicalideas.content.soultablet.SoulTabletBlockEntity;
import com.ignoransel.whimsicalideas.event.MaxMiningEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.chunk.WorldChunk;

import java.util.UUID;

public final class WIEvents {
    private enum ReinforceTier {
        NORMAL, IRON, GOLD, DIAMOND, NETHERITE
    }

    private static ReinforceTier tierOf(BlockState state) {
        var b = state.getBlock();

        // 普通
        if (b == WIBlocks.SOUL_TABLET || b == WIBlocks.SOUL_TABLET_WALL) return ReinforceTier.NORMAL;

        // 铁
        if (b == WIBlocks.SOUL_TABLET_IRON || b == WIBlocks.SOUL_TABLET_IRON_WALL) return ReinforceTier.IRON;

        // 金
        if (b == WIBlocks.SOUL_TABLET_GOLD || b == WIBlocks.SOUL_TABLET_GOLD_WALL) return ReinforceTier.GOLD;

        // 钻石
        if (b == WIBlocks.SOUL_TABLET_DIAMOND || b == WIBlocks.SOUL_TABLET_DIAMOND_WALL) return ReinforceTier.DIAMOND;

        // 下界合金
        if (b == WIBlocks.SOUL_TABLET_NETHERITE || b == WIBlocks.SOUL_TABLET_NETHERITE_WALL) return ReinforceTier.NETHERITE;

        return ReinforceTier.NORMAL;
    }

    private WIEvents(){}
    public static void init() {
        // MaxMiningEvents.register();
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (!(entity instanceof ServerPlayerEntity player)) return;

            ServerWorld world = (ServerWorld) player.getWorld();
            UUID dead = player.getUuid();

            BlockPos dp = player.getBlockPos();
            String dimId = world.getRegistryKey().getValue().toString();
            String dimName =
                    dimId.equals("minecraft:overworld") ? "主世界" :
                            dimId.equals("minecraft:the_nether") ? "下界" :
                                    dimId.equals("minecraft:the_end") ? "末地" :
                                            dimId;

            String last = dp.getX() + " " + dp.getY() + " " + dp.getZ() + "（" + dimName + "）";
            Text deathText = damageSource.getDeathMessage(player);
            String deathReason = deathText.getString();
            String reason = deathReason;
//            String last = dp.getX() + " " + dp.getY() + " " + dp.getZ()
//                    + " (" + world.getRegistryKey().getValue() + ")";

            int radiusChunks = 8;
            int cx = dp.getX() >> 4;
            int cz = dp.getZ() >> 4;

            // ✅ 先收集目标，避免 CME
            java.util.ArrayList<BlockPos> targets = new java.util.ArrayList<>();

            for (int dx = -radiusChunks; dx <= radiusChunks; dx++) {
                for (int dz = -radiusChunks; dz <= radiusChunks; dz++) {
                    int x = cx + dx;
                    int z = cz + dz;

                    if (!world.isChunkLoaded(x, z)) continue;

                    WorldChunk chunk = world.getChunk(x, z);

                    // ✅ 快照遍历：复制一份 values
                    for (BlockEntity be : java.util.List.copyOf(chunk.getBlockEntities().values())) {
                        if (!(be instanceof SoulTabletBlockEntity tablet)) continue;
                        if (tablet.getOwnerUuid() == null || !dead.equals(tablet.getOwnerUuid())) continue;

                        targets.add(tablet.getPos());
                    }
                }
            }

            // ✅ 第二阶段：对快照目标执行修改世界的操作
            for (BlockPos pos : targets) {
                BlockEntity be = world.getBlockEntity(pos);
                if (!(be instanceof SoulTabletBlockEntity tablet)) continue;
                if (tablet.getOwnerUuid() == null || !dead.equals(tablet.getOwnerUuid())) continue;
                UUID ownerUuid = tablet.getOwnerUuid();
                String ownerName = tablet.getOwnerName();
                // 1) 雷电
                LightningEntity bolt = EntityType.LIGHTNING_BOLT.create(world);
                if (bolt != null) {
                    bolt.refreshPositionAfterTeleport(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                    world.spawnEntity(bolt);
                }

                // 2) 切换为毁坏方块（保持朝向/旋转/含水）
                BlockState old = world.getBlockState(pos);
                BlockState next = toBrokenState(old);
                world.setBlockState(pos, next, 3);

                // 3) 重新获取“切换后”的 BE，再写入 lastDeath/broken
                BlockEntity be2 = world.getBlockEntity(pos);
                if (be2 instanceof SoulTabletBlockEntity tablet2) {
                    tablet2.setOwner(ownerUuid, ownerName);
                    tablet2.setBroken(last, reason);
                    tablet2.markDirty();
                    world.getChunkManager().markForUpdate(pos); // 强同步
                }
            }
        });




        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (world.isClient) return true;
            if (!(world instanceof ServerWorld sw)) return true;

            ReinforceTier tier = tierOf(state);

            // 只拦截：普通/强化 的“未毁坏魂牌”（插地/挂墙）
            boolean isTarget =
                    tier != null && (
                            state.getBlock() == WIBlocks.SOUL_TABLET || state.getBlock() == WIBlocks.SOUL_TABLET_WALL ||
                                    state.getBlock() == WIBlocks.SOUL_TABLET_IRON || state.getBlock() == WIBlocks.SOUL_TABLET_IRON_WALL ||
                                    state.getBlock() == WIBlocks.SOUL_TABLET_GOLD || state.getBlock() == WIBlocks.SOUL_TABLET_GOLD_WALL ||
                                    state.getBlock() == WIBlocks.SOUL_TABLET_DIAMOND || state.getBlock() == WIBlocks.SOUL_TABLET_DIAMOND_WALL ||
                                    state.getBlock() == WIBlocks.SOUL_TABLET_NETHERITE || state.getBlock() == WIBlocks.SOUL_TABLET_NETHERITE_WALL
                    );

            if (!isTarget) return true;
            if (!(blockEntity instanceof SoulTabletBlockEntity tablet)) return true;
            if (tablet.isBroken()) return true;
            if (tablet.getOwnerUuid() == null) return true;

            // ========= 1) 破坏者BUFF（可按等级提升）
            switch (tier) {
                case NORMAL -> {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 15, 0));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 20 * 15, 0));
                }
                case IRON -> {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 25, 0));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 20 * 25, 1));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 20 * 6, 0));
                }
                case GOLD -> {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 30, 1));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 20 * 30, 2));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 20 * 8, 0));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 20 * 20, 0));
                }
                case DIAMOND -> {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 35, 1));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 20 * 35, 3));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 20 * 10, 1));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20 * 20, 0));
                }
                case NETHERITE -> {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 45, 2));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 20 * 45, 3));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 20 * 12, 1));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20 * 30, 1));
                }
            }

            // ========= 2) 绑定者反噬（按等级）
            MinecraftServer server = sw.getServer();
            UUID ownerUuid = tablet.getOwnerUuid();
            String ownerName = tablet.getOwnerName();
            ServerPlayerEntity owner = server.getPlayerManager().getPlayer(ownerUuid);

            if (owner != null) {
                switch (tier) {
                    case NORMAL -> {
                        owner.damage(owner.getDamageSources().magic(), 4.0f); // 2心
                        owner.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 20 * 20, 0));
                        owner.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20 * 12, 0));
                        owner.addStatusEffect(new StatusEffectInstance(WIStatusEffects.BACKLASH, 20 * 30, 0));
                    }
                    case IRON -> {
                        owner.damage(owner.getDamageSources().magic(), 6.0f); // 3心
                        owner.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 20 * 35, 0));
                        owner.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20 * 18, 1));
                        owner.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 20 * 6, 0));
                        owner.addStatusEffect(new StatusEffectInstance(WIStatusEffects.BACKLASH, 20 * 45, 0));
                    }
                    case GOLD -> {
                        owner.damage(owner.getDamageSources().magic(), 7.0f); // 3.5心
                        owner.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 20 * 45, 1));
                        owner.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 20 * 12, 0));
                        owner.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 20 * 5, 0));
                        owner.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 20 * 8, 0));
                        owner.addStatusEffect(new StatusEffectInstance(WIStatusEffects.BACKLASH, 20 * 60, 0));
                    }
                    case DIAMOND -> {
                        owner.damage(owner.getDamageSources().magic(), 9.0f); // 4.5心
                        owner.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 20 * 60, 1));
                        owner.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20 * 30, 2));
                        owner.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 20 * 12, 1));
                        owner.addStatusEffect(new StatusEffectInstance(WIStatusEffects.BACKLASH, 20 * 80, 0));
                    }
                    case NETHERITE -> {
                        owner.damage(owner.getDamageSources().magic(), 12.0f); // 6心
                        owner.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 20 * 80, 2));
                        owner.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20 * 40, 2));
                        owner.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 20 * 16, 1));
                        owner.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 20 * 25, 1));
                        owner.addStatusEffect(new StatusEffectInstance(WIStatusEffects.BACKLASH, 20 * 120, 0));
                    }
                }
            }

            // ✅ 关键：取消原版破坏流程，避免 broken 又被敲掉变空气
            return true;
        });

    }

    private static java.util.List<ItemStack> smeltDrops(ServerWorld sw, java.util.List<ItemStack> in) {
        java.util.ArrayList<ItemStack> out = new java.util.ArrayList<>();
        var rm = sw.getRecipeManager();

        for (ItemStack st : in) {
            if (st.isEmpty()) continue;

            var inv = new net.minecraft.inventory.SimpleInventory(st.copyWithCount(1));
            var opt = rm.getFirstMatch(net.minecraft.recipe.RecipeType.SMELTING, inv, sw);

            if (opt.isPresent()) {
                ItemStack result = opt.get().getOutput(sw.getRegistryManager()).copy();

                if (!result.isEmpty()) {
                    result.setCount(result.getCount() * st.getCount());
                    out.add(result);
                    continue;
                }
            }
            out.add(st);
        }
        return out;
    }

    private static <T extends Comparable<T>> BlockState copyIfPresent(BlockState from, BlockState to, Property<T> prop) {
        if (from.contains(prop) && to.contains(prop)) {
            return to.with(prop, from.get(prop));
        }
        return to;
    }

    private static BlockState toBrokenState(BlockState old) {
        BlockState next = old;

        if (old.getBlock() == WIBlocks.SOUL_TABLET) {
            next = WIBlocks.SOUL_TABLET_BROKEN.getDefaultState();
            next = copyIfPresent(old, next, Properties.ROTATION);
            next = copyIfPresent(old, next, Properties.WATERLOGGED);
            return next;
        }

        if (old.getBlock() == WIBlocks.SOUL_TABLET_WALL) {
            next = WIBlocks.SOUL_TABLET_BROKEN_WALL.getDefaultState();
            next = copyIfPresent(old, next, Properties.HORIZONTAL_FACING);
            next = copyIfPresent(old, next, Properties.WATERLOGGED);
            return next;
        }

        // ✅ 强化版：铁/金/钻石/下界合金（照抄下面四段）
        if (old.getBlock() == WIBlocks.SOUL_TABLET_IRON) {
            next = WIBlocks.SOUL_TABLET_IRON_BROKEN.getDefaultState();
            next = copyIfPresent(old, next, Properties.ROTATION);
            next = copyIfPresent(old, next, Properties.WATERLOGGED);
            return next;
        }
        if (old.getBlock() == WIBlocks.SOUL_TABLET_IRON_WALL) {
            next = WIBlocks.SOUL_TABLET_IRON_BROKEN_WALL.getDefaultState();
            next = copyIfPresent(old, next, Properties.HORIZONTAL_FACING);
            next = copyIfPresent(old, next, Properties.WATERLOGGED);
            return next;
        }

        if (old.getBlock() == WIBlocks.SOUL_TABLET_GOLD) {
            next = WIBlocks.SOUL_TABLET_GOLD_BROKEN.getDefaultState();
            next = copyIfPresent(old, next, Properties.ROTATION);
            next = copyIfPresent(old, next, Properties.WATERLOGGED);
            return next;
        }
        if (old.getBlock() == WIBlocks.SOUL_TABLET_GOLD_WALL) {
            next = WIBlocks.SOUL_TABLET_GOLD_BROKEN_WALL.getDefaultState();
            next = copyIfPresent(old, next, Properties.HORIZONTAL_FACING);
            next = copyIfPresent(old, next, Properties.WATERLOGGED);
            return next;
        }

        if (old.getBlock() == WIBlocks.SOUL_TABLET_DIAMOND) {
            next = WIBlocks.SOUL_TABLET_DIAMOND_BROKEN.getDefaultState();
            next = copyIfPresent(old, next, Properties.ROTATION);
            next = copyIfPresent(old, next, Properties.WATERLOGGED);
            return next;
        }
        if (old.getBlock() == WIBlocks.SOUL_TABLET_DIAMOND_WALL) {
            next = WIBlocks.SOUL_TABLET_DIAMOND_BROKEN_WALL.getDefaultState();
            next = copyIfPresent(old, next, Properties.HORIZONTAL_FACING);
            next = copyIfPresent(old, next, Properties.WATERLOGGED);
            return next;
        }

        if (old.getBlock() == WIBlocks.SOUL_TABLET_NETHERITE) {
            next = WIBlocks.SOUL_TABLET_NETHERITE_BROKEN.getDefaultState();
            next = copyIfPresent(old, next, Properties.ROTATION);
            next = copyIfPresent(old, next, Properties.WATERLOGGED);
            return next;
        }
        if (old.getBlock() == WIBlocks.SOUL_TABLET_NETHERITE_WALL) {
            next = WIBlocks.SOUL_TABLET_NETHERITE_BROKEN_WALL.getDefaultState();
            next = copyIfPresent(old, next, Properties.HORIZONTAL_FACING);
            next = copyIfPresent(old, next, Properties.WATERLOGGED);
            return next;
        }

        return old;
    }


}
