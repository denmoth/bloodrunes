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
    private net.minecraft.core.NonNullList<ItemStack> items = net.minecraft.core.NonNullList.withSize(9, ItemStack.EMPTY);

    private boolean ritualActive = false;
    private int ticksActive = 0;
    private int villagerKills = 0;
    private int playerKills = 0;

    private int hostileKills = 0;
    private boolean playerWasLowHp = false;

    public AltarBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.ALTAR_BLOCK_ENTITY.get(), pos, blockState);
    }

    public net.minecraft.core.NonNullList<ItemStack> getItems() {
        return items;
    }
    
    private int getPriority(ItemStack stack) {
        if (stack.is(net.minecraft.tags.ItemTags.SWORDS) || stack.is(net.minecraft.tags.ItemTags.AXES)
                || stack.is(net.minecraft.tags.ItemTags.PICKAXES) || stack.is(net.minecraft.tags.ItemTags.SHOVELS)
                || stack.is(net.minecraft.tags.ItemTags.HOES)
                || stack.is(net.minecraft.tags.ItemTags.HEAD_ARMOR) || stack.is(net.minecraft.tags.ItemTags.CHEST_ARMOR)
                || stack.is(net.minecraft.tags.ItemTags.LEG_ARMOR) || stack.is(net.minecraft.tags.ItemTags.FOOT_ARMOR)
                || stack.getItem() instanceof net.minecraft.world.item.TridentItem
                || stack.getItem() instanceof net.minecraft.world.item.ShieldItem) {
            return 3;
        }
        if (stack.is(com.denmoth.bloodrunes.neoforge.setup.ModItems.BLANK_RUNE.get()) || stack.is(com.denmoth.bloodrunes.neoforge.setup.ModItems.BLOOD_RUNE.get()) || stack.is(com.denmoth.bloodrunes.neoforge.setup.ModItems.COURAGE_RUNE.get())) {
            return 2;
        }
        return 1;
    }
    
    public boolean addItem(ItemStack stack) {
        if (stack.isEmpty()) return false;
        
        int newPrio = getPriority(stack);
        ItemStack currentCenter = items.get(0);
        int currentPrio = currentCenter.isEmpty() ? 0 : getPriority(currentCenter);
        
        if (newPrio > currentPrio) {
            // Push current center to circle if not empty
            if (!currentCenter.isEmpty()) {
                if (!addToCircle(currentCenter)) return false; // Circle full
            }
            items.set(0, stack.copyWithCount(1));
            onChange();
            return true;
        } else {
            if (addToCircle(stack.copyWithCount(1))) {
                onChange();
                return true;
            }
        }
        return false;
    }
    
    private boolean addToCircle(ItemStack stack) {
        for (int i = 1; i < 9; i++) {
            if (items.get(i).isEmpty()) {
                items.set(i, stack);
                return true;
            }
        }
        return false;
    }
    
    public ItemStack extractLastItem() {
        for (int i = 8; i >= 1; i--) {
            if (!items.get(i).isEmpty()) {
                ItemStack res = items.get(i);
                items.set(i, ItemStack.EMPTY);
                onChange();
                return res;
            }
        }
        if (!items.get(0).isEmpty()) {
            ItemStack res = items.get(0);
            items.set(0, ItemStack.EMPTY);
            onChange();
            return res;
        }
        return ItemStack.EMPTY;
    }
    
    private void onChange() {
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
        output.store("items", ItemStack.OPTIONAL_CODEC.listOf(), items);
        output.putBoolean("ritualActive", ritualActive);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        java.util.List<ItemStack> list = input.read("items", ItemStack.OPTIONAL_CODEC.listOf()).orElse(java.util.Collections.emptyList());
        this.items.clear();
        for(int i = 0; i < Math.min(list.size(), 9); i++) {
            this.items.set(i, list.get(i));
        }
        this.ritualActive = input.getBooleanOr("ritualActive", false);
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
        if (!entity.items.get(0).isEmpty() && entity.items.get(0).is(ModItems.BLANK_RUNE.get())) {
            if (entity.ritualActive) {
                // Draw circle on the ground radius 8 and floating enchant particles
                for (int i = 0; i < 360; i += 5) {
                    if (level.getRandom().nextFloat() < 0.3f) {
                        double rad = Math.toRadians(i);
                        double x = pos.getX() + 0.5 + Math.cos(rad) * 8.0;
                        double z = pos.getZ() + 0.5 + Math.sin(rad) * 8.0;
                        level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, pos.getY() + 1.0, z, 0, 0.05, 0);
                        if (level.getRandom().nextFloat() < 0.4f) {
                            level.addParticle(ParticleTypes.ENCHANT, x, pos.getY() + 1.0 + level.getRandom().nextDouble() * 2.5, z, 0, 0.1, 0);
                        }
                    }
                }
            } else {
                // Subtle particles showing it's ready
                if (level.getRandom().nextFloat() < 0.05f) {
                    level.addParticle(ParticleTypes.ENCHANT, 
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

        if (!items.get(0).isEmpty() && items.get(0).is(ModItems.BLANK_RUNE.get())) {
            if (ritualActive) {
                ticksActive++;

                // Heartbeat sound - deeper and constant pitch
                if (ticksActive % 30 == 0) {
                    level.playSound(null, worldPosition, SoundEvents.WARDEN_HEARTBEAT, SoundSource.BLOCKS, 1.5F, 0.5F); // Pitch 0.5 for a deep heartbeat
                }

                // Block entities from crossing radius 8, y +/- 8
                net.minecraft.world.phys.AABB bounds = new net.minecraft.world.phys.AABB(
                        worldPosition.getX() + 0.5 - 9, worldPosition.getY() - 8, worldPosition.getZ() + 0.5 - 9,
                        worldPosition.getX() + 0.5 + 9, worldPosition.getY() + 9, worldPosition.getZ() + 0.5 + 9
                );
                for (net.minecraft.world.entity.LivingEntity entity : level.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class, bounds)) {
                    double dx = entity.getX() - (worldPosition.getX() + 0.5);
                    double dz = entity.getZ() - (worldPosition.getZ() + 0.5);
                    double dist = Math.sqrt(dx * dx + dz * dz);

                    if (dist > 7.0 && dist < 9.0) {
                        double nx = dx / dist;
                        double nz = dz / dist;
                        if (dist < 8.0) {
                            entity.setDeltaMovement(entity.getDeltaMovement().add(-nx * 0.8, 0, -nz * 0.8));
                        } else {
                            entity.setDeltaMovement(entity.getDeltaMovement().add(nx * 0.8, 0, nz * 0.8));
                        }
                        entity.hurtMarked = true;
                    }
                }

                if (playerKills >= 1) {
                    items.set(0, ModItems.BLOOD_RUNE.get().getDefaultInstance()); onChange();
                } else if (villagerKills >= 4) {
                    items.set(0, ModItems.BLOOD_RUNE.get().getDefaultInstance()); onChange();
                } else if (hostileKills >= 3 && playerWasLowHp) {
                    items.set(0, ModItems.COURAGE_RUNE.get().getDefaultInstance()); onChange();
                } else if (ticksActive >= 200) { // 10 seconds timeout
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
        if (!items.get(0).isEmpty() && items.get(0).is(ModItems.BLANK_RUNE.get())) {
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
        this.items.set(0, result.copy());
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
                serverLevel.sendParticles(ParticleTypes.SOUL, 
                    worldPosition.getX() + 0.5, worldPosition.getY() + 1.2, worldPosition.getZ() + 0.5, 
                    100, 0.5, 0.5, 0.5, 0.2);
                serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, 
                    worldPosition.getX() + 0.5, worldPosition.getY() + 1.2, worldPosition.getZ() + 0.5, 
                    50, 0.5, 0.5, 0.5, 0.1);
                serverLevel.sendParticles(ParticleTypes.ENCHANT, 
                    worldPosition.getX() + 0.5, worldPosition.getY() + 1.2, worldPosition.getZ() + 0.5, 
                    100, 1.0, 1.0, 1.0, 0.5);
            }
        }
    }
}