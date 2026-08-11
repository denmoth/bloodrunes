package com.denmoth.bloodrunes.neoforge.setup;

import com.denmoth.bloodrunes.BloodRunes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(net.minecraft.core.registries.Registries.DATA_COMPONENT_TYPE, BloodRunes.MOD_ID);

    public static final Supplier<DataComponentType<RuneData>> RUNE_DATA = DATA_COMPONENTS.registerComponentType(
            "rune_data",
            builder -> builder
                    .persistent(RuneData.CODEC)
                    .networkSynchronized(RuneData.STREAM_CODEC)
    );

    public record RuneData(String runeId, long cooldownEndTimestamp) {
        public static final Codec<RuneData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("runeId").forGetter(RuneData::runeId),
                Codec.LONG.fieldOf("cooldownEndTimestamp").forGetter(RuneData::cooldownEndTimestamp)
        ).apply(instance, RuneData::new));

        public static final StreamCodec<io.netty.buffer.ByteBuf, RuneData> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, RuneData::runeId,
                ByteBufCodecs.VAR_LONG, RuneData::cooldownEndTimestamp,
                RuneData::new
        );
    }
}
