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
 * Matches weather states: "rain", "thunder", or "clear"
 */
public record WeatherCondition(String weather) implements RitualCondition {
    public static final MapCodec<WeatherCondition> CODEC = RecordCodecBuilder.mapCodec(builder ->
            builder.group(
                    Codec.STRING.fieldOf("weather").forGetter(WeatherCondition::weather)
            ).apply(builder, WeatherCondition::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, WeatherCondition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, WeatherCondition::weather,
            WeatherCondition::new
    );

    @Override
    public RitualConditionType<?> getType() {
        return ModConditionTypes.WEATHER.get();
    }

    @Override
    public boolean matches(RitualRecipeInput input, Level level) {
        if ("thunder".equalsIgnoreCase(weather)) {
            return level.isThundering();
        } else if ("rain".equalsIgnoreCase(weather)) {
            return level.isRaining();
        } else if ("clear".equalsIgnoreCase(weather)) {
            return !level.isRaining() && !level.isThundering();
        }
        return true;
    }
}
