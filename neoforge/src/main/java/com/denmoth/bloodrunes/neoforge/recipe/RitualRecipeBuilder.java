package com.denmoth.bloodrunes.neoforge.recipe;

import com.denmoth.bloodrunes.neoforge.recipe.condition.RitualCondition;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Fluent builder for programmatic / datagen creation of Ritual recipes.
 */
public class RitualRecipeBuilder {
    private final Ingredient baseRune;
    private final ItemStack result;
    private double radius = 8.0;
    private int durationTicks = 200;
    private final List<RitualCondition> conditions = new ArrayList<>();

    private RitualRecipeBuilder(Ingredient baseRune, ItemStack result) {
        this.baseRune = baseRune;
        this.result = result;
    }

    public static RitualRecipeBuilder ritual(Ingredient baseRune, ItemStack result) {
        return new RitualRecipeBuilder(baseRune, result);
    }

    public RitualRecipeBuilder radius(double radius) {
        this.radius = radius;
        return this;
    }

    public RitualRecipeBuilder duration(int ticks) {
        this.durationTicks = ticks;
        return this;
    }

    public RitualRecipeBuilder condition(RitualCondition condition) {
        this.conditions.add(condition);
        return this;
    }

    public RitualRecipe build() {
        return new RitualRecipe(baseRune, result, radius, durationTicks, List.copyOf(conditions));
    }

    public void save(RecipeOutput output, Identifier id) {
        output.accept(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.RECIPE, id), build(), null);
    }
}
