package com.denmoth.bloodrunes.neoforge.client.jei;

import com.denmoth.bloodrunes.BloodRunes;
import com.denmoth.bloodrunes.neoforge.recipe.RitualRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import com.denmoth.bloodrunes.neoforge.setup.ModBlocks;

import java.util.List;

@JeiPlugin
public class BloodRunesJEIPlugin implements IModPlugin {
    public static final mezz.jei.api.recipe.RecipeType<RitualRecipe> RITUAL_RECIPE_TYPE =
        mezz.jei.api.recipe.RecipeType.create("bloodrunes", "ritual", RitualRecipe.class);
    
    public static final mezz.jei.api.recipe.RecipeType<RitualRecipe> RISTUBLOT_RECIPE_TYPE =
        mezz.jei.api.recipe.RecipeType.create("bloodrunes", "ristublot", RitualRecipe.class);

    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(BloodRunes.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
            new RitualRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            new RistublotRecipeCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<RitualRecipe> recipes = new java.util.ArrayList<>();
        // Common variables
        String[] runes = {"uruz_rune", "fehu_rune", "berkano_rune", "hagalaz_rune", "wunjo_rune", "dagaz_rune"};
        java.util.Map<String, net.minecraft.world.item.Item> runeItems = new java.util.HashMap<>();
        runeItems.put("uruz_rune", com.denmoth.bloodrunes.neoforge.setup.ModItems.URUZ_RUNE.get());
        runeItems.put("fehu_rune", com.denmoth.bloodrunes.neoforge.setup.ModItems.FEHU_RUNE.get());
        runeItems.put("berkano_rune", com.denmoth.bloodrunes.neoforge.setup.ModItems.BERKANO_RUNE.get());
        runeItems.put("hagalaz_rune", com.denmoth.bloodrunes.neoforge.setup.ModItems.HAGALAZ_RUNE.get());
        runeItems.put("wunjo_rune", com.denmoth.bloodrunes.neoforge.setup.ModItems.WUNJO_RUNE.get());
        runeItems.put("dagaz_rune", com.denmoth.bloodrunes.neoforge.setup.ModItems.DAGAZ_RUNE.get());

        // Add Ritual Recipes (manually loaded)
        for (String runeName : runes) {
            net.minecraft.world.item.Item runeItem = runeItems.get(runeName);
            RitualRecipe ritual = new RitualRecipe(
                net.minecraft.world.item.crafting.Ingredient.of(com.denmoth.bloodrunes.neoforge.setup.ModItems.BLANK_RUNE.get()),
                new net.minecraft.world.item.ItemStack(runeItem),
                8.0,
                600,
                java.util.List.of(
                    new com.denmoth.bloodrunes.neoforge.recipe.condition.KillCondition(
                        new com.denmoth.bloodrunes.neoforge.recipe.EntityIngredient(
                            java.util.Optional.empty(),
                            net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.fromNamespaceAndPath("minecraft", "ravager")).map(net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE::wrapAsHolder)
                        ),
                        3,
                        java.util.Optional.empty()
                    )
                )
            );
            recipes.add(ritual);
        }
        
        registration.addRecipes(RITUAL_RECIPE_TYPE, recipes);

        List<RitualRecipe> ristublotRecipes = new java.util.ArrayList<>();

        // Add Ristublot (Ritual of Application) recipes for all valid tools/armor
        java.util.List<net.minecraft.world.item.ItemStack> validItems = new java.util.ArrayList<>();
        for (net.minecraft.world.item.Item item : net.minecraft.core.registries.BuiltInRegistries.ITEM) {
            net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(item);
            if (com.denmoth.bloodrunes.neoforge.block.AltarBlockEntity.isRuneCompatible(stack)) {
                validItems.add(stack);
            }
        }

        for (String runeName : runes) {
            net.minecraft.world.item.Item runeItem = runeItems.get(runeName);
            
            net.minecraft.world.item.ItemStack dummyResult = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND_SWORD);
            dummyResult.set(com.denmoth.bloodrunes.neoforge.setup.ModDataComponents.RUNE_DATA.get(), 
                new com.denmoth.bloodrunes.neoforge.setup.ModDataComponents.RuneData("bloodrunes:" + runeName, 0L));
            
            RitualRecipe ristublot = new RitualRecipe(
                net.minecraft.world.item.crafting.Ingredient.of(validItems.stream().map(net.minecraft.world.item.ItemStack::getItem).toArray(net.minecraft.world.level.ItemLike[]::new)),
                dummyResult,
                0.0,
                200,
                java.util.List.of(
                    new com.denmoth.bloodrunes.neoforge.recipe.condition.SacrificeCondition(net.minecraft.world.item.crafting.Ingredient.of(net.minecraft.world.item.Items.AMETHYST_SHARD), 4),
                    new com.denmoth.bloodrunes.neoforge.recipe.condition.SacrificeCondition(net.minecraft.world.item.crafting.Ingredient.of(net.minecraft.world.item.Items.ECHO_SHARD), 4),
                    new com.denmoth.bloodrunes.neoforge.recipe.condition.SacrificeCondition(net.minecraft.world.item.crafting.Ingredient.of(runeItem), 1)
                )
            );
            ristublotRecipes.add(ristublot);
        }

        registration.addRecipes(RISTUBLOT_RECIPE_TYPE, ristublotRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new net.minecraft.world.item.ItemStack(ModBlocks.ALTAR_ITEM.get()), RITUAL_RECIPE_TYPE, RISTUBLOT_RECIPE_TYPE);
    }
}
