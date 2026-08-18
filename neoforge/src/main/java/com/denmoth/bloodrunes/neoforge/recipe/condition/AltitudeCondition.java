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
 * Matches altar Y coordinate (e.g. minY: 150 for mountaintops, maxY: 0 for deep caves)
 */
public record AltitudeCondition(int minY, int maxY) implements RitualCondition {
    public static final MapCodec<AltitudeCondition> CODEC = RecordCodecBuilder.mapCodec(builder ->
            builder.group(
                    Codec.INT.fieldOf("min_y").forGetter(AltitudeCondition::minY),
                    Codec.INT.fieldOf("max_y").forGetter(AltitudeCondition::maxY)
            ).apply(builder, AltitudeCondition::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, AltitudeCondition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, AltitudeCondition::minY,
            ByteBufCodecs.VAR_INT, AltitudeCondition::maxY,
            AltitudeCondition::new
    );

    @Override
    public RitualConditionType<?> getType() {
        return ModConditionTypes.ALTITUDE.get();
    }

    @Override
    public boolean matches(RitualRecipeInput input, Level level) {
        int y = input.altarPos().getY();
        return y >= this.minY && y <= this.maxY;
    }
}
