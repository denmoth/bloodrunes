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
import net.minecraft.world.item.crafting.SmithingTransformRecipe;

public class RuneSmithingRecipe extends SmithingTransformRecipe {
    public RuneSmithingRecipe(Ingredient template, Ingredient base, Ingredient addition) {
        super(template, base, addition, ItemStack.EMPTY);
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput input, HolderLookup.Provider provider) {
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
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.RUNE_SMITHING_SERIALIZER.get();
    }

    public static class Serializer implements RecipeSerializer<RuneSmithingRecipe> {
        private static final MapCodec<RuneSmithingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("template").forGetter(recipe -> ((RuneSmithingRecipe)recipe).template),
                Ingredient.CODEC.fieldOf("base").forGetter(recipe -> ((RuneSmithingRecipe)recipe).base),
                Ingredient.CODEC.fieldOf("addition").forGetter(recipe -> ((RuneSmithingRecipe)recipe).addition)
        ).apply(instance, RuneSmithingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, RuneSmithingRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, recipe -> ((RuneSmithingRecipe)recipe).template,
                Ingredient.CONTENTS_STREAM_CODEC, recipe -> ((RuneSmithingRecipe)recipe).base,
                Ingredient.CONTENTS_STREAM_CODEC, recipe -> ((RuneSmithingRecipe)recipe).addition,
                RuneSmithingRecipe::new
        );

        @Override
        public MapCodec<RuneSmithingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, RuneSmithingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
