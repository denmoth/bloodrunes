package com.denmoth.bloodrunes.neoforge.recipe.condition;

import com.denmoth.bloodrunes.neoforge.recipe.RitualRecipeInput;
import com.denmoth.bloodrunes.neoforge.setup.ModConditionTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

/**
 * Matches a biome by ResourceLocation or by Biome Tag (#minecraft:is_mountain, #minecraft:is_ocean, etc.)
 */
public record BiomeCondition(String biomeOrTag) implements RitualCondition {
    public static final MapCodec<BiomeCondition> CODEC = RecordCodecBuilder.mapCodec(builder ->
            builder.group(
                    Codec.STRING.fieldOf("biome").forGetter(BiomeCondition::biomeOrTag)
            ).apply(builder, BiomeCondition::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, BiomeCondition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, BiomeCondition::biomeOrTag,
            BiomeCondition::new
    );

    @Override
    public RitualConditionType<?> getType() {
        return ModConditionTypes.BIOME.get();
    }

    @Override
    public boolean matches(RitualRecipeInput input, Level level) {
        Holder<Biome> biomeHolder = level.getBiome(input.altarPos());
        if (biomeOrTag.startsWith("#")) {
            Identifier tagId = Identifier.parse(biomeOrTag.substring(1));
            TagKey<Biome> tagKey = TagKey.create(Registries.BIOME, tagId);
            return biomeHolder.is(tagKey);
        } else {
            Identifier biomeId = Identifier.parse(biomeOrTag);
            return biomeHolder.is(biomeId);
        }
    }
}
