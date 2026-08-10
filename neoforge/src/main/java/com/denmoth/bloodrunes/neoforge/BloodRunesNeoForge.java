package com.denmoth.bloodrunes.neoforge;

import com.denmoth.bloodrunes.BloodRunes;
import net.neoforged.fml.common.Mod;

@Mod(BloodRunes.MOD_ID)
public class BloodRunesNeoForge {
    public BloodRunesNeoForge(net.neoforged.bus.api.IEventBus modEventBus) {
        com.denmoth.bloodrunes.neoforge.setup.ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        BloodRunes.init();
    }
}
