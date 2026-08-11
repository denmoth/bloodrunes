package com.denmoth.bloodrunes.neoforge.block;

import com.denmoth.bloodrunes.neoforge.setup.ModBlockEntities;
import com.denmoth.bloodrunes.neoforge.setup.ModItems;
import com.denmoth.bloodrunes.neoforge.setup.ModRecipes;
import com.denmoth.bloodrunes.neoforge.recipe.RitualRecipe;
import com.denmoth.bloodrunes.neoforge.recipe.RitualRecipeInput;
import com.denmoth.bloodrunes.neoforge.recipe.KilledEntityData;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AltarBlockEntity extends BlockEntity {
    private ItemStack runeStack = ItemStack.EMPTY;

    private boolean ritualActive = false;
    private int ticksActive = 0;
    
    private final List<KilledEntityData> kills = new ArrayList<>();
    private boolean playerWasLowHp = false;
    private boolean playerDied = false;

    public AltarBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.ALTAR_BLOCK_ENTITY.get(), pos, blockState);
    }

    public ItemStack getRuneStack() {
        return runeStack;
    }

    public void setRuneStack(ItemStack stack) {
        this.runeStack = stack;
        this.ritualActive = false;
        resetRitualState();
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    private void resetRitualState() {
        this.ticksActive = 0;
        this.kills.clear();
        this.playerWasLowHp = false;
        this.playerDied = false;
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
        entity.applyBarrierPhysics();
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

                // Check recipe
                RitualRecipeInput input = new RitualRecipeInput(runeStack, kills, playerWasLowHp, playerDied);
                Optional<RecipeHolder<RitualRecipe>> recipe = level.getRecipeManager().getRecipeFor(ModRecipes.RITUAL_TYPE.get(), input, level);

                if (recipe.isPresent()) {
                    completeRitual(recipe.get().value().getResult());
                } else if (ticksActive >= 200) { // 10 seconds timeout
                    // Failed!
                    ritualActive = false;
                    resetRitualState();
                    level.playSound(null, worldPosition, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
                    setChanged();
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                }
            }
        } else {
            ritualActive = false;
            resetRitualState();
        }
    }

    public void onMobKilled(LivingEntity victim, @Nullable Player killer) {
        if (!runeStack.isEmpty() && runeStack.is(ModItems.BLANK_RUNE.get())) {
            if (!ritualActive) {
                // Start the ritual on the FIRST kill
                ritualActive = true;
                resetRitualState();
                setChanged();
                if (level != null) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                }
            }

            if (ritualActive && ticksActive < 200) {
                if (victim instanceof Player) {
                    playerDied = true;
                } else {
                    kills.add(new KilledEntityData(victim.getType(), victim.isBaby()));
                }
                
                if (killer != null && killer.getHealth() <= 3.0F) {
                    playerWasLowHp = true;
                }
                
                if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    com.denmoth.bloodrunes.neoforge.network.SpawnSoulParticlesPacket packet = new com.denmoth.bloodrunes.neoforge.network.SpawnSoulParticlesPacket(victim.blockPosition(), worldPosition);
                    net.neoforged.neoforge.network.PacketDistributor.sendToPlayersTrackingChunk(serverLevel, new net.minecraft.world.level.ChunkPos(worldPosition.getX() >> 4, worldPosition.getZ() >> 4), packet);
                }
            }
        }
    }

    private void applyBarrierPhysics() {
        if (!ritualActive || level == null) return;
        
        // Find current recipe to get radius
        double r = 8.0; // Default radius
        RitualRecipeInput input = new RitualRecipeInput(runeStack, kills, playerWasLowHp, playerDied);
        List<RecipeHolder<RitualRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(ModRecipes.RITUAL_TYPE.get());
        for (RecipeHolder<RitualRecipe> holder : recipes) {
            if (holder.value().getBaseRune().test(runeStack)) {
                r = holder.value().getRadius();
                break; // Just use the first matching base rune radius
            }
        }

        AABB box = new AABB(worldPosition).inflate(r + 2.0);
        List<net.minecraft.world.entity.Entity> entities = level.getEntitiesOfClass(net.minecraft.world.entity.Entity.class, box, 
            e -> e instanceof net.minecraft.world.entity.LivingEntity || e instanceof net.minecraft.world.entity.projectile.Projectile);
        
        double cx = worldPosition.getX() + 0.5;
        double cy = worldPosition.getY() + 0.5;
        double cz = worldPosition.getZ() + 0.5;
        
        for (net.minecraft.world.entity.Entity entity : entities) {
            double dx = entity.getX() - cx;
            double dy = entity.getY() - cy;
            double dz = entity.getZ() - cz;
            
            // 2D distance for cylinder
            double distSq = dx * dx + dz * dz;
            if (distSq > r * r && distSq < (r + 2.0) * (r + 2.0)) {
                double dist = Math.sqrt(distSq);
                double nx = dx / dist;
                double nz = dz / dist;
                
                double oldDistSq = (entity.xOld - cx) * (entity.xOld - cx) + (entity.zOld - cz) * (entity.zOld - cz);
                if (oldDistSq <= r * r) {
                    entity.setPos(cx + nx * r, entity.getY(), cz + nz * r);
                    entity.setDeltaMovement(0, entity.getDeltaMovement().y, 0);
                } else {
                    entity.setDeltaMovement(nx * 1.5, 0.2, nz * 1.5);
                }
            }
        }
    }

    private void completeRitual(ItemStack result) {
        this.runeStack = result.copy();
        this.ritualActive = false;
        resetRitualState();
        
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