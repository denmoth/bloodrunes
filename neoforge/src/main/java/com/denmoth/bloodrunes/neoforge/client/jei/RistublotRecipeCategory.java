package com.denmoth.bloodrunes.neoforge.client.jei;

import com.denmoth.bloodrunes.neoforge.block.AltarBlockEntity;
import com.denmoth.bloodrunes.neoforge.recipe.RitualRecipe;
import com.denmoth.bloodrunes.neoforge.setup.ModBlocks;
import com.denmoth.bloodrunes.neoforge.setup.ModDataComponents;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * JEI category: Ristublot (applying a rune to a weapon/armour piece).
 *
 * <pre>
 * ┌───────────────────────────────────────────┐
 * │  [Tool] [Rune]  ──→──  [Runed Tool]       │  y=5  (top row)
 * ├───────────────────────────────────────────┤
 * │  [Sac item] [Sac item] …                  │  y=30 (sacrifice condition slots)
 * ├ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┤
 * │  ❤ HP ≤ 30%   👥 2+ players              │  y=62 (text conditions via ITextWidget)
 * └───────────────────────────────────────────┘
 * </pre>
 *
 * Uses a curated sample list for the output slot instead of iterating the entire
 * item registry (which was very expensive).
 */
public class RistublotRecipeCategory extends BaseRitualCategory {

    /** Curated representative items shown in the output slot cycle. */
    private static final List<Item> SAMPLE_ITEMS = List.of(
            net.minecraft.world.item.Items.DIAMOND_SWORD,
            net.minecraft.world.item.Items.DIAMOND_AXE,
            net.minecraft.world.item.Items.BOW,
            net.minecraft.world.item.Items.CROSSBOW,
            net.minecraft.world.item.Items.DIAMOND_HELMET,
            net.minecraft.world.item.Items.DIAMOND_CHESTPLATE,
            net.minecraft.world.item.Items.DIAMOND_LEGGINGS,
            net.minecraft.world.item.Items.DIAMOND_BOOTS,
            net.minecraft.world.item.Items.NETHERITE_SWORD,
            net.minecraft.world.item.Items.NETHERITE_AXE,
            net.minecraft.world.item.Items.NETHERITE_HELMET,
            net.minecraft.world.item.Items.NETHERITE_CHESTPLATE
    );

    public RistublotRecipeCategory(IGuiHelper guiHelper) {
        super(guiHelper, new ItemStack(ModBlocks.ALTAR_ITEM.get()));
    }

    @Override
    public IRecipeType<RitualRecipe> getRecipeType() {
        return BloodRunesJEIPlugin.RISTUBLOT_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.bloodrunes.category.ristublot");
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RitualRecipe recipe, IFocusGroup focuses) {
        // Top row slot 1: compatible tool/armor (cycling through sample items)
        List<ItemStack> inputItems = buildInputSample();
        if (!inputItems.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X, TOP_Y)
                   .addItemStacks(inputItems);
        }

        // Top row slot 2: the rune ingredient
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X + SLOT_SIZE, TOP_Y)
               .add(recipe.getBaseRune());

        // Output: same items but with rune data component applied
        List<ItemStack> outputs = buildOutputSample(recipe);
        if (!outputs.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, TOP_Y)
                   .addItemStacks(outputs);
        }

        // Sacrifice / kill condition slots
        addItemConditionSlots(builder, recipe.getConditions(), INPUT_X, SLOTS_Y);
    }

    /** Arrow positioned after two input slots. */
    @Override
    protected int arrowX() {
        return INPUT_X + SLOT_SIZE * 2 + 4;
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private static List<ItemStack> buildInputSample() {
        List<ItemStack> result = new ArrayList<>();
        for (Item item : SAMPLE_ITEMS) {
            ItemStack s = new ItemStack(item);
            if (AltarBlockEntity.isRuneCompatible(s)) result.add(s);
        }
        return result;
    }

    private static List<ItemStack> buildOutputSample(RitualRecipe recipe) {
        if (!recipe.getResult().has(ModDataComponents.RUNE_DATA.get())) {
            return recipe.getResult().isEmpty() ? List.of() : List.of(recipe.getResult().copy());
        }
        ModDataComponents.RuneData runeData = recipe.getResult().get(ModDataComponents.RUNE_DATA.get());
        List<ItemStack> outputs = new ArrayList<>();
        for (Item item : SAMPLE_ITEMS) {
            ItemStack base = new ItemStack(item);
            if (AltarBlockEntity.isRuneCompatible(base)) {
                ItemStack out = base.copy();
                out.set(ModDataComponents.RUNE_DATA.get(), runeData);
                outputs.add(out);
            }
        }
        return outputs;
    }
}
