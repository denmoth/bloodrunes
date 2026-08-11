package com.denmoth.bloodrunes.neoforge.recipe;

import java.util.List;
import java.util.Optional;

public record RitualAlternative(List<KillCondition> kills, boolean requiresPlayerDeath, boolean requiresLowHp) {
    public static final com.mojang.serialization.Codec<RitualAlternative> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.create(inst -> inst.group(
            KillCondition.CODEC.listOf().optionalFieldOf("kills", List.of()).forGetter(RitualAlternative::kills),
            com.mojang.serialization.Codec.BOOL.optionalFieldOf("requires_player_death", false).forGetter(RitualAlternative::requiresPlayerDeath),
            com.mojang.serialization.Codec.BOOL.optionalFieldOf("requires_low_hp", false).forGetter(RitualAlternative::requiresLowHp)
    ).apply(inst, RitualAlternative::new));
}