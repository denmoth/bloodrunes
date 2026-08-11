package com.denmoth.bloodrunes.neoforge.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.Level;
import java.util.List;

public class RitualRecipe implements Recipe<RitualRecipeInput> {
    private final Ingredient baseRune;
    private final ItemStack result;
    private final double radius;
    private final List<RitualAlternative> alternatives;

    public static final com.mojang.serialization.MapCodec<RitualRecipe> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.fieldOf("base_rune").forGetter(RitualRecipe::getBaseRune),
            ItemStack.CODEC.fieldOf("result").forGetter(RitualRecipe::getResult),
            com.mojang.serialization.Codec.DOUBLE.optionalFieldOf("radius", 8.0).forGetter(RitualRecipe::getRadius),
            RitualAlternative.CODEC.listOf().fieldOf("alternatives").forGetter(RitualRecipe::getAlternatives)
    ).apply(inst, RitualRecipe::new));

    public static final net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, RitualRecipe> STREAM_CODEC = net.minecraft.network.codec.StreamCodec.of(
            RitualRecipe::toNetwork, RitualRecipe::fromNetwork
    );

    private static void toNetwork(net.minecraft.network.RegistryFriendlyByteBuf buf, RitualRecipe recipe) {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.baseRune);
        ItemStack.STREAM_CODEC.encode(buf, recipe.result);
        buf.writeDouble(recipe.radius);
        
        buf.writeVarInt(recipe.alternatives.size());
        for (RitualAlternative alt : recipe.alternatives) {
            buf.writeBoolean(alt.requiresPlayerDeath());
            buf.writeBoolean(alt.requiresLowHp());
            buf.writeVarInt(alt.kills().size());
            for (KillCondition kill : alt.kills()) {
                buf.writeVarInt(kill.count());
                buf.writeBoolean(kill.isBaby().isPresent());
                kill.isBaby().ifPresent(buf::writeBoolean);
                
                buf.writeBoolean(kill.entity().tag().isPresent());
                if (kill.entity().tag().isPresent()) {
                    net.minecraft.resources.Identifier.STREAM_CODEC.encode(buf, kill.entity().tag().get().location());
                } else if (kill.entity().entityType().isPresent()) {
                    net.minecraft.resources.Identifier.STREAM_CODEC.encode(buf, kill.entity().entityType().get().unwrapKey().get().identifier());
                }
            }
        }
    }

    private static RitualRecipe fromNetwork(net.minecraft.network.RegistryFriendlyByteBuf buf) {
        Ingredient baseRune = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
        ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
        double radius = buf.readDouble();
        
        int altSize = buf.readVarInt();
        List<RitualAlternative> alternatives = new java.util.ArrayList<>(altSize);
        for (int i = 0; i < altSize; i++) {
            boolean reqDeath = buf.readBoolean();
            boolean reqLowHp = buf.readBoolean();
            int killSize = buf.readVarInt();
            List<KillCondition> kills = new java.util.ArrayList<>(killSize);
            for (int j = 0; j < killSize; j++) {
                int count = buf.readVarInt();
                java.util.Optional<Boolean> isBaby = buf.readBoolean() ? java.util.Optional.of(buf.readBoolean()) : java.util.Optional.empty();
                boolean isTag = buf.readBoolean();
                net.minecraft.resources.Identifier loc = net.minecraft.resources.Identifier.STREAM_CODEC.decode(buf);
                
                EntityIngredient entity;
                if (isTag) {
                    entity = new EntityIngredient(java.util.Optional.of(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, loc)), java.util.Optional.empty());
                } else {
                    entity = new EntityIngredient(java.util.Optional.empty(), net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getOptional(loc).map(net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE::wrapAsHolder));
                }
                kills.add(new KillCondition(entity, count, isBaby));
            }
            alternatives.add(new RitualAlternative(kills, reqDeath, reqLowHp));
        }
        
        return new RitualRecipe(baseRune, result, radius, alternatives);
    }

    public RitualRecipe(Ingredient baseRune, ItemStack result, double radius, List<RitualAlternative> alternatives) {
        this.baseRune = baseRune;
        this.result = result;
        this.radius = radius;
        this.alternatives = alternatives;
    }

    @Override
    public boolean matches(RitualRecipeInput input, Level level) {
        if (!baseRune.test(input.rune())) return false;
        
        for (RitualAlternative alt : alternatives) {
            if (alt.requiresPlayerDeath() && !input.playerDied()) continue;
            if (alt.requiresLowHp() && !input.isLowHp()) continue;
            
            boolean allKillsMet = true;
            for (KillCondition cond : alt.kills()) {
                long count = input.kills().stream().filter(cond::matches).count();
                if (count < cond.count()) {
                    allKillsMet = false;
                    break;
                }
            }
            if (allKillsMet) return true;
        }
        
        return false;
    }

    @Override
    public ItemStack assemble(RitualRecipeInput input) {
        return result.copy();
    }

    @Override
    public boolean showNotification() {
        return true;
    }
    
    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeSerializer<RitualRecipe> getSerializer() {
        return (RecipeSerializer<RitualRecipe>) com.denmoth.bloodrunes.neoforge.setup.ModRecipes.RITUAL_SERIALIZER.get();
    }

    @Override
    public RecipeType<RitualRecipe> getType() {
        return (RecipeType<RitualRecipe>) com.denmoth.bloodrunes.neoforge.setup.ModRecipes.RITUAL_TYPE.get();
    }

    @Override
    public net.minecraft.world.item.crafting.PlacementInfo placementInfo() {
        return net.minecraft.world.item.crafting.PlacementInfo.create(java.util.List.of(baseRune));
    }

    @Override
    public net.minecraft.world.item.crafting.RecipeBookCategory recipeBookCategory() {
        return net.minecraft.world.item.crafting.RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of();
    }


    public Ingredient getBaseRune() { return baseRune; }
    public ItemStack getResult() { return result; }
    public double getRadius() { return radius; }
    public List<RitualAlternative> getAlternatives() { return alternatives; }


}