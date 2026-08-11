package com.denmoth.bloodrunes.neoforge.network;

import com.denmoth.bloodrunes.BloodRunes;
import com.denmoth.bloodrunes.neoforge.setup.ModAttachments;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModMessages {
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(BloodRunes.MOD_ID).versioned("1.0");
        registrar.playToClient(
            SyncLanguagePacket.TYPE,
            SyncLanguagePacket.STREAM_CODEC,
            (payload, context) -> {
                context.enqueueWork(() -> {
                    if (Minecraft.getInstance().player != null) {
                        Minecraft.getInstance().player.setData(ModAttachments.VIKING_LANGUAGE, payload.knowsLanguage());
                    }
                });
            }
        );
        registrar.playToClient(
            SpawnSoulParticlesPacket.TYPE,
            SpawnSoulParticlesPacket.STREAM_CODEC,
            SpawnSoulParticlesPacket::handle
        );
    }
}
