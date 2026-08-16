package com.denmoth.bloodrunes.neoforge.setup;

import com.denmoth.bloodrunes.BloodRunes;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BloodRunes.MOD_ID);

    public static final DeferredItem<Item> BLANK_RUNE = ITEMS.registerItem("blank_rune", Item::new);
    
    public static final DeferredItem<Item> FEHU_RUNE = ITEMS.registerItem("fehu_rune", Item::new);
    public static final DeferredItem<Item> KENAZ_RUNE = ITEMS.registerItem("kenaz_rune", Item::new);
    public static final DeferredItem<Item> RAIDO_RUNE = ITEMS.registerItem("raido_rune", Item::new);
    public static final DeferredItem<Item> WUNJO_RUNE = ITEMS.registerItem("wunjo_rune", Item::new);
    public static final DeferredItem<Item> LAGUZ_RUNE = ITEMS.registerItem("laguz_rune", Item::new);
    public static final DeferredItem<Item> ANSUZ_RUNE = ITEMS.registerItem("ansuz_rune", Item::new);
    public static final DeferredItem<Item> URUZ_RUNE = ITEMS.registerItem("uruz_rune", Item::new);
    public static final DeferredItem<Item> THURISAZ_RUNE = ITEMS.registerItem("thurisaz_rune", Item::new);
    public static final DeferredItem<Item> ISA_RUNE = ITEMS.registerItem("isa_rune", Item::new);
    public static final DeferredItem<Item> SOWILO_RUNE = ITEMS.registerItem("sowilo_rune", Item::new);
    public static final DeferredItem<Item> MANNAZ_RUNE = ITEMS.registerItem("mannaz_rune", Item::new);
    public static final DeferredItem<Item> EHWAZ_RUNE = ITEMS.registerItem("ehwaz_rune", Item::new);
    public static final DeferredItem<Item> INGWAZ_RUNE = ITEMS.registerItem("ingwaz_rune", Item::new);
    public static final DeferredItem<Item> ALGIZ_RUNE = ITEMS.registerItem("algiz_rune", Item::new);
    public static final DeferredItem<Item> BERKANO_RUNE = ITEMS.registerItem("berkano_rune", Item::new);
    public static final DeferredItem<Item> EIHWAZ_RUNE = ITEMS.registerItem("eihwaz_rune", Item::new);
    public static final DeferredItem<Item> PERTHRO_RUNE = ITEMS.registerItem("perthro_rune", Item::new);
    public static final DeferredItem<Item> DAGAZ_RUNE = ITEMS.registerItem("dagaz_rune", Item::new);
    public static final DeferredItem<Item> JERA_RUNE = ITEMS.registerItem("jera_rune", Item::new);
    public static final DeferredItem<Item> TIWAZ_RUNE = ITEMS.registerItem("tiwaz_rune", Item::new);
    public static final DeferredItem<Item> HAGALAZ_RUNE = ITEMS.registerItem("hagalaz_rune", Item::new);
    public static final DeferredItem<Item> OTHALA_RUNE = ITEMS.registerItem("othala_rune", Item::new);
    public static final DeferredItem<Item> NAUTHIZ_RUNE = ITEMS.registerItem("nauthiz_rune", Item::new);
    public static final DeferredItem<Item> GEBO_RUNE = ITEMS.registerItem("gebo_rune", Item::new);
    
    public static final DeferredItem<Item> RITUAL_RUNE = ITEMS.registerItem("ritual_rune", Item::new);
}
