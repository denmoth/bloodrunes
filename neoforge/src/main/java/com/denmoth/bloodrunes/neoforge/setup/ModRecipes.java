package com.denmoth.bloodrunes.neoforge.setup;

import com.denmoth.bloodrunes.BloodRunes;
import com.denmoth.bloodrunes.neoforge.recipe.RitualRecipe;
import com.denmoth.bloodrunes.neoforge.recipe.RitualRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, BloodRunes.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, BloodRunes.MOD_ID);

    public static final Supplier<RecipeSerializer<RitualRecipe>> RITUAL_SERIALIZER = SERIALIZERS.register("ritual", () -> new RecipeSerializer<>(RitualRecipe.CODEC, RitualRecipe.STREAM_CODEC));
    public static final Supplier<RecipeSerializer<com.denmoth.bloodrunes.neoforge.recipe.RuneSmithingRecipe>> RUNE_SMITHING_SERIALIZER = SERIALIZERS.register("rune_smithing", () -> new RecipeSerializer<>(com.denmoth.bloodrunes.neoforge.recipe.RuneSmithingRecipe.CODEC, com.denmoth.bloodrunes.neoforge.recipe.RuneSmithingRecipe.STREAM_CODEC));
    
    public static final Supplier<RecipeType<RitualRecipe>> RITUAL_TYPE = TYPES.register("ritual", () -> new RecipeType<RitualRecipe>() {
        @Override
        public String toString() {
            return "ritual";
        }
    });
}