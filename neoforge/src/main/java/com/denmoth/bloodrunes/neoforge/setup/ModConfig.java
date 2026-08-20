package com.denmoth.bloodrunes.neoforge.setup;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ModConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // --- COMMON ---

    public static final ModConfigSpec.IntValue ALTAR_SEARCH_RADIUS = BUILDER
            .comment("Max radius (in blocks) around an altar in which mob deaths are counted")
            .defineInRange("altar_search_radius", 64, 8, 256);

    public static final ModConfigSpec.IntValue MOB_DEATH_CACHE_SIZE = BUILDER
            .comment("Max kills cached per altar per ritual")
            .defineInRange("mob_death_cache_size", 64, 16, 512);

    public static final ModConfigSpec.IntValue RITUAL_MAX_DURATION_TICKS = BUILDER
            .comment("Maximum allowed ritual duration in ticks (200 ticks = 10 seconds, 24000 = 20 minutes)")
            .defineInRange("ritual_max_duration_ticks", 24000, 200, 72000);

    public static final ModConfigSpec.DoubleValue ALTAR_PARTICLE_DENSITY = BUILDER
            .comment("Particle count multiplier for altar effects (0.0 = off, 1.0 = normal, 3.0 = dense)")
            .defineInRange("altar_particle_density", 1.0, 0.0, 3.0);

    public static final ModConfigSpec.BooleanValue ENABLE_RUNE_GLOW = BUILDER
            .comment("Whether runed items have enchantment glint")
            .define("enable_rune_glow", true);

    public static final ModConfigSpec.BooleanValue ENABLE_BLOOD_PARTICLES = BUILDER
            .comment("Master toggle for blood particle effects")
            .define("enable_blood_particles", true);

    public static final ModConfigSpec.DoubleValue COVENANT_DEFAULT_RADIUS = BUILDER
            .comment("Default radius (in blocks) for coven rituals")
            .defineInRange("covenant_default_radius", 16.0, 2.0, 64.0);

    public static final ModConfigSpec SPEC = BUILDER.build();
}
