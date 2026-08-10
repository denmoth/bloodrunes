package com.denmoth.bloodrunes.neoforge.setup;

import com.denmoth.bloodrunes.BloodRunes;
import com.denmoth.bloodrunes.neoforge.block.AltarBlock;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(BloodRunes.MOD_ID);

    public static final DeferredBlock<Block> ALTAR = BLOCKS.register("altar", AltarBlock::new);
    public static final DeferredItem<Item> ALTAR_ITEM = ModItems.ITEMS.registerSimpleBlockItem("altar", ALTAR);
}
