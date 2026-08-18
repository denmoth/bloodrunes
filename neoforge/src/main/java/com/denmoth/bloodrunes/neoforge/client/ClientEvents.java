package com.denmoth.bloodrunes.neoforge.client;

import com.denmoth.bloodrunes.BloodRunes;
import com.denmoth.bloodrunes.neoforge.setup.ModBlockEntities;
import com.denmoth.bloodrunes.neoforge.setup.ModDataComponents;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = BloodRunes.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {
    
    // Called from BloodRunesNeoForge on the MOD bus (client only)
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.ALTAR_BLOCK_ENTITY.get(), AltarBlockEntityRenderer::new);
    }
}
