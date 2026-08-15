package com.denmoth.bloodrunes.neoforge.recipe;

import com.denmoth.bloodrunes.neoforge.recipe.condition.RitualCondition;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

public class RitualRecipe implements Recipe<RitualRecipeInput> {
    private final Ingredient baseRune;
    private final ItemStack result;
    private final double radius;
    private final int durationTicks;
    private final List<RitualCondition> conditions;

    public static final MapCodec<RitualRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.fieldOf("base_rune").forGetter(RitualRecipe::getBaseRune),
            ItemStack.CODEC.fieldOf("result").forGetter(RitualRecipe::getResult),
            Codec.DOUBLE.optionalFieldOf("radius", 8.0).forGetter(RitualRecipe::getRadius),
            Codec.INT.optionalFieldOf("duration_ticks", 200).forGetter(RitualRecipe::getDurationTicks),
            RitualCondition.CODEC.listOf().optionalFieldOf("conditions", List.of()).forGetter(RitualRecipe::getConditions)
    ).apply(inst, RitualRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RitualRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, RitualRecipe::getBaseRune,
            ItemStack.STREAM_CODEC, RitualRecipe::getResult,
            ByteBufCodecs.DOUBLE, RitualRecipe::getRadius,
            ByteBufCodecs.INT, RitualRecipe::getDurationTicks,
            RitualCondition.STREAM_CODEC.apply(ByteBufCodecs.list()), RitualRecipe::getConditions,
            RitualRecipe::new
    );

    public RitualRecipe(Ingredient baseRune, ItemStack result, double radius, int durationTicks, List<RitualCondition> conditions) {
        this.baseRune = baseRune;
        this.result = result;
        this.radius = radius;
        this.durationTicks = durationTicks;
        this.conditions = conditions;
    }

    @Override
    public boolean matches(RitualRecipeInput input, Level level) {
        if (!baseRune.test(input.centerItem())) return false;
        
        for (RitualCondition condition : conditions) {
            if (!condition.matches(input, level)) return false;
        }
        
        return true;
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
    public int getDurationTicks() { return durationTicks; }
    public List<RitualCondition> getConditions() { return conditions; }
}