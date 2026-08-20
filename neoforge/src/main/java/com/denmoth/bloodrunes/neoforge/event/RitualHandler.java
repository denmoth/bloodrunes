package com.denmoth.bloodrunes.neoforge.event;

import com.denmoth.bloodrunes.BloodRunes;
import com.denmoth.bloodrunes.neoforge.block.AltarBlockEntity;
import com.denmoth.bloodrunes.neoforge.setup.ModAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber(modid = BloodRunes.MOD_ID)
public class RitualHandler {
    
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof Player killer) {
            
            // Language check temporarily removed so anyone can do the ritual

            LivingEntity victim = event.getEntity();
            Level level = victim.level();
            
            if (!level.isClientSide()) {
                boolean isHostile = victim instanceof Monster;
                boolean isVillager = victim instanceof net.minecraft.world.entity.npc.villager.Villager;
                boolean isPlayer = victim instanceof Player;
                boolean isLowHp = killer.getHealth() / killer.getMaxHealth() < 0.3f;
                BlockPos killPos = victim.blockPosition();

                // Fast lookup using loaded altars in the world (O(1) instead of 4913 block scan)
                // Use config radius — compare distanceSq to avoid sqrt
                int searchR = com.denmoth.bloodrunes.neoforge.setup.ModConfig.ALTAR_SEARCH_RADIUS.get();
                double searchRSq = (double) searchR * searchR;
                for (AltarBlockEntity altar : AltarBlockEntity.LOADED_ALTARS) {
                    if (altar.getLevel() == level && altar.getBlockPos().distSqr(killPos) <= searchRSq) {
                        altar.onMobKilled(killer, isVillager, isPlayer, isHostile, isLowHp, killPos, event.getEntity());
                    }
                }
            }
        }
    }
}
