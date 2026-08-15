package com.denmoth.bloodrunes.neoforge.recipe.condition;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import com.denmoth.bloodrunes.neoforge.recipe.RitualRecipeInput;
import net.minecraft.world.level.Level;

public interface RitualCondition {
    
    RitualConditionType<?> getType();
    
    boolean matches(RitualRecipeInput input, Level level);
    
    Codec<RitualCondition> CODEC = RitualConditionType.CODEC.dispatch(RitualCondition::getType, RitualConditionType::codec);
    
    StreamCodec<RegistryFriendlyByteBuf, RitualCondition> STREAM_CODEC = StreamCodec.of(
            (buf, condition) -> {
                buf.writeUtf(RitualConditionType.getId(condition.getType()));
                ((RitualConditionType<RitualCondition>) condition.getType()).streamCodec().encode(buf, condition);
            },
            buf -> {
                String id = buf.readUtf();
                RitualConditionType<?> type = RitualConditionType.getById(id);
                return type.streamCodec().decode(buf);
            }
    );
}
