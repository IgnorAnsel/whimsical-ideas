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

public final class SoulSailEvents {
    private SoulSailEvents() {}

    public static void register() {
        // 外界/小世界 击杀逻辑
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killed) -> {
            if (!(entity instanceof ServerPlayerEntity)) return;
            ServerPlayerEntity sp = (ServerPlayerEntity) entity;

            if (!(killed instanceof LivingEntity)) return;
            LivingEntity le = (LivingEntity) killed;


            ItemStack sail = findHeldSoulSail(sp);
            if (sail.isEmpty()) return;

            // 只处理我们的魂幡物品
            if (!(sail.getItem() instanceof SoulSailBannerItem)) return;
            SoulSailBannerItem bannerItem = (SoulSailBannerItem) sail.getItem();
            SoulSailTier tier = bannerItem.tier();

            boolean inSoulWorld = sp.getServerWorld().getRegistryKey().equals(SoulSailRoomManager.SOUL_SAIL_DIM);

            if (inSoulWorld) {
                // 小世界击杀：扣魂
                SoulSailItemCompat.addSouls(sail, -1, tier.capacity);
                return;
            }

            // 外界击杀：加魂
            SoulSailItemCompat.addSouls(sail, +1, tier.capacity);

            // 记录待生成生物
            Identifier typeId = Registries.ENTITY_TYPE.getId(le.getType());
            if (typeId != null) {
                SoulSailItemCompat.addPendingMob(sail, typeId.toString());
            }
        });

        // 小世界玩家无敌：取消伤害
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity instanceof ServerPlayerEntity) {
                ServerPlayerEntity sp = (ServerPlayerEntity) entity;
                if (sp.getServerWorld().getRegistryKey().equals(SoulSailRoomManager.SOUL_SAIL_DIM)) {
                    return false;
                }
            }
            return true;
        });

        // 小世界生物和平：禁用 AI
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (!(world instanceof ServerWorld)) return;
            ServerWorld sw = (ServerWorld) world;

            if (!sw.getRegistryKey().equals(SoulSailRoomManager.SOUL_SAIL_DIM)) return;

            if (entity instanceof MobEntity) {
                MobEntity mob = (MobEntity) entity;
                mob.setAiDisabled(true);
                mob.setPersistent();
            }
        });
    }

    private static ItemStack findHeldSoulSail(ServerPlayerEntity p) {
        ItemStack main = p.getMainHandStack();
        if (main.getItem() instanceof SoulSailBannerItem) return main;

        ItemStack off = p.getOffHandStack();
        if (off.getItem() instanceof SoulSailBannerItem) return off;

        return ItemStack.EMPTY;
    }
}
