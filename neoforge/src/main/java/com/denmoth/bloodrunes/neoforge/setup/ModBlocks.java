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

    public static final DeferredBlock<Block> ALTAR = BLOCKS.register("altar", () -> new AltarBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(2.5F)));
    public static final net.neoforged.neoforge.registries.DeferredItem<net.minecraft.world.item.BlockItem> ALTAR_ITEM = ModItems.ITEMS.registerSimpleBlockItem("altar", ALTAR);
}
