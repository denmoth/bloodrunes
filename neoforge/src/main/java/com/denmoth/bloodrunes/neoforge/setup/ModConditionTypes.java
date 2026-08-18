package com.denmoth.bloodrunes.neoforge.setup;

import com.denmoth.bloodrunes.neoforge.recipe.condition.*;

import java.util.function.Supplier;

public class ModConditionTypes {
    public static final Supplier<RitualConditionType<KillCondition>> KILL = () -> (RitualConditionType<KillCondition>) RitualConditionType.getById("bloodrunes:kill");
    public static final Supplier<RitualConditionType<SacrificeCondition>> SACRIFICE = () -> (RitualConditionType<SacrificeCondition>) RitualConditionType.getById("bloodrunes:sacrifice");
    public static final Supplier<RitualConditionType<TrialCondition>> TRIAL = () -> (RitualConditionType<TrialCondition>) RitualConditionType.getById("bloodrunes:trial");
    public static final Supplier<RitualConditionType<StormCondition>> STORM = () -> (RitualConditionType<StormCondition>) RitualConditionType.getById("bloodrunes:storm");
    public static final Supplier<RitualConditionType<CovenantCondition>> COVENANT = () -> (RitualConditionType<CovenantCondition>) RitualConditionType.getById("bloodrunes:covenant");
    public static final Supplier<RitualConditionType<EventCondition>> EVENT = () -> (RitualConditionType<EventCondition>) RitualConditionType.getById("bloodrunes:event");
    public static final Supplier<RitualConditionType<MoonPhaseCondition>> MOON_PHASE = () -> (RitualConditionType<MoonPhaseCondition>) RitualConditionType.getById("bloodrunes:moon_phase");
    public static final Supplier<RitualConditionType<TimeOfDayCondition>> TIME_OF_DAY = () -> (RitualConditionType<TimeOfDayCondition>) RitualConditionType.getById("bloodrunes:time_of_day");
    public static final Supplier<RitualConditionType<WeatherCondition>> WEATHER = () -> (RitualConditionType<WeatherCondition>) RitualConditionType.getById("bloodrunes:weather");
    public static final Supplier<RitualConditionType<BiomeCondition>> BIOME = () -> (RitualConditionType<BiomeCondition>) RitualConditionType.getById("bloodrunes:biome");
    public static final Supplier<RitualConditionType<AltitudeCondition>> ALTITUDE = () -> (RitualConditionType<AltitudeCondition>) RitualConditionType.getById("bloodrunes:altitude");
    public static final Supplier<RitualConditionType<DimensionCondition>> DIMENSION = () -> (RitualConditionType<DimensionCondition>) RitualConditionType.getById("bloodrunes:dimension");
    public static final Supplier<RitualConditionType<LightLevelCondition>> LIGHT_LEVEL = () -> (RitualConditionType<LightLevelCondition>) RitualConditionType.getById("bloodrunes:light_level");
    public static final Supplier<RitualConditionType<NearbyBlockCondition>> NEARBY_BLOCK = () -> (RitualConditionType<NearbyBlockCondition>) RitualConditionType.getById("bloodrunes:nearby_block");
    public static final Supplier<RitualConditionType<PlayerHealthCondition>> PLAYER_HEALTH = () -> (RitualConditionType<PlayerHealthCondition>) RitualConditionType.getById("bloodrunes:player_health");

    public static void init() {
        RitualConditionType.register("bloodrunes:kill", KillCondition.CODEC, KillCondition.STREAM_CODEC);
        RitualConditionType.register("bloodrunes:sacrifice", SacrificeCondition.CODEC, SacrificeCondition.STREAM_CODEC);
        RitualConditionType.register("bloodrunes:trial", TrialCondition.CODEC, TrialCondition.STREAM_CODEC);
        RitualConditionType.register("bloodrunes:storm", StormCondition.CODEC, StormCondition.STREAM_CODEC);
        RitualConditionType.register("bloodrunes:covenant", CovenantCondition.CODEC, CovenantCondition.STREAM_CODEC);
        RitualConditionType.register("bloodrunes:event", EventCondition.CODEC, EventCondition.STREAM_CODEC);
        RitualConditionType.register("bloodrunes:moon_phase", MoonPhaseCondition.CODEC, MoonPhaseCondition.STREAM_CODEC);
        RitualConditionType.register("bloodrunes:time_of_day", TimeOfDayCondition.CODEC, TimeOfDayCondition.STREAM_CODEC);
        RitualConditionType.register("bloodrunes:weather", WeatherCondition.CODEC, WeatherCondition.STREAM_CODEC);
        RitualConditionType.register("bloodrunes:biome", BiomeCondition.CODEC, BiomeCondition.STREAM_CODEC);
        RitualConditionType.register("bloodrunes:altitude", AltitudeCondition.CODEC, AltitudeCondition.STREAM_CODEC);
        RitualConditionType.register("bloodrunes:dimension", DimensionCondition.CODEC, DimensionCondition.STREAM_CODEC);
        RitualConditionType.register("bloodrunes:light_level", LightLevelCondition.CODEC, LightLevelCondition.STREAM_CODEC);
        RitualConditionType.register("bloodrunes:nearby_block", NearbyBlockCondition.CODEC, NearbyBlockCondition.STREAM_CODEC);
        RitualConditionType.register("bloodrunes:player_health", PlayerHealthCondition.CODEC, PlayerHealthCondition.STREAM_CODEC);
    }
}
