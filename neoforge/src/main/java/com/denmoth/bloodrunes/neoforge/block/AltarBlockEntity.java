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
    public static final java.util.Set<AltarBlockEntity> ACTIVE_ALTARS = java.util.Collections.newSetFromMap(new java.util.WeakHashMap<>());

    private net.minecraft.core.NonNullList<ItemStack> items = net.minecraft.core.NonNullList.withSize(10, ItemStack.EMPTY);

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
                || stack.getItem() instanceof net.minecraft.world.item.ShieldItem
                || stack.is(com.denmoth.bloodrunes.neoforge.setup.ModItems.BLANK_RUNE.get())) {
            return 3;
        }
        net.minecraft.resources.Identifier id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id.getPath().endsWith("_rune") && !id.getPath().equals("blank_rune")) {
            return 2;
        }
        return 1;
    }
    
    public boolean addItem(ItemStack stack, Player player) {
        if (stack.isEmpty()) return false;
        
        int newPrio = getPriority(stack);
        
        if (newPrio == 3) {
            if (items.get(0).isEmpty()) {
                items.set(0, stack.copyWithCount(1));
                stack.shrink(1);
                interactedPlayers.add(player.getUUID());
                onChange();
                return true;
            }
        } else if (newPrio == 2) {
            if (items.get(9).isEmpty()) {
                items.set(9, stack.copyWithCount(1));
                stack.shrink(1);
                interactedPlayers.add(player.getUUID());
                onChange();
                return true;
            }
        } else {
            if (addToCircle(stack)) {
                stack.shrink(1);
                interactedPlayers.add(player.getUUID());
                onChange();
                return true;
            }
        }
        return false;
    }
    
    private boolean addToCircle(ItemStack stack) {
        for (int i = 1; i < 9; i++) {
            if (items.get(i).isEmpty()) {
                items.set(i, stack.copyWithCount(1));
                return true;
            }
        }
        return false;
    }
    
    public void removeItem(int slot, Player player) {
        if (!items.get(slot).isEmpty()) {
            ItemStack stack = items.get(slot).copy();
            items.set(slot, ItemStack.EMPTY);
            if (!level.isClientSide()) {
                if (ritualActive) {
                    // Punish the player for interrupting the ritual
                    if (level instanceof net.minecraft.server.level.ServerLevel sl) {
                        net.minecraft.world.entity.Entity entity = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.get(net.minecraft.resources.Identifier.withDefaultNamespace("lightning_bolt")).get().value().create(sl, net.minecraft.world.entity.EntitySpawnReason.COMMAND);
                        if (entity instanceof net.minecraft.world.entity.LightningBolt bolt) {
                            bolt.setPos(player.getX(), player.getY(), player.getZ());
                            sl.addFreshEntity(bolt);
                        }
                    }
                    cancelRitual(); // This triggers the extinguish sound and resets state
                }
                
                net.minecraft.world.entity.item.ItemEntity itemEntity = new net.minecraft.world.entity.item.ItemEntity(
                    level, worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, stack
                );
                level.addFreshEntity(itemEntity);
                onChange();
            }
        }
    }
    
    private java.util.List<com.denmoth.bloodrunes.neoforge.recipe.KilledEntityData> recordedKills = new java.util.ArrayList<>();
    private boolean playerDied = false;
    private final java.util.Set<java.util.UUID> interactedPlayers = new java.util.HashSet<>();
    private int duration = 600;

    public boolean isRitualActive() { return ritualActive; }
    public int getTicksActive() { return ticksActive; }
    public int getDuration() { return duration; }

    private void onChange() {
        this.ritualActive = false;
        this.ticksActive = 0;
        this.recordedKills.clear();
        this.playerWasLowHp = false;
        this.playerDied = false;
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
        output.putInt("ticksActive", ticksActive);
        output.putBoolean("playerWasLowHp", playerWasLowHp);
        output.putBoolean("playerDied", playerDied);
        // Note: Full serialization of recordedKills requires a Codec for it, for simplicity we rely on active runtime list here unless the chunk unloads mid-ritual.
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        java.util.List<ItemStack> list = input.read("items", ItemStack.OPTIONAL_CODEC.listOf()).orElse(java.util.Collections.emptyList());
        this.items.clear();
        for(int i = 0; i < Math.min(list.size(), 10); i++) {
            this.items.set(i, list.get(i));
        }
        this.ritualActive = input.getBooleanOr("ritualActive", false);
        this.ticksActive = input.getIntOr("ticksActive", 0);
        this.playerWasLowHp = input.getBooleanOr("playerWasLowHp", false);
        this.playerDied = input.getBooleanOr("playerDied", false);
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
        if (!entity.items.get(0).isEmpty()) {
            if (entity.ritualActive) {
                entity.ticksActive++; // Sync visually
                
                float progress = 0.0f;
                if (entity.duration > 0) {
                    progress = Math.min(1.0f, (float) entity.ticksActive / entity.duration);
                }
                
                // Particle circle expands at start, holds, and collapses at end
                double currentRadius = 8.0;
                if (progress < 0.1f) {
                    currentRadius = 8.0 * (progress / 0.1f); // Expand over first 10%
                } else if (progress > 0.9f) {
                    currentRadius = 8.0 * ((1.0f - progress) / 0.1f); // Collapse over last 10%
                }

                // Draw circle on the ground radius and floating enchant particles
                for (int i = 0; i < 360; i += 5) {
                    if (level.getRandom().nextFloat() < 0.3f) {
                        double rad = Math.toRadians(i);
                        double cx = pos.getX() + 0.5 + Math.cos(rad) * currentRadius;
                        double cz = pos.getZ() + 0.5 + Math.sin(rad) * currentRadius;
                        level.addParticle(net.minecraft.core.particles.ParticleTypes.SOUL_FIRE_FLAME, cx, pos.getY() + 1.0, cz, 0, 0.05, 0);
                        if (level.getRandom().nextFloat() < 0.4f) {
                            level.addParticle(net.minecraft.core.particles.ParticleTypes.ENCHANT, cx, pos.getY() + 1.0 + level.getRandom().nextDouble() * 2.5, cz, 0, 0.1, 0);
                        }
                    }
                }

                if (level.getRandom().nextFloat() < 0.1f) {
                    level.addParticle(net.minecraft.core.particles.ParticleTypes.SOUL, 
                        pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, 
                        0, 0.05, 0);
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

    public void setRemoved() {
        super.setRemoved();
        ACTIVE_ALTARS.remove(this);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AltarBlockEntity entity) {
        if (entity.ritualActive) {
            ACTIVE_ALTARS.add(entity);
        } else {
            ACTIVE_ALTARS.remove(entity);
        }
        entity.tick();
    }

    private void tick() {
        if (level == null || level.isClientSide()) return;

        if (!items.get(0).isEmpty()) {
            java.util.List<ItemStack> circleItems = new java.util.ArrayList<>();
            for (int i = 1; i < 9; i++) {
                if (!items.get(i).isEmpty()) {
                    circleItems.add(items.get(i));
                }
            }
            
            com.denmoth.bloodrunes.neoforge.recipe.RitualRecipeInput input = new com.denmoth.bloodrunes.neoforge.recipe.RitualRecipeInput(
                items.get(0), circleItems, recordedKills, playerWasLowHp, playerDied, ticksActive, worldPosition, interactedPlayers
            );

            if (!ritualActive) {
                // Try to start a ritual that doesn't require kills (or matches current state)
                java.util.Optional<net.minecraft.world.item.crafting.RecipeHolder<com.denmoth.bloodrunes.neoforge.recipe.RitualRecipe>> recipeOpt = java.util.Optional.empty();
                if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    recipeOpt = serverLevel.getServer().getRecipeManager().getRecipeFor((net.minecraft.world.item.crafting.RecipeType<com.denmoth.bloodrunes.neoforge.recipe.RitualRecipe>)com.denmoth.bloodrunes.neoforge.setup.ModRecipes.RITUAL_TYPE.get(), input, serverLevel);
                }
                if (recipeOpt.isPresent() || checkRistublot(input)) {
                    ritualActive = true;
                    ticksActive = 0;
                    setChanged();
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                }
            }
            
            if (ritualActive) {
                ticksActive++;

                // Retrieve recipe to know duration
                java.util.Optional<net.minecraft.world.item.crafting.RecipeHolder<com.denmoth.bloodrunes.neoforge.recipe.RitualRecipe>> currentRecipeOpt = java.util.Optional.empty();
                if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    currentRecipeOpt = serverLevel.getServer().getRecipeManager().getRecipeFor((net.minecraft.world.item.crafting.RecipeType<com.denmoth.bloodrunes.neoforge.recipe.RitualRecipe>)com.denmoth.bloodrunes.neoforge.setup.ModRecipes.RITUAL_TYPE.get(), input, serverLevel);
                    if (currentRecipeOpt.isPresent()) {
                        this.duration = currentRecipeOpt.get().value().getDurationTicks();
                    } else if (checkRistublot(input)) {
                        this.duration = 200;
                    } else {
                        this.duration = 600;
                    }
                }

                // Heartbeat sound - accelerates as ritual progresses
                float progress = Math.min(1.0f, (float) ticksActive / duration);
                int heartbeatInterval = Math.max(10, (int) (40 - (progress * 30))); // 40 ticks at start, down to 10 at end
                float pitch = 0.5f + (progress * 0.5f); // Pitch increases slightly

                if (ticksActive % heartbeatInterval == 0) {
                    level.playSound(null, worldPosition, SoundEvents.WARDEN_HEARTBEAT, SoundSource.BLOCKS, 1.5F, pitch);
                }

                // Block entities from crossing radius 8, y +12 / -8
                net.minecraft.world.phys.AABB bounds = new net.minecraft.world.phys.AABB(
                        worldPosition.getX() + 0.5 - 9, worldPosition.getY() - 8, worldPosition.getZ() + 0.5 - 9,
                        worldPosition.getX() + 0.5 + 9, worldPosition.getY() + 12, worldPosition.getZ() + 0.5 + 9
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

                // Re-evaluate input with new ticksActive
                input = new com.denmoth.bloodrunes.neoforge.recipe.RitualRecipeInput(
                    items.get(0), circleItems, recordedKills, playerWasLowHp, playerDied, ticksActive, worldPosition, interactedPlayers
                );
                java.util.Optional<net.minecraft.world.item.crafting.RecipeHolder<com.denmoth.bloodrunes.neoforge.recipe.RitualRecipe>> recipeOpt = java.util.Optional.empty();
                if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    recipeOpt = serverLevel.getServer().getRecipeManager().getRecipeFor((net.minecraft.world.item.crafting.RecipeType<com.denmoth.bloodrunes.neoforge.recipe.RitualRecipe>)com.denmoth.bloodrunes.neoforge.setup.ModRecipes.RITUAL_TYPE.get(), input, serverLevel);
                }
                
                boolean isSacrificeRitual = recipeOpt.isPresent() && recipeOpt.get().value().getConditions().stream().anyMatch(c -> c instanceof com.denmoth.bloodrunes.neoforge.recipe.condition.SacrificeCondition);
                boolean isKillRitual = recipeOpt.isPresent() && !isSacrificeRitual;
                
                if (ticksActive >= duration || (isKillRitual && recipeOpt.isPresent())) {
                    if (recipeOpt.isPresent()) {
                        completeRitual(recipeOpt.get().value().assemble(input));
                    } else if (checkRistublot(input)) {
                        completeRitual(assembleRistublot(input));
                    } else {
                        cancelRitual();
                    }
                }
            }
        } else {
            cancelRitual();
        }
    }
    
    private void cancelRitual() {
        if (!ritualActive) return;
        ritualActive = false;
        ticksActive = 0;
        recordedKills.clear();
        playerWasLowHp = false;
        playerDied = false;
        if (level != null) {
            level.playSound(null, worldPosition, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, 
                    worldPosition.getX() + 0.5, worldPosition.getY() + 1.2, worldPosition.getZ() + 0.5, 
                    50, 0.5, 0.5, 0.5, 0.1);
            }
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void onMobKilled(Player killer, boolean isVillager, boolean isPlayer, boolean isHostile, boolean isLowHp, BlockPos victimPos, net.minecraft.world.entity.LivingEntity victim) {
        if (!items.get(0).isEmpty()) {
            if (!ritualActive) {
                // Start the ritual on the FIRST kill
                ritualActive = true;
                ticksActive = 0;
                recordedKills.clear();
                playerWasLowHp = false;
                playerDied = false;
                setChanged();
                if (level != null) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                }
            }

            if (ritualActive) {
                if (isLowHp) playerWasLowHp = true;
                if (isPlayer) playerDied = true;
                
                recordedKills.add(new com.denmoth.bloodrunes.neoforge.recipe.KilledEntityData(victim.getType(), victim.isBaby()));
                
                if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    com.denmoth.bloodrunes.neoforge.network.SpawnSoulParticlesPacket packet = new com.denmoth.bloodrunes.neoforge.network.SpawnSoulParticlesPacket(victimPos, worldPosition);
                    net.neoforged.neoforge.network.PacketDistributor.sendToPlayersTrackingChunk(serverLevel, new net.minecraft.world.level.ChunkPos(worldPosition.getX() >> 4, worldPosition.getZ() >> 4), packet);
                }
            }
        }
    }

    private void completeRitual(ItemStack result) {
        this.items.set(0, result.copy());
        for (int i = 1; i < 10; i++) {
            this.items.set(i, ItemStack.EMPTY);
        }
        this.ritualActive = false;
        this.ticksActive = 0;
        this.recordedKills.clear();
        this.playerWasLowHp = false;
        this.playerDied = false;
        
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
    
    private boolean checkRistublot(com.denmoth.bloodrunes.neoforge.recipe.RitualRecipeInput input) {
        if (!isRuneCompatible(input.centerItem())) return false;
        
        int amethystCount = 0;
        int echoShardCount = 0;
        boolean hasRune = false;
        
        ItemStack runeStack = items.get(9);
        if (!runeStack.isEmpty()) {
            net.minecraft.resources.Identifier id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(runeStack.getItem());
            if (id.getPath().endsWith("_rune")) {
                hasRune = true;
            }
        }
        
        for (ItemStack stack : input.circleItems()) {
            if (stack.is(net.minecraft.world.item.Items.AMETHYST_SHARD)) {
                amethystCount += stack.getCount();
            } else if (stack.is(net.minecraft.world.item.Items.ECHO_SHARD)) {
                echoShardCount += stack.getCount();
            }
        }
        if (amethystCount < 4 || echoShardCount < 4 || !hasRune) return false;
        
        // Check if nearest player has enough XP
        if (this.level != null) {
            Player nearestPlayer = this.level.getNearestPlayer(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, 8.0, false);
            if (nearestPlayer != null) {
                int runeTier = 1;
                ItemStack runeStackForXp = items.get(9);
                if (!runeStackForXp.isEmpty()) {
                    net.minecraft.resources.Identifier id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(runeStackForXp.getItem());
                    String path = id.getPath();
                    if (path.endsWith("_rune")) {
                        runeTier = getRuneTier(path.replace("_rune", ""));
                    }
                }
                int requiredXp = 15 + (runeTier - 1) * 5;
                if (nearestPlayer.experienceLevel >= requiredXp || nearestPlayer.isCreative()) {
                    return true;
                }
            }
        }
        return false;
    }

    private ItemStack assembleRistublot(com.denmoth.bloodrunes.neoforge.recipe.RitualRecipeInput input) {
        ItemStack result = input.centerItem().copy();
        ItemStack runeStack = items.get(9);
        
        if (!runeStack.isEmpty()) {
            result.set(com.denmoth.bloodrunes.neoforge.setup.ModDataComponents.RUNE_DATA.get(), new com.denmoth.bloodrunes.neoforge.setup.ModDataComponents.RuneData(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(runeStack.getItem()).toString(), 0L));
        }
        
        if (this.level != null) {
            Player nearestPlayer = this.level.getNearestPlayer(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, 8.0, false);
            if (nearestPlayer != null && !nearestPlayer.isCreative()) {
                int runeTier = 1;
                if (!runeStack.isEmpty()) {
                    net.minecraft.resources.Identifier id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(runeStack.getItem());
                    String path = id.getPath();
                    if (path.endsWith("_rune")) {
                        runeTier = getRuneTier(path.replace("_rune", ""));
                    }
                }
                int requiredXp = 15 + (runeTier - 1) * 5;
                nearestPlayer.giveExperienceLevels(-requiredXp);
                // Experience sucking particles
                if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    com.denmoth.bloodrunes.neoforge.network.SpawnXpParticlesPacket packet = new com.denmoth.bloodrunes.neoforge.network.SpawnXpParticlesPacket(nearestPlayer.blockPosition(), worldPosition);
                    net.neoforged.neoforge.network.PacketDistributor.sendToPlayersTrackingChunk(serverLevel, new net.minecraft.world.level.ChunkPos(worldPosition.getX() >> 4, worldPosition.getZ() >> 4), packet);
                    for (int i = 0; i < 20; i++) {
                        serverLevel.sendParticles(ParticleTypes.ENCHANT, 
                            nearestPlayer.getX(), nearestPlayer.getY() + 1.0, nearestPlayer.getZ(), 
                            1, 0, 0, 0, 1.5);
                    }
                }
            }
        }
        return result;
    }

    public static boolean isRuneCompatible(ItemStack stack) {
        if (stack.isEmpty()) return false;
        net.minecraft.world.item.Item item = stack.getItem();

        if (stack.has(net.minecraft.core.component.DataComponents.UNBREAKABLE)) return true;

        if (item instanceof net.minecraft.world.item.ShieldItem) return true;
        if (item instanceof net.minecraft.world.item.TridentItem) return true;
        if (item instanceof net.minecraft.world.item.BowItem || item instanceof net.minecraft.world.item.CrossbowItem) return true;

        int durability = stack.getMaxDamage();
        if (stack.is(net.minecraft.tags.ItemTags.SWORDS) || stack.is(net.minecraft.tags.ItemTags.AXES)
            || stack.is(net.minecraft.tags.ItemTags.PICKAXES) || stack.is(net.minecraft.tags.ItemTags.SHOVELS)
            || stack.is(net.minecraft.tags.ItemTags.HOES)) {
            return durability > 0 && durability >= 1400; // >= diamond tier
        }

        if (stack.is(net.minecraft.tags.ItemTags.HEAD_ARMOR)) {
            return durability > 0 && durability >= 330;
        } else if (stack.is(net.minecraft.tags.ItemTags.CHEST_ARMOR)) {
            return durability > 0 && durability >= 480;
        } else if (stack.is(net.minecraft.tags.ItemTags.LEG_ARMOR)) {
            return durability > 0 && durability >= 450;
        } else if (stack.is(net.minecraft.tags.ItemTags.FOOT_ARMOR)) {
            return durability > 0 && durability >= 390;
        }
        return false;
    }

    public static int getRuneTier(String runeName) {
        return switch (runeName) {
            case "uruz", "thurisaz", "isa", "sowilo", "mannaz", "ehwaz" -> 2;
            case "ingwaz", "algiz", "berkano", "eihwaz", "perthro", "dagaz" -> 3;
            case "jera", "tiwaz", "hagalaz", "othala", "nauthiz", "gebo" -> 4;
            default -> 1; // fehu, kenaz, raido, wunjo, laguz, ansuz and blank_rune
        };
    }
}