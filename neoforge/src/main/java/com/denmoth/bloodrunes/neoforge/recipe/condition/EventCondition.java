package com.denmoth.bloodrunes.neoforge.recipe.condition;

import com.denmoth.bloodrunes.neoforge.recipe.RitualRecipeInput;
import com.denmoth.bloodrunes.neoforge.setup.ModConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import java.util.Optional;

public record EventCondition(String eventId) implements RitualCondition {
    public static final com.mojang.serialization.MapCodec<EventCondition> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(inst -> inst.group(
            com.mojang.serialization.Codec.STRING.optionalFieldOf("event_id", "").forGetter(EventCondition::eventId)
    ).apply(inst, EventCondition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EventCondition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, EventCondition::eventId,
            EventCondition::new
    );

    @Override
    public RitualConditionType<?> getType() {
        return ModConditionTypes.EVENT.get();
    }

    @Override
    public boolean matches(RitualRecipeInput input, Level level) {
        return true;
    }
}
