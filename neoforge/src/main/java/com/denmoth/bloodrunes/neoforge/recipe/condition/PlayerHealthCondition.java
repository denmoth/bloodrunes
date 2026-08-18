package com.denmoth.bloodrunes.neoforge.recipe.condition;

import com.denmoth.bloodrunes.neoforge.recipe.RitualRecipeInput;
import com.denmoth.bloodrunes.neoforge.setup.ModConditionTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Checks if the player who interacted with the altar has low health (<= maxHealthPercentage, e.g. 0.3)
 */
public record PlayerHealthCondition(float maxHealthRatio) implements RitualCondition {
    public static final MapCodec<PlayerHealthCondition> CODEC = RecordCodecBuilder.mapCodec(builder ->
            builder.group(
                    Codec.FLOAT.optionalFieldOf("max_ratio", 0.3f).forGetter(PlayerHealthCondition::maxHealthRatio)
            ).apply(builder, PlayerHealthCondition::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerHealthCondition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, PlayerHealthCondition::maxHealthRatio,
            PlayerHealthCondition::new
    );

    @Override
    public RitualConditionType<?> getType() {
        return ModConditionTypes.PLAYER_HEALTH.get();
    }

    @Override
    public boolean matches(RitualRecipeInput input, Level level) {
        if (input.isLowHp()) return true;
        Player nearest = level.getNearestPlayer(input.altarPos().getX() + 0.5, input.altarPos().getY() + 0.5, input.altarPos().getZ() + 0.5, 8.0, false);
        if (nearest != null) {
            return (nearest.getHealth() / nearest.getMaxHealth()) <= maxHealthRatio;
        }
        return false;
    }
}
