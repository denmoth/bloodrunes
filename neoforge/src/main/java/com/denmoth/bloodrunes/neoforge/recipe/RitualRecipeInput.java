package com.denmoth.bloodrunes.neoforge.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import java.util.List;

public record RitualRecipeInput(ItemStack rune, List<KilledEntityData> kills, boolean isLowHp, boolean playerDied) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        return index == 0 ? rune : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 1;
    }
}