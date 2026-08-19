package com.denmoth.bloodrunes.neoforge.client.jei;

import com.denmoth.bloodrunes.neoforge.recipe.RitualRecipe;
import com.denmoth.bloodrunes.neoforge.recipe.condition.*;
import com.denmoth.bloodrunes.neoforge.setup.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for BloodRunes JEI categories.
 *
 * Layout (160 × 100):
 * <pre>
 *   y= 5: [Input Slot(s)]  ──→──  [Output Slot]       (top row: inputs + arrow + output)
 *   y=30: [Sacrifice/Kill slots, wrapping in rows of 7]  (item conditions)
 *   y=60: Text lines for environmental conditions       (text conditions via ITextWidget)
 * </pre>
 */
public abstract class BaseRitualCategory implements IRecipeCategory<RitualRecipe> {

    // ── Geometry ──────────────────────────────────────────────────────────────
    protected static final int WIDTH         = 160;
    protected static final int HEIGHT        = 100;
    protected static final int INPUT_X       = 5;
    protected static final int TOP_Y         = 5;
    protected static final int OUTPUT_X      = 135;
    protected static final int SLOTS_Y       = 30;
    protected static final int SLOT_SIZE     = 18;
    protected static final int SLOTS_PER_ROW = 7;
    protected static final int TEXT_BASE_Y   = 62;
    protected static final int LINE_H        = 10;
    protected static final int TEXT_COLOR    = 0x777777;

    // ── Drawables ─────────────────────────────────────────────────────────────
    private final IDrawable background;
    private final IDrawable icon;
    protected final IDrawable arrow;
    protected final IGuiHelper guiHelper;

