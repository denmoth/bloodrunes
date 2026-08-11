package com.denmoth.bloodrunes.neoforge.recipe;

import net.minecraft.world.entity.EntityType;

public record KilledEntityData(EntityType<?> type, boolean isBaby) {
}
