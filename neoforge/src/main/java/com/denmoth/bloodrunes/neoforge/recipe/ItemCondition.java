package com.denmoth.bloodrunes.neoforge.recipe;

import net.minecraft.world.item.crafting.Ingredient;

public record ItemCondition(Ingredient ingredient, int count) {
    public static final com.mojang.serialization.Codec<ItemCondition> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.create(inst -> inst.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(ItemCondition::ingredient),
            com.mojang.serialization.Codec.INT.optionalFieldOf("count", 1).forGetter(ItemCondition::count)
    ).apply(inst, ItemCondition::new));

    public boolean matches(net.minecraft.world.item.ItemStack stack) {
        return ingredient.test(stack);
    }
}
