package com.denmoth.bloodrunes.neoforge.setup;

import com.denmoth.bloodrunes.neoforge.recipe.condition.KillCondition;
import com.denmoth.bloodrunes.neoforge.recipe.condition.RitualConditionType;
import com.denmoth.bloodrunes.neoforge.recipe.condition.SacrificeCondition;
import com.denmoth.bloodrunes.neoforge.recipe.condition.TrialCondition;
import com.denmoth.bloodrunes.neoforge.recipe.condition.StormCondition;
import com.denmoth.bloodrunes.neoforge.recipe.condition.CovenantCondition;
import com.denmoth.bloodrunes.neoforge.recipe.condition.EventCondition;

import java.util.function.Supplier;

public class ModConditionTypes {
    public static final Supplier<RitualConditionType<KillCondition>> KILL = () -> (RitualConditionType<KillCondition>) RitualConditionType.getById("bloodrunes:kill");
    public static final Supplier<RitualConditionType<SacrificeCondition>> SACRIFICE = () -> (RitualConditionType<SacrificeCondition>) RitualConditionType.getById("bloodrunes:sacrifice");
    public static final Supplier<RitualConditionType<TrialCondition>> TRIAL = () -> (RitualConditionType<TrialCondition>) RitualConditionType.getById("bloodrunes:trial");
    public static final Supplier<RitualConditionType<StormCondition>> STORM = () -> (RitualConditionType<StormCondition>) RitualConditionType.getById("bloodrunes:storm");
    public static final Supplier<RitualConditionType<CovenantCondition>> COVENANT = () -> (RitualConditionType<CovenantCondition>) RitualConditionType.getById("bloodrunes:covenant");
    public static final Supplier<RitualConditionType<EventCondition>> EVENT = () -> (RitualConditionType<EventCondition>) RitualConditionType.getById("bloodrunes:event");

    public static void init() {
        RitualConditionType.register("bloodrunes:kill", KillCondition.CODEC, KillCondition.STREAM_CODEC);
        RitualConditionType.register("bloodrunes:sacrifice", SacrificeCondition.CODEC, SacrificeCondition.STREAM_CODEC);
        RitualConditionType.register("bloodrunes:trial", TrialCondition.CODEC, TrialCondition.STREAM_CODEC);
        RitualConditionType.register("bloodrunes:storm", StormCondition.CODEC, StormCondition.STREAM_CODEC);
        RitualConditionType.register("bloodrunes:covenant", CovenantCondition.CODEC, CovenantCondition.STREAM_CODEC);
        RitualConditionType.register("bloodrunes:event", EventCondition.CODEC, EventCondition.STREAM_CODEC);
    }
}