    protected BaseRitualCategory(IGuiHelper guiHelper, ItemStack iconStack) {
        this.guiHelper  = guiHelper;
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        this.icon       = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, iconStack);
        // JEI built-in arrow widget — drawn via createRecipeExtras, so here we use a
        // simple blank for the fallback draw() path
        this.arrow      = guiHelper.createBlankDrawable(24, 17);
    }

    // ── IRecipeCategory ───────────────────────────────────────────────────────

    public IDrawable getBackground()           { return background; }
    @Override public IDrawable getIcon()       { return icon; }
    @Override public int getWidth()            { return WIDTH; }
    @Override public int getHeight()           { return HEIGHT; }

    // ── Recipe extras: arrow + text conditions ────────────────────────────────

    /**
     * Adds the JEI arrow widget and all text-based condition lines.
     * Subclasses call super and add their own arrow x-offset.
     */
    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RitualRecipe recipe, IFocusGroup focuses) {
        // Arrow
        builder.addRecipeArrow().setPosition(arrowX(), TOP_Y);

        // Text condition lines
        List<FormattedText> lines = buildConditionLines(recipe);
        if (!lines.isEmpty()) {
            builder.addText(lines, WIDTH - 8, HEIGHT - TEXT_BASE_Y - 2)
                   .setColor(TEXT_COLOR)
                   .setPosition(4, TEXT_BASE_Y);
        }
    }

    /** Subclasses override to position the arrow correctly relative to their inputs. */
    protected int arrowX() {
        return INPUT_X + SLOT_SIZE + 4;
    }

    // ── Shared layout helpers ─────────────────────────────────────────────────

    /**
     * Adds sacrifice/kill slots to the builder.
     * Returns the Y position AFTER the last used row.
     */
    protected int addItemConditionSlots(IRecipeLayoutBuilder builder,
                                        List<RitualCondition> conditions,
                                        int startX, int startY) {
        int x = startX;
        int y = startY;
        int col = 0;

        for (RitualCondition cond : conditions) {
            if (cond instanceof SacrificeCondition sacrifice) {
                // Resolve items from Ingredient (MC 26.2: ingredient.items() → Stream<Holder<Item>>)
                List<ItemStack> items = sacrifice.item().items()
                        .map(h -> new ItemStack(h.value()))
                        .toList();
                if (items.isEmpty()) continue;

                int count = Math.min(sacrifice.count(), SLOTS_PER_ROW - col);
                for (int i = 0; i < sacrifice.count(); i++) {
                    if (col >= SLOTS_PER_ROW) { col = 0; x = startX; y += SLOT_SIZE; }
                    builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                           .addItemStacks(items);
                    x += SLOT_SIZE; col++;
                }

            } else if (cond instanceof KillCondition kill) {
                List<ItemStack> eggs = resolveKillEggs(kill);
                if (eggs.isEmpty()) continue;
                if (col >= SLOTS_PER_ROW) { col = 0; x = startX; y += SLOT_SIZE; }
                builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                       .addItemStacks(eggs);
                x += SLOT_SIZE; col++;
            }
        }

        return (col == 0) ? y : y + SLOT_SIZE;
    }

    protected static List<ItemStack> resolveKillEggs(KillCondition kill) {
        List<ItemStack> eggs = new ArrayList<>();
        if (kill.entity().entityType().isPresent()) {
            var eggOpt = SpawnEggItem.byId(kill.entity().entityType().get().value());
            if (eggOpt != null && eggOpt.isPresent())
                eggs.add(new ItemStack(eggOpt.get().value()));
        } else if (kill.entity().tag().isPresent()) {
            var tagSet = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.get(kill.entity().tag().get());
            if (tagSet.isPresent()) {
                for (var holder : tagSet.get()) {
                    var eggOpt = SpawnEggItem.byId(holder.value());
                    if (eggOpt != null && eggOpt.isPresent())
                        eggs.add(new ItemStack(eggOpt.get().value()));
                }
            }
        }
        return eggs;
    }

    // ── Condition text lines ──────────────────────────────────────────────────

    private static List<FormattedText> buildConditionLines(RitualRecipe recipe) {
        List<FormattedText> lines = new ArrayList<>();
        for (RitualCondition cond : recipe.getConditions()) {
            String line = conditionLine(cond);
            if (line != null) lines.add(Component.literal(line));
        }
        return lines;
    }

    private static String conditionLine(RitualCondition cond) {
        // Item-based conditions are shown as slots — skip them here
        if (cond instanceof SacrificeCondition || cond instanceof KillCondition) return null;

        if (cond instanceof MoonPhaseCondition moon) {
            String[] phases = {
                "Full Moon", "Waning Gibbous", "Last Quarter", "Waning Crescent",
                "New Moon",  "Waxing Crescent", "First Quarter", "Waxing Gibbous"
            };
            int p = moon.phase();
            String name = (p >= 0 && p < phases.length) ? phases[p] : "Phase " + p;
            return "\u263E " + name;
        }
        if (cond instanceof TimeOfDayCondition time) {
            return "\u2600 Time: " + ticksToHHMM(time.minTime()) + " - " + ticksToHHMM(time.maxTime());
        }
        if (cond instanceof WeatherCondition weather) {
            return switch (weather.weather().toLowerCase()) {
                case "thunder" -> "\u26A1 Thunder";
                case "rain"    -> "\u2614 Rain";
                default        -> "\u2600 Clear";
            };
        }
        if (cond instanceof StormCondition) {
            return "\u26C8 Storm";
        }
        if (cond instanceof BiomeCondition biome) {
            String b = biome.biomeOrTag();
            String short_ = b.contains(":") ? b.substring(b.lastIndexOf(':') + 1).replace('_', ' ') : b;
            String prefix = b.startsWith("#") ? "Tag: " : "";
            return "\uD83C\uDF32 " + prefix + capitalize(short_);
        }
        if (cond instanceof AltitudeCondition alt) {
            if (alt.minY() > Integer.MIN_VALUE + 1 && alt.maxY() < Integer.MAX_VALUE - 1) {
                return "\u2195 Y: " + alt.minY() + " - " + alt.maxY();
            } else if (alt.minY() > Integer.MIN_VALUE + 1) {
                return "\u2195 Y \u2265 " + alt.minY();
            } else {
                return "\u2195 Y \u2264 " + alt.maxY();
            }
        }
        if (cond instanceof DimensionCondition dim) {
            String id = dim.dimensionId();
            String short_ = id.contains(":") ? id.substring(id.lastIndexOf(':') + 1).replace('_', ' ') : id;
            return "\uD83C\uDF10 " + capitalize(short_);
        }
        if (cond instanceof LightLevelCondition light) {
            return "\u25D0 Light: " + light.minLight() + " - " + light.maxLight();
        }
        if (cond instanceof NearbyBlockCondition block) {
            String id = block.blockId();
            String short_ = id.contains(":") ? id.substring(id.lastIndexOf(':') + 1).replace('_', ' ') : id;
            return "\u25A0 Near: " + capitalize(short_) + " (r=" + block.radius() + ")";
        }
        if (cond instanceof PlayerHealthCondition hp) {
            int pct = Math.round(hp.maxHealthRatio() * 100);
            return "\u2764 HP \u2264 " + pct + "%";
        }
        if (cond instanceof TrialCondition trial) {
            List<String> parts = new ArrayList<>();
            trial.requiresLowHp().ifPresent(v -> { if (v) parts.add("Low HP"); });
            trial.requiresPlayerDeath().ifPresent(v -> { if (v) parts.add("Near Death"); });
            return "\u2694 Trial: " + (parts.isEmpty() ? "Active" : String.join(", ", parts));
        }
        if (cond instanceof CovenantCondition cov) {
            return "\uD83D\uDC65 Coven: " + cov.minPlayers() + "+ players (r=" + (int) cov.radius() + ")";
        }
        if (cond instanceof EventCondition evt) {
            return "\uD83D\uDCDC Event: " + evt.eventId();
        }
        return "\u2022 " + cond.getClass().getSimpleName().replace("Condition", "");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    protected static String ticksToHHMM(long ticks) {
        int totalMins = (int) ((ticks + 6000) % 24000 * 60 / 1000);
        return String.format("%02d:%02d", totalMins / 60, totalMins % 60);
    }

    protected static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
