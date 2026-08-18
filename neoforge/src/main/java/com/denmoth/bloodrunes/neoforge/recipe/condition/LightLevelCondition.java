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
 * Matches altar light level (e.g. minLight: 0, maxLight: 4 for pitch dark shadow rituals)
 */
public record LightLevelCondition(int minLight, int maxLight) implements RitualCondition {
    public static final MapCodec<LightLevelCondition> CODEC = RecordCodecBuilder.mapCodec(builder ->
            builder.group(
                    Codec.INT.fieldOf("min_light").forGetter(LightLevelCondition::minLight),
                    Codec.INT.fieldOf("max_light").forGetter(LightLevelCondition::maxLight)
            ).apply(builder, LightLevelCondition::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, LightLevelCondition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, LightLevelCondition::minLight,
            ByteBufCodecs.VAR_INT, LightLevelCondition::maxLight,
            LightLevelCondition::new
    );

    @Override
    public RitualConditionType<?> getType() {
        return ModConditionTypes.LIGHT_LEVEL.get();
    }

    @Override
    public boolean matches(RitualRecipeInput input, Level level) {
        int light = level.getMaxLocalRawBrightness(input.altarPos().above());
        return light >= this.minLight && light <= this.maxLight;
    }
}
