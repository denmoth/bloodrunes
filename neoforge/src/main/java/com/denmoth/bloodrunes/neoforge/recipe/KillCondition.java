package com.denmoth.bloodrunes.neoforge.recipe;

import java.util.Optional;

public record KillCondition(EntityIngredient entity, int count, Optional<Boolean> isBaby) {
    public static final com.mojang.serialization.Codec<KillCondition> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.create(inst -> inst.group(
            EntityIngredient.CODEC.fieldOf("entity").forGetter(KillCondition::entity),
            com.mojang.serialization.Codec.INT.optionalFieldOf("count", 1).forGetter(KillCondition::count),
            com.mojang.serialization.Codec.BOOL.optionalFieldOf("is_baby").forGetter(KillCondition::isBaby)
    ).apply(inst, KillCondition::new));

    public boolean matches(KilledEntityData data) {
        if (!entity.test(data.type())) return false;
        if (isBaby.isPresent() && isBaby.get() != data.isBaby()) return false;
        return true;
    }
}