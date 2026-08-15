package com.denmoth.bloodrunes.neoforge.setup;

import com.denmoth.bloodrunes.BloodRunes;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BloodRunes.MOD_ID);

    public static final DeferredItem<Item> BLANK_RUNE = ITEMS.registerSimpleItem("blank_rune");
    public static final DeferredItem<Item> BLOOD_RUNE = ITEMS.registerSimpleItem("blood_rune");
    public static final DeferredItem<Item> COURAGE_RUNE = ITEMS.registerSimpleItem("courage_rune");
    
    // The 6 new runes from the design doc
    public static final DeferredItem<Item> URUZ_RUNE = ITEMS.registerSimpleItem("uruz_rune");
    public static final DeferredItem<Item> FEHU_RUNE = ITEMS.registerSimpleItem("fehu_rune");
    public static final DeferredItem<Item> BERKANO_RUNE = ITEMS.registerSimpleItem("berkano_rune");
    public static final DeferredItem<Item> HAGALAZ_RUNE = ITEMS.registerSimpleItem("hagalaz_rune");
    public static final DeferredItem<Item> WUNJO_RUNE = ITEMS.registerSimpleItem("wunjo_rune");
    public static final DeferredItem<Item> DAGAZ_RUNE = ITEMS.registerSimpleItem("dagaz_rune");
}
