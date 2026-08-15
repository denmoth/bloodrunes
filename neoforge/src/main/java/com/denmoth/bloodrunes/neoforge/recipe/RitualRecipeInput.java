package com.denmoth.bloodrunes.neoforge.recipe;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record RitualRecipeInput(ItemStack centerItem, List<ItemStack> circleItems, List<KilledEntityData> kills, boolean isLowHp, boolean playerDied, int ticksActive, BlockPos altarPos, Set<UUID> interactedPlayers) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        if (index == 0) return centerItem;
        if (index - 1 < circleItems.size()) return circleItems.get(index - 1);
        return ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 1 + circleItems.size();
    }
}