package com.denmoth.bloodrunes.neoforge.recipe.condition;

import com.denmoth.bloodrunes.neoforge.recipe.EntityIngredient;
import com.denmoth.bloodrunes.neoforge.recipe.KilledEntityData;
import com.denmoth.bloodrunes.neoforge.recipe.RitualRecipeInput;
import com.denmoth.bloodrunes.neoforge.setup.ModConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import java.util.Optional;

public record KillCondition(EntityIngredient entity, int count, Optional<Boolean> isBaby) implements RitualCondition {
    public static final com.mojang.serialization.MapCodec<KillCondition> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(inst -> inst.group(
            EntityIngredient.CODEC.fieldOf("entity").forGetter(KillCondition::entity),
            com.mojang.serialization.Codec.INT.optionalFieldOf("count", 1).forGetter(KillCondition::count),
            com.mojang.serialization.Codec.BOOL.optionalFieldOf("is_baby").forGetter(KillCondition::isBaby)
    ).apply(inst, KillCondition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, KillCondition> STREAM_CODEC = StreamCodec.composite(
            EntityIngredient.STREAM_CODEC, KillCondition::entity,
            ByteBufCodecs.VAR_INT, KillCondition::count,
            ByteBufCodecs.optional(ByteBufCodecs.BOOL), KillCondition::isBaby,
            KillCondition::new
    );

    @Override
    public RitualConditionType<?> getType() {
        return ModConditionTypes.KILL.get();
    }

    @Override
    public boolean matches(RitualRecipeInput input, Level level) {
        long actualCount = input.kills().stream().filter(this::matchesSingle).count();
        return actualCount >= count;
    }

    private boolean matchesSingle(KilledEntityData data) {
        if (!entity.test(data.type())) return false;
        if (isBaby.isPresent() && isBaby.get() != data.isBaby()) return false;
        return true;
    }
}