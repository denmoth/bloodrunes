package com.denmoth.bloodrunes.neoforge.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SpawnSoulParticlesPacket(BlockPos start, BlockPos end) implements CustomPacketPayload {
    public static final Type<SpawnSoulParticlesPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath("bloodrunes", "spawn_soul_particles"));

    public static final StreamCodec<ByteBuf, SpawnSoulParticlesPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SpawnSoulParticlesPacket::start,
            BlockPos.STREAM_CODEC, SpawnSoulParticlesPacket::end,
            SpawnSoulParticlesPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SpawnSoulParticlesPacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                BlockPos start = payload.start();
                BlockPos end = payload.end();
                double dx = end.getX() - start.getX();
                double dy = end.getY() - start.getY();
                double dz = end.getZ() - start.getZ();
                int steps = 20;
                for (int i = 0; i < steps; i++) {
                    double x = start.getX() + 0.5 + (dx * i / steps) + (mc.level.getRandom().nextFloat() - 0.5) * 0.5;
                    double y = start.getY() + 0.5 + (dy * i / steps) + (mc.level.getRandom().nextFloat() - 0.5) * 0.5;
                    double z = start.getZ() + 0.5 + (dz * i / steps) + (mc.level.getRandom().nextFloat() - 0.5) * 0.5;
                    mc.level.addParticle(ParticleTypes.SCULK_SOUL, x, y, z, 0, 0.05, 0);
                }
            }
        });
    }
}
