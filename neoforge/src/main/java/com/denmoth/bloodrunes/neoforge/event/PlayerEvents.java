package com.denmoth.bloodrunes.neoforge.event;

import com.denmoth.bloodrunes.BloodRunes;
import com.denmoth.bloodrunes.neoforge.network.SyncLanguagePacket;
import com.denmoth.bloodrunes.neoforge.setup.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = BloodRunes.MOD_ID)
public class PlayerEvents {
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            boolean knowsLanguage = serverPlayer.getData(ModAttachments.VIKING_LANGUAGE);
            PacketDistributor.sendToPlayer(serverPlayer, new SyncLanguagePacket(knowsLanguage));
        }
    }
    
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getEntity().setData(ModAttachments.VIKING_LANGUAGE, event.getOriginal().getData(ModAttachments.VIKING_LANGUAGE));
        }
    }
}
