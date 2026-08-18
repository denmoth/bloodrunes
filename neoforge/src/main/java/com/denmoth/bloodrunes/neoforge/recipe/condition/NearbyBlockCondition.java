package com.denmoth.bloodrunes.neoforge.recipe.condition;

import com.denmoth.bloodrunes.neoforge.recipe.RitualRecipeInput;
import com.denmoth.bloodrunes.neoforge.setup.ModConditionTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/**
 * Checks for a specific block placed within radius of the altar
 * (e.g. block: "minecraft:soul_campfire", radius: 3)
 */
public record NearbyBlockCondition(String blockId, int radius) implements RitualCondition {
    public static final MapCodec<NearbyBlockCondition> CODEC = RecordCodecBuilder.mapCodec(builder ->
            builder.group(
                    Codec.STRING.fieldOf("block").forGetter(NearbyBlockCondition::blockId),
                    Codec.INT.optionalFieldOf("radius", 4).forGetter(NearbyBlockCondition::radius)
            ).apply(builder, NearbyBlockCondition::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, NearbyBlockCondition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, NearbyBlockCondition::blockId,
            ByteBufCodecs.VAR_INT, NearbyBlockCondition::radius,
            NearbyBlockCondition::new
    );

    @Override
    public RitualConditionType<?> getType() {
        return ModConditionTypes.NEARBY_BLOCK.get();
    }

    @Override
    public boolean matches(RitualRecipeInput input, Level level) {
        Block target = BuiltInRegistries.BLOCK.get(Identifier.parse(blockId)).map(net.minecraft.core.Holder.Reference::value).orElse(null);
        if (target == null) return false;
        
        BlockPos center = input.altarPos();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (level.getBlockState(center.offset(x, y, z)).is(target)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
