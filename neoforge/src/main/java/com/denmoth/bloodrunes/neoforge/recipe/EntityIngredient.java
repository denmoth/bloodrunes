package com.denmoth.bloodrunes.neoforge.recipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.core.Holder;
import java.util.Optional;

public record EntityIngredient(Optional<TagKey<EntityType<?>>> tag, Optional<Holder<EntityType<?>>> entityType) {
    public static final com.mojang.serialization.Codec<EntityIngredient> CODEC = com.mojang.serialization.Codec.STRING.flatXmap(
        str -> {
            if (str.startsWith("#")) {
                return com.mojang.serialization.DataResult.success(new EntityIngredient(Optional.of(TagKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, ResourceLocation.parse(str.substring(1)))), Optional.empty()));
            } else {
                return BuiltInRegistries.ENTITY_TYPE.getOptional(ResourceLocation.parse(str))
                    .map(e -> com.mojang.serialization.DataResult.success(new EntityIngredient(Optional.empty(), Optional.of(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(e)))))
                    .orElseGet(() -> com.mojang.serialization.DataResult.error(() -> "Unknown entity type: " + str));
            }
        },
        ing -> {
            if (ing.tag().isPresent()) return com.mojang.serialization.DataResult.success("#" + ing.tag().get().location().toString());
            if (ing.entityType().isPresent()) return com.mojang.serialization.DataResult.success(ing.entityType().get().unwrapKey().get().location().toString());
            return com.mojang.serialization.DataResult.error(() -> "Invalid EntityIngredient");
        }
    );

    public boolean test(EntityType<?> type) {
        if (tag.isPresent()) return type.is(tag.get());
        if (entityType.isPresent()) return type == entityType.get().value();
        return false;
    }
}