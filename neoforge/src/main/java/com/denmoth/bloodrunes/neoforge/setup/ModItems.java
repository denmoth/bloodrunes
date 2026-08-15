package com.denmoth.bloodrunes.neoforge.setup;

import com.denmoth.bloodrunes.BloodRunes;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BloodRunes.MOD_ID);

    public static final DeferredItem<Item> BLANK_RUNE = ITEMS.registerItem("blank_rune", Item::new);
    
    // The 6 new runes from the design doc
    public static final DeferredItem<Item> URUZ_RUNE = ITEMS.registerItem("uruz_rune", Item::new);
    public static final DeferredItem<Item> FEHU_RUNE = ITEMS.registerItem("fehu_rune", Item::new);
    public static final DeferredItem<Item> BERKANO_RUNE = ITEMS.registerItem("berkano_rune", Item::new);
    public static final DeferredItem<Item> HAGALAZ_RUNE = ITEMS.registerItem("hagalaz_rune", Item::new);
    public static final DeferredItem<Item> WUNJO_RUNE = ITEMS.registerItem("wunjo_rune", Item::new);
    public static final DeferredItem<Item> DAGAZ_RUNE = ITEMS.registerItem("dagaz_rune", Item::new);
}
