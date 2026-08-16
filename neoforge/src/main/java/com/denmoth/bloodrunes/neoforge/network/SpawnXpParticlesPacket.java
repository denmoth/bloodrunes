package com.denmoth.bloodrunes.neoforge.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SpawnXpParticlesPacket(BlockPos start, BlockPos end) implements CustomPacketPayload {
    public static final Type<SpawnXpParticlesPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath("bloodrunes", "spawn_xp_particles"));

    public static final StreamCodec<ByteBuf, SpawnXpParticlesPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SpawnXpParticlesPacket::start,
            BlockPos.STREAM_CODEC, SpawnXpParticlesPacket::end,
            SpawnXpParticlesPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SpawnXpParticlesPacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                BlockPos start = payload.start();
                BlockPos end = payload.end();
                double dx = end.getX() - start.getX();
                double dy = end.getY() - start.getY();
                double dz = end.getZ() - start.getZ();
                int steps = 30;
                for (int i = 0; i < steps; i++) {
                    double x = start.getX() + 0.5 + (dx * i / steps) + (mc.level.getRandom().nextFloat() - 0.5) * 1.5;
                    double y = start.getY() + 1.0 + (dy * i / steps) + (mc.level.getRandom().nextFloat() - 0.5) * 1.5;
                    double z = start.getZ() + 0.5 + (dz * i / steps) + (mc.level.getRandom().nextFloat() - 0.5) * 1.5;
                    mc.level.addParticle(ParticleTypes.HAPPY_VILLAGER, x, y, z, 0, 0, 0);
                    mc.level.addParticle(ParticleTypes.ENCHANT, x, y, z, dx * 0.05, dy * 0.05, dz * 0.05);
                }
            }
        });
    }
}
