package com.denmoth.bloodrunes.neoforge.setup;

import com.denmoth.bloodrunes.BloodRunes;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BloodRunes.MOD_ID);

    public static final DeferredItem<Item> BLANK_RUNE = ITEMS.registerSimpleItem("blank_rune", new Item.Properties());
    public static final DeferredItem<Item> BLOOD_RUNE = ITEMS.registerSimpleItem("blood_rune", new Item.Properties());
    public static final DeferredItem<Item> COURAGE_RUNE = ITEMS.registerSimpleItem("courage_rune", new Item.Properties());
}
