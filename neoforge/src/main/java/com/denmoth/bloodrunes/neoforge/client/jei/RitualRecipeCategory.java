package com.denmoth.bloodrunes.neoforge.client.jei;

import com.denmoth.bloodrunes.neoforge.recipe.RitualRecipe;
import com.denmoth.bloodrunes.neoforge.setup.ModBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * JEI category: Blood Ritual (Blank Rune → Crafted Rune).
 *
 * <pre>
 * ┌───────────────────────────────────────────┐
 * │  [Base Rune]  ──→──  [Result Rune]        │  y=5  (top row)
 * ├───────────────────────────────────────────┤
 * │  [Sac ×N] [Kill egg] …                    │  y=30 (item condition slots)
 * ├ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┤
 * │  ☾ Full Moon                              │  y=62 (text conditions via ITextWidget)
 * │  ⚡ Thunder                               │
 * └───────────────────────────────────────────┘
 * </pre>
 */
public class RitualRecipeCategory extends BaseRitualCategory {

    public RitualRecipeCategory(IGuiHelper guiHelper) {
        super(guiHelper, new ItemStack(ModBlocks.ALTAR_ITEM.get()));
    }

    @Override
    public IRecipeType<RitualRecipe> getRecipeType() {
        return BloodRunesJEIPlugin.RITUAL_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.bloodrunes.altar");
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RitualRecipe recipe, IFocusGroup focuses) {
        // Top row: base rune input
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X, TOP_Y)
               .add(recipe.getBaseRune());

        // Top row: result output
        if (!recipe.getResult().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, TOP_Y)
                   .add(recipe.getResult());
        }

        // Item condition slots (sacrifice + kill)
        addItemConditionSlots(builder, recipe.getConditions(), INPUT_X, SLOTS_Y);
    }

    // Arrow X is right after the single input slot
    @Override
    protected int arrowX() {
        return INPUT_X + SLOT_SIZE + 4;
    }
}
