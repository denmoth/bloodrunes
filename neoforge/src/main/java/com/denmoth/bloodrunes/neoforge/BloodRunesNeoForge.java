package com.denmoth.bloodrunes.neoforge;

import com.denmoth.bloodrunes.BloodRunes;
import net.neoforged.fml.common.Mod;

@Mod(BloodRunes.MOD_ID)
public class BloodRunesNeoForge {
    public BloodRunesNeoForge(net.neoforged.bus.api.IEventBus modEventBus) {
        com.denmoth.bloodrunes.neoforge.setup.ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        com.denmoth.bloodrunes.neoforge.setup.ModItems.ITEMS.register(modEventBus);
        com.denmoth.bloodrunes.neoforge.setup.ModBlocks.BLOCKS.register(modEventBus);
        com.denmoth.bloodrunes.neoforge.setup.ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        com.denmoth.bloodrunes.neoforge.setup.ModDataComponents.DATA_COMPONENTS.register(modEventBus);
        com.denmoth.bloodrunes.neoforge.setup.ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        com.denmoth.bloodrunes.neoforge.setup.ModRecipes.SERIALIZERS.register(modEventBus);
        com.denmoth.bloodrunes.neoforge.setup.ModRecipes.TYPES.register(modEventBus);

        modEventBus.addListener(com.denmoth.bloodrunes.neoforge.network.ModMessages::register);
        modEventBus.addListener(com.denmoth.bloodrunes.neoforge.client.ClientEvents::onRegisterRenderers);

        com.denmoth.bloodrunes.neoforge.setup.ModConditionTypes.init();
        com.denmoth.bloodrunes.neoforge.setup.ModRuneEffects.init();
        BloodRunes.init();
    }
}
