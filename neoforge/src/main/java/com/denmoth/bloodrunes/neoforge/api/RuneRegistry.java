package com.denmoth.bloodrunes.neoforge.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RuneRegistry {
    private static final Map<String, IRuneEffect> RUNES = new HashMap<>();

    public static void register(IRuneEffect effect) {
        String id = effect.getRuneId().startsWith("bloodrunes:") ? effect.getRuneId().substring(11) : effect.getRuneId();
        RUNES.put(id, effect);
    }

    public static Optional<IRuneEffect> get(String runeId) {
        if (runeId == null) return Optional.empty();
        String clean = runeId.startsWith("bloodrunes:") ? runeId.substring(11) : runeId;
        return Optional.ofNullable(RUNES.get(clean));
    }

    public static boolean has(String runeId) {
        if (runeId == null) return false;
        String clean = runeId.startsWith("bloodrunes:") ? runeId.substring(11) : runeId;
        return RUNES.containsKey(clean);
    }

    public static Map<String, IRuneEffect> getAll() {
        return Collections.unmodifiableMap(RUNES);
    }
}
