package com.denmoth.bloodrunes.neoforge.recipe;

import com.denmoth.bloodrunes.neoforge.setup.ModDataComponents;
import com.denmoth.bloodrunes.neoforge.setup.ModItems;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.PlacementInfo;
import java.util.Optional;
import java.util.List;

public class RuneSmithingRecipe implements SmithingRecipe {
    final Ingredient template;
    final Ingredient base;
    final Ingredient addition;

    public RuneSmithingRecipe(Ingredient template, Ingredient base, Ingredient addition) {
        this.template = template;
        this.base = base;
        this.addition = addition;
    }

    @Override
    public boolean matches(SmithingRecipeInput input, net.minecraft.world.level.Level level) {
        return this.template.test(input.template()) && this.base.test(input.base()) && this.addition.test(input.addition());
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput input) {
        ItemStack baseStack = input.base().copy();
        ItemStack additionStack = input.addition();
        
        if (additionStack.getItem() == ModItems.BLOOD_RUNE.get()) {
            baseStack.set(ModDataComponents.RUNE_DATA, new ModDataComponents.RuneData("blood_rune", 0));
        } else if (additionStack.getItem() == ModItems.COURAGE_RUNE.get()) {
            baseStack.set(ModDataComponents.RUNE_DATA, new ModDataComponents.RuneData("courage_rune", 0));
        }
        
        return baseStack;
    }


    @Override
    public RecipeSerializer<RuneSmithingRecipe> getSerializer() {
        return (RecipeSerializer<RuneSmithingRecipe>) com.denmoth.bloodrunes.neoforge.setup.ModRecipes.RUNE_SMITHING_SERIALIZER.get();
    }

    @Override
    public RecipeType<SmithingRecipe> getType() {
        return RecipeType.SMITHING;
    }

    @Override
    public Optional<Ingredient> templateIngredient() {
        return Optional.of(template);
    }

    @Override
    public Ingredient baseIngredient() {
        return base;
    }

    @Override
    public Optional<Ingredient> additionIngredient() {
        return Optional.of(addition);
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return net.minecraft.world.item.crafting.RecipeBookCategories.SMITHING;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(List.of(template, base, addition));
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of();
    }

    public static final MapCodec<RuneSmithingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("template").forGetter(recipe -> recipe.template),
            Ingredient.CODEC.fieldOf("base").forGetter(recipe -> recipe.base),
            Ingredient.CODEC.fieldOf("addition").forGetter(recipe -> recipe.addition)
    ).apply(instance, RuneSmithingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RuneSmithingRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.template,
            Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.base,
            Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.addition,
            RuneSmithingRecipe::new
    );
}
