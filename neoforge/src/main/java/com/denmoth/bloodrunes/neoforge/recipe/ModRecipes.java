package com.denmoth.bloodrunes.neoforge.recipe;

import com.denmoth.bloodrunes.BloodRunes;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, BloodRunes.MOD_ID);

    public static final Supplier<RecipeSerializer<RuneSmithingRecipe>> RUNE_SMITHING_SERIALIZER = RECIPE_SERIALIZERS.register("rune_smithing",
            RuneSmithingRecipe.Serializer::new);
}
