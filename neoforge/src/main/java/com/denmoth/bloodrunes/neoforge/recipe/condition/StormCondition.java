package com.denmoth.bloodrunes.neoforge.recipe.condition;

import com.denmoth.bloodrunes.neoforge.recipe.RitualRecipeInput;
import com.denmoth.bloodrunes.neoforge.setup.ModConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public record StormCondition() implements RitualCondition {
    public static final MapCodec<StormCondition> CODEC = MapCodec.unit(new StormCondition());

    public static final StreamCodec<RegistryFriendlyByteBuf, StormCondition> STREAM_CODEC = StreamCodec.unit(new StormCondition());

    @Override
    public RitualConditionType<?> getType() {
        return ModConditionTypes.STORM.get();
    }

    @Override
    public boolean matches(RitualRecipeInput input, Level level) {
        return level.isThundering();
    }
}
