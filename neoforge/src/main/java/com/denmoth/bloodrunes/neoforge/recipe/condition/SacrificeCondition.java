package com.denmoth.bloodrunes.neoforge.recipe.condition;

import com.denmoth.bloodrunes.neoforge.recipe.RitualRecipeInput;
import com.denmoth.bloodrunes.neoforge.setup.ModConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

public record SacrificeCondition(Ingredient item, int count) implements RitualCondition {
    public static final com.mojang.serialization.MapCodec<SacrificeCondition> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.fieldOf("item").forGetter(SacrificeCondition::item),
            com.mojang.serialization.Codec.INT.optionalFieldOf("count", 1).forGetter(SacrificeCondition::count)
    ).apply(inst, SacrificeCondition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SacrificeCondition> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, SacrificeCondition::item,
            ByteBufCodecs.VAR_INT, SacrificeCondition::count,
            SacrificeCondition::new
    );

    @Override
    public RitualConditionType<?> getType() {
        return ModConditionTypes.SACRIFICE.get();
    }

    @Override
    public boolean matches(RitualRecipeInput input, Level level) {
        long matched = input.circleItems().stream().filter(item::test).count();
        return matched >= count;
    }
}
