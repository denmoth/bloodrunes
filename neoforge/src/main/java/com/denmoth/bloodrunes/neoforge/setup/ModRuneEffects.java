package com.denmoth.bloodrunes.neoforge.setup;

import com.denmoth.bloodrunes.neoforge.api.RuneRegistry;
import com.denmoth.bloodrunes.neoforge.rune.UruzRuneEffect;

public class ModRuneEffects {
    
    public static void init() {
        RuneRegistry.register(new UruzRuneEffect());
        // Future runes from GDD can be registered here:
        // RuneRegistry.register(new AlgizRuneEffect());
        // RuneRegistry.register(new ThurisazRuneEffect());
        // ...
    }
}
