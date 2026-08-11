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
        if (event.getEntity() instanceof Player victimPlayer) {
            // Check if player died during ritual
            Level level = victimPlayer.level();
            if (!level.isClientSide) {
                BlockPos killPos = victimPlayer.blockPosition();
                BlockPos.betweenClosedStream(
                        killPos.offset(-8, -8, -8),
                        killPos.offset(8, 8, 8)
                ).forEach(pos -> {
                    if (level.getBlockEntity(pos) instanceof AltarBlockEntity altar) {
                        altar.onMobKilled(victimPlayer, null); // no specific killer needed for player death
                    }
                });
            }
        }
        
        if (event.getSource().getEntity() instanceof Player killer) {
            LivingEntity victim = event.getEntity();
            Level level = victim.level();
            
            if (!level.isClientSide) {
                BlockPos killPos = victim.blockPosition();

                // Search for AltarBlockEntity within 8 blocks
                BlockPos.betweenClosedStream(
                        killPos.offset(-8, -8, -8),
                        killPos.offset(8, 8, 8)
                ).forEach(pos -> {
                    if (level.getBlockEntity(pos) instanceof AltarBlockEntity altar) {
                        altar.onMobKilled(victim, killer);
                    }
                });
            }
        }
    }
}
