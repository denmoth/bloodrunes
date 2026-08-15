package com.denmoth.bloodrunes.neoforge.client.jei;

import com.denmoth.bloodrunes.neoforge.recipe.RitualRecipe;
import com.denmoth.bloodrunes.neoforge.setup.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.gui.drawable.IDrawableStatic;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.Identifier;

public class RistublotRecipeCategory implements IRecipeCategory<RitualRecipe> {
    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;
    private final IDrawableStatic slotDrawable;
    private final IGuiHelper guiHelper;

    public static final net.minecraft.resources.Identifier TEXTURE = net.minecraft.resources.Identifier.fromNamespaceAndPath("bloodrunes", "textures/gui/jei_ritual.png");

    public RistublotRecipeCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.background = guiHelper.drawableBuilder(TEXTURE, 0, 0, 176, 85).build();
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.ALTAR_ITEM.get()));
        this.title = Component.translatable("gui.bloodrunes.category.ristublot");
        this.slotDrawable = guiHelper.getSlotDrawable();
    }

    @Override
    public IRecipeType<RitualRecipe> getRecipeType() {
        return BloodRunesJEIPlugin.RISTUBLOT_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return this.title;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return 160;
    }

    @Override
    public int getHeight() {
        return 140;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RitualRecipe recipe, IFocusGroup focuses) {
        // Bottom row for static, clickable slots
        int startX = 10;
        int bottomY = 115;
        
        builder.addSlot(RecipeIngredientRole.INPUT, startX, bottomY)
            .add(recipe.getBaseRune());

        int currentX = startX + 20;
        for (com.denmoth.bloodrunes.neoforge.recipe.condition.RitualCondition condition : recipe.getConditions()) {
            if (condition instanceof com.denmoth.bloodrunes.neoforge.recipe.condition.SacrificeCondition sacrifice) {
                for (int c = 0; c < sacrifice.count() && currentX < 120; c++) {
                    builder.addSlot(RecipeIngredientRole.INPUT, currentX, bottomY)
                        .add(sacrifice.item());
                    currentX += 18;
                }
            } else if (condition instanceof com.denmoth.bloodrunes.neoforge.recipe.condition.KillCondition kill) {
                java.util.List<ItemStack> eggs = new java.util.ArrayList<>();
                if (kill.entity().entityType().isPresent()) {
                    var eggOpt = net.minecraft.world.item.SpawnEggItem.byId(kill.entity().entityType().get().value());
                    if (eggOpt != null && eggOpt.isPresent()) eggs.add(new ItemStack(eggOpt.get().value(), kill.count()));
                } else if (kill.entity().tag().isPresent()) {
                    var tagOpt = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.get(kill.entity().tag().get());
                    if (tagOpt.isPresent()) {
                        for (var holder : tagOpt.get()) {
                            var eggOpt = net.minecraft.world.item.SpawnEggItem.byId(holder.value());
                            if (eggOpt != null && eggOpt.isPresent()) eggs.add(new ItemStack(eggOpt.get().value(), kill.count()));
                        }
                    }
                }
                
                if (!eggs.isEmpty()) {
                    builder.addSlot(RecipeIngredientRole.INPUT, currentX, bottomY)
                        .addItemStacks(eggs);
                    currentX += 18;
                }
            }
        }

        // Output item
        java.util.List<ItemStack> outputs = new java.util.ArrayList<>();
        if (recipe.getResult().has(com.denmoth.bloodrunes.neoforge.setup.ModDataComponents.RUNE_DATA.get())) {
            String runeId = recipe.getResult().get(com.denmoth.bloodrunes.neoforge.setup.ModDataComponents.RUNE_DATA.get()).runeId();
            for (net.minecraft.world.item.Item item : net.minecraft.core.registries.BuiltInRegistries.ITEM) {
                net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(item);
                if (com.denmoth.bloodrunes.neoforge.block.AltarBlockEntity.isRuneCompatible(stack)) {
                    ItemStack out = stack.copy();
                    out.set(com.denmoth.bloodrunes.neoforge.setup.ModDataComponents.RUNE_DATA.get(), new com.denmoth.bloodrunes.neoforge.setup.ModDataComponents.RuneData(runeId, 0L));
                    outputs.add(out);
                }
            }
        } else if (!recipe.getResult().isEmpty()) {
            outputs.add(recipe.getResult());
        }
        
        if (!outputs.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 135, bottomY)
                .addItemStacks(outputs);
        }
    }

    @Override
    public void draw(RitualRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        background.draw(graphics, 0, 0);

        long time = System.currentTimeMillis() / 20; // 20ms per tick

        // Draw Altar at center
        int centerX = 72;
        int centerY = 52;
        int altarY = centerY + (int) (Math.sin(time * 0.08) * 3);
        guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.ALTAR_ITEM.get())).draw(graphics, centerX, altarY);

        // Draw orbiting ingredients using JEI's currently displayed slot items
        int radius = 32;
        java.util.List<ItemStack> itemsToOrbit = new java.util.ArrayList<>();
        java.util.List<mezz.jei.api.gui.ingredient.IRecipeSlotView> slots = recipeSlotsView.getSlotViews();
        // Skip slot 0 (Base Rune) and only take Inputs
        for (int i = 1; i < slots.size(); i++) {
            mezz.jei.api.gui.ingredient.IRecipeSlotView slot = slots.get(i);
            if (slot.getRole() == mezz.jei.api.recipe.RecipeIngredientRole.INPUT) {
                slot.getDisplayedIngredient(mezz.jei.api.constants.VanillaTypes.ITEM_STACK).ifPresent(itemsToOrbit::add);
            }
        }

        int count = itemsToOrbit.size();
        for (int i = 0; i < count; i++) {
            double angle = (time * 0.05) + (2 * Math.PI * i / count);
            int x = centerX + (int) (radius * Math.sin(angle));
            int y = centerY - (int) (radius * Math.cos(angle)) + (int) (Math.sin(time * 0.06 + i * 0.8) * 3);
            guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, itemsToOrbit.get(i)).draw(graphics, x, y);
        }
    }
}
