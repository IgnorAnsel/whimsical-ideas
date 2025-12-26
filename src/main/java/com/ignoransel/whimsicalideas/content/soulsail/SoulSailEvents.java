package com.ignoransel.whimsicalideas.content.soulsail;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public final class SoulSailEvents {
    private SoulSailEvents() {}

    private static final String TAG_SOUL = "wi:soul";
    private static final String TAG_SAIL_PREFIX = "wi:sail:";

    public static void register() {
        // 击杀逻辑
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, attacker, killed) -> {
            if (!(attacker instanceof ServerPlayerEntity sp)) return;
            if (!(killed instanceof LivingEntity)) return;
            System.out.println("击杀事件：击杀者 " + sp.getName().getString() + " 被击杀者 " + killed.getName().getString() + "");
            LivingEntity le = killed;

            boolean inSoulWorld = sp.getServerWorld().getRegistryKey().equals(SoulSailRoomManager.SOUL_SAIL_DIM);
            // 魂幡世界：不要求手持，从被杀实体身上反查对应魂幡扣魂
            if (inSoulWorld) {
                String sailId = getSailIdFromKilled(le);
                if (sailId == null) return; // 不是“魂幡存储生成的灵魂生物”，不扣

                ItemStack sail = findSoulSailById(sp, sailId);
                if (sail.isEmpty()) return; // 玩家背包里没有对应魂幡，就没法扣（你也可以选择别的策略）

                if (!(sail.getItem() instanceof ISoulSailItem bannerItem)) return;
                SoulSailTier tier = bannerItem.tier();

                SoulSailItemCompat.addRawSouls(sail, -1, tier.capacity);
                return;
            }

            // 魂幡世界外：必须主手/副手拿着魂幡才触发
            ItemStack held = findHeldSoulSail(sp);
            if (held.isEmpty()) return;
            if (!(held.getItem() instanceof ISoulSailItem bannerItem)) return;

            SoulSailTier tier = SoulSailItemCompat.getBannerGrade(held).getSoulSailTier();

            // 外界击杀：加魂（未炼化）
            SoulSailItemCompat.addRawSouls(held, +1, tier.capacity);
            // 记录待生成生物
            Identifier typeId = Registries.ENTITY_TYPE.getId(le.getType());
            SoulSailItemCompat.getOrCreateSailId(held);
            SoulSailItemCompat.addPendingMob(held, typeId.toString());
            if (killed instanceof ServerPlayerEntity killedPlayer) {
                ServerWorld target = sp.getServer().getWorld(SoulSailRoomManager.SOUL_SAIL_DIM);
                if (target != null) {
                    SoulSailRoomManager.ensureRoomBuilt(target, sp, held, tier);
                    killedPlayer.requestRespawn();
                    SoulSailRoomManager.teleportIntoRoom(target, killedPlayer, held, tier);
                    SoulSailRoomManager.spawnPendingMobsOnce(target, held, tier);
                    SoulSailRoomManager.applyPacifistRules(target, sp);
                }
            }
        });

        // 小世界玩家无敌：取消伤害
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity instanceof ServerPlayerEntity sp) {
                if (sp.getServerWorld().getRegistryKey().equals(SoulSailRoomManager.SOUL_SAIL_DIM)) {
                    return false;
                }
            }
            return true;
        });

        // 小世界生物和平：禁用 AI
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (!(world instanceof ServerWorld)) return;
            ServerWorld sw = world;
            if (!sw.getRegistryKey().equals(SoulSailRoomManager.SOUL_SAIL_DIM)) return;

            if (entity instanceof MobEntity mob) {
                mob.setAiDisabled(true);
                mob.setPersistent();
            }
        });
    }

    /** 从被杀实体的 command tags 中读出 sailId */
    private static String getSailIdFromKilled(LivingEntity e) {
        var tags = e.getCommandTags();
        if (!tags.contains(TAG_SOUL)) return null;

        for (String tag : tags) {
            if (tag.startsWith(TAG_SAIL_PREFIX)) {
                return tag.substring(TAG_SAIL_PREFIX.length());
            }
        }
        return null;
    }

    /** 在玩家背包里查找 SailId 匹配的魂幡 */
    private static ItemStack findSoulSailById(ServerPlayerEntity sp, String sailId) {
        var inv = sp.getInventory();
        for (int i = 0; i < inv.size(); i++) {
            ItemStack s = inv.getStack(i);
            if (!(s.getItem() instanceof ISoulSailItem)) continue;

            // 注意：这里要求 SoulSailItemCompat.data(ItemStack) 是 public
            var nbt = SoulSailItemCompat.data(s);
            if (sailId.equals(nbt.getString(SoulSailKeys.SAIL_ID))) return s;
        }
        return ItemStack.EMPTY;
    }

    /** 只查主手/副手 */
    private static ItemStack findHeldSoulSail(ServerPlayerEntity p) {
        ItemStack main = p.getMainHandStack();
        if (main.getItem() instanceof ISoulSailItem) return main;

        ItemStack off = p.getOffHandStack();
        if (off.getItem() instanceof ISoulSailItem) return off;

        return ItemStack.EMPTY;
    }

 }
