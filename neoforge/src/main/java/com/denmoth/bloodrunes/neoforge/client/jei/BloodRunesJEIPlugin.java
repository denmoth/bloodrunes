package com.denmoth.bloodrunes.neoforge.client.jei;

import com.denmoth.bloodrunes.BloodRunes;
import com.denmoth.bloodrunes.neoforge.recipe.EntityIngredient;
import com.denmoth.bloodrunes.neoforge.recipe.RitualRecipe;
import com.denmoth.bloodrunes.neoforge.recipe.condition.KillCondition;
import com.denmoth.bloodrunes.neoforge.recipe.condition.SacrificeCondition;
import com.denmoth.bloodrunes.neoforge.setup.ModBlocks;
import com.denmoth.bloodrunes.neoforge.setup.ModDataComponents;
import com.denmoth.bloodrunes.neoforge.setup.ModItems;
import com.denmoth.bloodrunes.neoforge.setup.ModRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@JeiPlugin
public class BloodRunesJEIPlugin implements IModPlugin {

    // ── Recipe types ──────────────────────────────────────────────────────────

    public static final IRecipeType<RitualRecipe> RITUAL_RECIPE_TYPE =
            IRecipeType.create(Identifier.fromNamespaceAndPath(BloodRunes.MOD_ID, "ritual"), RitualRecipe.class);

    public static final IRecipeType<RitualRecipe> RISTUBLOT_RECIPE_TYPE =
            IRecipeType.create(Identifier.fromNamespaceAndPath(BloodRunes.MOD_ID, "ristublot"), RitualRecipe.class);

    // ── Plugin identity ───────────────────────────────────────────────────────

    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(BloodRunes.MOD_ID, "jei_plugin");
    }

    // ── Category registration ─────────────────────────────────────────────────

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new RitualRecipeCategory(gui),
                new RistublotRecipeCategory(gui)
        );
    }

    // ── Recipe registration ───────────────────────────────────────────────────

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // JEI calls this after the recipe manager has been populated.
        // Use IRecipeRegistration.getIngredientManager() for ingredient lookups.
        // For recipes, we query the Minecraft client's recipe manager if available,
        // falling back to hardcoded demo entries for dev environments.
        var mc = net.minecraft.client.Minecraft.getInstance();

        List<RitualRecipe> allRitualRecipes = List.of();

        // In a running game mc.getConnection() has the RecipeManager from the server
        if (mc.getConnection() != null) {
            // MC 26.2: recipes are available via the level's ServerData / recipe manager
            // Note: RecipeManager is server-side; on client we use a workaround
            // by accessing the server recipe manager through getServer() in singleplayer.
            var server = mc.getSingleplayerServer();
            if (server != null) {
                var rm = server.getRecipeManager();
                @SuppressWarnings("unchecked")
                var type = (net.minecraft.world.item.crafting.RecipeType<RitualRecipe>) ModRecipes.RITUAL_TYPE.get();
                allRitualRecipes = rm.getRecipes().stream()
                        .filter(h -> h.value().getType() == type)
                        .map(h -> (RitualRecipe) h.value())
                        .collect(Collectors.toList());
            }
        }

        // Split Ritual vs Ristublot by presence of RUNE_DATA component on result
        List<RitualRecipe> rituals = allRitualRecipes.stream()
                .filter(r -> !r.getResult().has(ModDataComponents.RUNE_DATA.get()))
                .collect(Collectors.toList());

        List<RitualRecipe> ristublots = allRitualRecipes.stream()
                .filter(r -> r.getResult().has(ModDataComponents.RUNE_DATA.get()))
                .collect(Collectors.toList());

        // Always include fallback entries so the JEI tab is never empty
        if (rituals.isEmpty()) rituals = buildFallbackRituals();
        if (ristublots.isEmpty()) ristublots = buildFallbackRistublots();

        registration.addRecipes(RITUAL_RECIPE_TYPE,    rituals);
        registration.addRecipes(RISTUBLOT_RECIPE_TYPE, ristublots);
    }

    // ── Catalyst registration ─────────────────────────────────────────────────

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(RITUAL_RECIPE_TYPE,    ModBlocks.ALTAR.get());
        registration.addCraftingStation(RISTUBLOT_RECIPE_TYPE, ModBlocks.ALTAR.get());
    }

    // ── Fallback demo recipes ─────────────────────────────────────────────────

    private static List<RitualRecipe> buildFallbackRituals() {
        var runeItems = List.of(
                ModItems.URUZ_RUNE.get(),
                ModItems.FEHU_RUNE.get(),
                ModItems.BERKANO_RUNE.get(),
                ModItems.HAGALAZ_RUNE.get(),
                ModItems.WUNJO_RUNE.get(),
                ModItems.DAGAZ_RUNE.get()
        );
        return runeItems.stream().map(rune -> new RitualRecipe(
                Ingredient.of(ModItems.BLANK_RUNE.get()),
                new ItemStack(rune),
                8.0, 600,
                List.of(new KillCondition(
                        new EntityIngredient(
                                Optional.empty(),
                                BuiltInRegistries.ENTITY_TYPE
                                        .getOptional(Identifier.fromNamespaceAndPath("minecraft", "ravager"))
                                        .map(BuiltInRegistries.ENTITY_TYPE::wrapAsHolder)
                        ),
                        3, Optional.empty()))
        )).collect(Collectors.toList());
    }

    private static List<RitualRecipe> buildFallbackRistublots() {
        var runeItems = List.of(
                ModItems.URUZ_RUNE.get(),
                ModItems.FEHU_RUNE.get(),
                ModItems.BERKANO_RUNE.get()
        );
        return runeItems.stream().map(rune -> {
            String runeId = BloodRunes.MOD_ID + ":" + BuiltInRegistries.ITEM.getKey(rune).getPath();
            ItemStack result = new ItemStack(Items.DIAMOND_SWORD);
            result.set(ModDataComponents.RUNE_DATA.get(),
                    new ModDataComponents.RuneData(runeId, 0L));
            return new RitualRecipe(
                    Ingredient.of(rune),
                    result, 0.0, 200,
                    List.of(
                            new SacrificeCondition(Ingredient.of(Items.AMETHYST_SHARD), 4),
                            new SacrificeCondition(Ingredient.of(rune), 1)
                    ));
        }).collect(Collectors.toList());
    }
}
