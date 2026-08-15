package com.denmoth.bloodrunes.neoforge.recipe.condition;

import com.denmoth.bloodrunes.neoforge.recipe.RitualRecipeInput;
import com.denmoth.bloodrunes.neoforge.setup.ModConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import java.util.Optional;

public record TrialCondition(Optional<Boolean> requiresLowHp, Optional<Boolean> requiresPlayerDeath, Optional<Integer> maxDistance) implements RitualCondition {
    public static final com.mojang.serialization.MapCodec<TrialCondition> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(inst -> inst.group(
            com.mojang.serialization.Codec.BOOL.optionalFieldOf("requires_low_hp").forGetter(TrialCondition::requiresLowHp),
            com.mojang.serialization.Codec.BOOL.optionalFieldOf("requires_player_death").forGetter(TrialCondition::requiresPlayerDeath),
            com.mojang.serialization.Codec.INT.optionalFieldOf("max_distance").forGetter(TrialCondition::maxDistance)
    ).apply(inst, TrialCondition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TrialCondition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ByteBufCodecs.BOOL), TrialCondition::requiresLowHp,
            ByteBufCodecs.optional(ByteBufCodecs.BOOL), TrialCondition::requiresPlayerDeath,
            ByteBufCodecs.optional(ByteBufCodecs.VAR_INT), TrialCondition::maxDistance,
            TrialCondition::new
    );

    @Override
    public RitualConditionType<?> getType() {
        return ModConditionTypes.TRIAL.get();
    }

    @Override
    public boolean matches(RitualRecipeInput input, Level level) {
        if (requiresLowHp.isPresent() && requiresLowHp.get() != input.isLowHp()) return false;
        if (requiresPlayerDeath.isPresent() && requiresPlayerDeath.get() != input.playerDied()) return false;
        // In the future: handle distance using input.altarPos() and tracking player position
        return true;
    }
}
