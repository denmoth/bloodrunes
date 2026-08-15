package com.denmoth.bloodrunes.neoforge.recipe.condition;

import com.denmoth.bloodrunes.neoforge.recipe.RitualRecipeInput;
import com.denmoth.bloodrunes.neoforge.setup.ModConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.player.Player;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record CovenantCondition(int minPlayers, double radius) implements RitualCondition {
    public static final MapCodec<CovenantCondition> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.INT.optionalFieldOf("min_players", 2).forGetter(CovenantCondition::minPlayers),
            Codec.DOUBLE.optionalFieldOf("radius", 5.0).forGetter(CovenantCondition::radius)
    ).apply(inst, CovenantCondition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CovenantCondition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, CovenantCondition::minPlayers,
            ByteBufCodecs.DOUBLE, CovenantCondition::radius,
            CovenantCondition::new
    );

    @Override
    public RitualConditionType<?> getType() {
        return ModConditionTypes.COVENANT.get();
    }

    @Override
    public boolean matches(RitualRecipeInput input, Level level) {
        if (input.altarPos() == null) return false;
        AABB bounds = new AABB(input.altarPos()).inflate(radius);
        long playerCount = level.getEntitiesOfClass(Player.class, bounds).size();
        if (input.interactedPlayers() == null || input.interactedPlayers().size() < minPlayers) return false;
        return playerCount >= minPlayers;
    }
}
