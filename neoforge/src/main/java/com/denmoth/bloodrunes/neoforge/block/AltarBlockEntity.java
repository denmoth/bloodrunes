package com.denmoth.bloodrunes.neoforge.block;

import com.denmoth.bloodrunes.neoforge.setup.ModBlockEntities;
import com.denmoth.bloodrunes.neoforge.setup.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class AltarBlockEntity extends BlockEntity {
    private boolean ritualActive = false;
    private int ticksActive = 0;
    private int passiveKills = 0;
    private int hostileKills = 0;
    private boolean playerWasLowHp = false;
    private ItemEntity currentRuneEntity = null;

    public AltarBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.ALTAR_BLOCK_ENTITY.get(), pos, blockState);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, AltarBlockEntity entity) {
        // Particles can be handled here if needed
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AltarBlockEntity entity) {
        entity.tick();
    }

    private void tick() {
        if (level == null || level.isClientSide) return;

        AABB area = new AABB(worldPosition.above());
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area);
        
        ItemEntity blankRune = null;
        for (ItemEntity item : items) {
            if (item.getItem().getItem() == ModItems.BLANK_RUNE.get()) {
                blankRune = item;
                break;
            }
        }

        if (blankRune != null) {
            if (!ritualActive) {
                ritualActive = true;
                ticksActive = 0;
                passiveKills = 0;
                hostileKills = 0;
                playerWasLowHp = false;
                currentRuneEntity = blankRune;
            } else {
                ticksActive++;
                
                // Heartbeat sound
                int soundInterval = Math.max(5, 40 - (ticksActive / 6));
                if (ticksActive % soundInterval == 0) {
                    float pitch = 1.0F + (ticksActive / 400.0F); // Increases slightly
                    level.playSound(null, worldPosition, SoundEvents.WARDEN_HEARTBEAT, SoundSource.BLOCKS, 1.0F, pitch);
                }

                if (passiveKills >= 5) {
                    completeRitual(blankRune, ModItems.BLOOD_RUNE.get().getDefaultInstance());
                } else if (hostileKills >= 3 && playerWasLowHp) {
                    completeRitual(blankRune, ModItems.COURAGE_RUNE.get().getDefaultInstance());
                } else if (ticksActive >= 200) { // 10 seconds
                    ritualActive = false;
                    // Reset to wait for them to pick it up or it starts over next tick
                }
            }
        } else {
            ritualActive = false;
            currentRuneEntity = null;
        }
    }

    public void onMobKilled(Player killer, boolean isHostile) {
        if (ritualActive && ticksActive < 200) {
            if (isHostile) {
                hostileKills++;
                if (killer.getHealth() <= 1.0F) {
                    playerWasLowHp = true;
                }
            } else {
                passiveKills++;
            }
        }
    }

    private void completeRitual(ItemEntity oldEntity, ItemStack result) {
        oldEntity.setItem(result);
        ritualActive = false;
        
        if (level != null) {
            level.playSound(null, worldPosition, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 1.0F, 1.0F);
            // Spawn blood particles
            // Send packet to clients or use level.sendParticles if on server
            // NeoForge server particle spawning:
            if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.DAMAGE_INDICATOR, 
                    worldPosition.getX() + 0.5, worldPosition.getY() + 1.2, worldPosition.getZ() + 0.5, 
                    20, 0.2, 0.2, 0.2, 0.1);
            }
        }
    }
}
