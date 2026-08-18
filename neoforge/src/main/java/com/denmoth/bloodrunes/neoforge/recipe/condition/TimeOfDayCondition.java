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
 * Matches a specific range of daytime in ticks (0..24000).
 * e.g. Night: 13000..23000, Midnight: 17500..18500
 */
public record TimeOfDayCondition(long minTime, long maxTime) implements RitualCondition {
    public static final MapCodec<TimeOfDayCondition> CODEC = RecordCodecBuilder.mapCodec(builder ->
            builder.group(
                    Codec.LONG.fieldOf("min_time").forGetter(TimeOfDayCondition::minTime),
                    Codec.LONG.fieldOf("max_time").forGetter(TimeOfDayCondition::maxTime)
            ).apply(builder, TimeOfDayCondition::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, TimeOfDayCondition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG, TimeOfDayCondition::minTime,
            ByteBufCodecs.VAR_LONG, TimeOfDayCondition::maxTime,
            TimeOfDayCondition::new
    );

    @Override
    public RitualConditionType<?> getType() {
        return ModConditionTypes.TIME_OF_DAY.get();
    }

    @Override
    public boolean matches(RitualRecipeInput input, Level level) {
        long time = level.getOverworldClockTime() % 24000L;
        return time >= this.minTime && time <= this.maxTime;
    }
}
