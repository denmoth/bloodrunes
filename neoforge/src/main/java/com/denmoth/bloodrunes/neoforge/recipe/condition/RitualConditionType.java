package com.denmoth.bloodrunes.neoforge.recipe.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import java.util.Map;
import java.util.HashMap;

public record RitualConditionType<T extends RitualCondition>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
    
    private static final Map<String, RitualConditionType<?>> TYPES = new HashMap<>();
    private static final Map<RitualConditionType<?>, String> IDS = new HashMap<>();

    public static final Codec<RitualConditionType<?>> CODEC = Codec.STRING.xmap(
            RitualConditionType::getById,
            RitualConditionType::getId
    );

    public static <T extends RitualCondition> RitualConditionType<T> register(String id, MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        RitualConditionType<T> type = new RitualConditionType<>(codec, streamCodec);
        TYPES.put(id, type);
        IDS.put(type, id);
        return type;
    }

    public static RitualConditionType<?> getById(String id) {
        return TYPES.get(id);
    }

    public static String getId(RitualConditionType<?> type) {
        return IDS.get(type);
    }
}
