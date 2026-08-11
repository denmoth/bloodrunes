package com.denmoth.bloodrunes.neoforge.block;

import com.denmoth.bloodrunes.neoforge.setup.ModBlockEntities;
import com.denmoth.bloodrunes.neoforge.setup.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class AltarBlockEntity extends BlockEntity {
    private ItemStack runeStack = ItemStack.EMPTY;

    private boolean ritualActive = false;
    private int ticksActive = 0;
    private int villagerKills = 0;
    private int playerKills = 0;

    private int hostileKills = 0;
    private boolean playerWasLowHp = false;

    public AltarBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.ALTAR_BLOCK_ENTITY.get(), pos, blockState);
    }

    public ItemStack getRuneStack() {
        return runeStack;
    }

    public void setRuneStack(ItemStack stack) {
        this.runeStack = stack;
        this.ritualActive = false;
        this.ticksActive = 0;
        this.villagerKills = 0;
        this.playerKills = 0;
        this.hostileKills = 0;
        this.playerWasLowHp = false;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("rune", ItemStack.CODEC, runeStack);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.runeStack = input.read("rune", ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    public net.minecraft.nbt.CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveCustomOnly(provider);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, AltarBlockEntity entity) {
        if (!entity.runeStack.isEmpty() && entity.runeStack.is(ModItems.BLANK_RUNE.get())) {
            if (entity.ritualActive) {
                // Draw circle radius 8
                for (int i = 0; i < 360; i += 15) { // every 15 degrees
                    if (level.getRandom().nextFloat() < 0.1f) {
                        double rad = Math.toRadians(i);
                        double x = pos.getX() + 0.5 + Math.cos(rad) * 8.0;
                        double z = pos.getZ() + 0.5 + Math.sin(rad) * 8.0;
                        level.addParticle(ParticleTypes.ENCHANT, x, pos.getY() + 0.1, z, 0, 0.05, 0);
                    }
                }
            } else {
                // Subtle particles showing it's ready
                if (level.getRandom().nextFloat() < 0.05f) {
                    level.addParticle(ParticleTypes.DAMAGE_INDICATOR, 
                        pos.getX() + 0.5 + (level.getRandom().nextFloat() - 0.5) * 0.2, 
                        pos.getY() + 1.2, 
                        pos.getZ() + 0.5 + (level.getRandom().nextFloat() - 0.5) * 0.2, 
                        0, 0.01, 0);
                }
            }
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AltarBlockEntity entity) {
        entity.tick();
    }

    private void tick() {
        if (level == null || level.isClientSide()) return;

        if (!runeStack.isEmpty() && runeStack.is(ModItems.BLANK_RUNE.get())) {
            if (ritualActive) {
                ticksActive++;

                // Heartbeat sound - deeper and constant pitch
                if (ticksActive % 30 == 0) {
                    level.playSound(null, worldPosition, SoundEvents.WARDEN_HEARTBEAT, SoundSource.BLOCKS, 1.5F, 0.5F); // Pitch 0.5 for a deep heartbeat
                }

                if (playerKills >= 1) {
                    completeRitual(ModItems.BLOOD_RUNE.get().getDefaultInstance());
                } else if (villagerKills >= 4) {
                    completeRitual(ModItems.BLOOD_RUNE.get().getDefaultInstance());
                } else if (hostileKills >= 3 && playerWasLowHp) {
                    completeRitual(ModItems.COURAGE_RUNE.get().getDefaultInstance());
                } else if (ticksActive >= 200) { // 10 seconds timeout
                    // Failed!
                    ritualActive = false;
                    ticksActive = 0;
                    playerKills = 0;
                    villagerKills = 0;
                    hostileKills = 0;
                    playerWasLowHp = false;
                    level.playSound(null, worldPosition, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
                    setChanged();
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                }
            }
        } else {
            ritualActive = false;
            ticksActive = 0;
            playerKills = 0;
            villagerKills = 0;
            hostileKills = 0;
            playerWasLowHp = false;
        }
    }

    public void onMobKilled(Player killer, boolean isVillager, boolean isPlayer, boolean isHostile, boolean isLowHp, BlockPos victimPos) {
        if (!runeStack.isEmpty() && runeStack.is(ModItems.BLANK_RUNE.get())) {
            if (!ritualActive) {
                // Start the ritual on the FIRST kill
                ritualActive = true;
                ticksActive = 0;
                playerKills = 0;
                villagerKills = 0;
                hostileKills = 0;
                playerWasLowHp = false;
                setChanged();
                if (level != null) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                }
            }

            if (ritualActive && ticksActive < 200) {
                boolean accepted = false;
                if (isPlayer) {
                    playerKills++;
                    accepted = true;
                } else if (isVillager) {
                    villagerKills++;
                    accepted = true;
                } else if (isHostile) {
                    hostileKills++;
                    if (isLowHp) {
                        playerWasLowHp = true;
                    }
                    accepted = true;
                }
                
                if (accepted && level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    com.denmoth.bloodrunes.neoforge.network.SpawnSoulParticlesPacket packet = new com.denmoth.bloodrunes.neoforge.network.SpawnSoulParticlesPacket(victimPos, worldPosition);
                    net.neoforged.neoforge.network.PacketDistributor.sendToPlayersTrackingChunk(serverLevel, new net.minecraft.world.level.ChunkPos(worldPosition.getX() >> 4, worldPosition.getZ() >> 4), packet);
                }
            }
        }
    }

    private void completeRitual(ItemStack result) {
        this.runeStack = result.copy();
        this.ritualActive = false;
        this.ticksActive = 0;
        this.playerKills = 0;
        this.villagerKills = 0;
        this.hostileKills = 0;
        this.playerWasLowHp = false;
        
        if (level != null) {
            level.playSound(null, worldPosition, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 1.0F, 1.0F);
            // Notify client for rendering change
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);

            if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.DAMAGE_INDICATOR, 
                    worldPosition.getX() + 0.5, worldPosition.getY() + 1.2, worldPosition.getZ() + 0.5, 
                    40, 0.3, 0.3, 0.3, 0.2);
            }
        }
    }
}