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
 * Matches a specific moon phase (0 = Full moon, 4 = New moon, etc.)
 */
public record MoonPhaseCondition(int phase) implements RitualCondition {
    public static final MapCodec<MoonPhaseCondition> CODEC = RecordCodecBuilder.mapCodec(builder ->
            builder.group(
                    Codec.INT.fieldOf("phase").forGetter(MoonPhaseCondition::phase)
            ).apply(builder, MoonPhaseCondition::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, MoonPhaseCondition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, MoonPhaseCondition::phase,
            MoonPhaseCondition::new
    );

    @Override
    public RitualConditionType<?> getType() {
        return ModConditionTypes.MOON_PHASE.get();
    }

    @Override
    public boolean matches(RitualRecipeInput input, Level level) {
        int currentPhase = (int) (level.getOverworldClockTime() / 24000L % 8L + 8L) % 8;
        return currentPhase == this.phase;
    }
}
