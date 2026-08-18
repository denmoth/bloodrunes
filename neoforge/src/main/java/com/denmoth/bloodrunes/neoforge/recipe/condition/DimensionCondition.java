package com.denmoth.bloodrunes.neoforge.recipe.condition;

import com.denmoth.bloodrunes.neoforge.recipe.RitualRecipeInput;
import com.denmoth.bloodrunes.neoforge.setup.ModConditionTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

/**
 * Matches dimension: "minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"
 */
public record DimensionCondition(String dimensionId) implements RitualCondition {
    public static final MapCodec<DimensionCondition> CODEC = RecordCodecBuilder.mapCodec(builder ->
            builder.group(
                    Codec.STRING.fieldOf("dimension").forGetter(DimensionCondition::dimensionId)
            ).apply(builder, DimensionCondition::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, DimensionCondition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, DimensionCondition::dimensionId,
            DimensionCondition::new
    );

    @Override
    public RitualConditionType<?> getType() {
        return ModConditionTypes.DIMENSION.get();
    }

    @Override
    public boolean matches(RitualRecipeInput input, Level level) {
        return level.dimension().identifier().toString().equalsIgnoreCase(this.dimensionId);
    }
}
