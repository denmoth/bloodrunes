package com.denmoth.bloodrunes.neoforge.setup;

import com.denmoth.bloodrunes.BloodRunes;
import com.denmoth.bloodrunes.neoforge.block.AltarBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(BloodRunes.MOD_ID);

    private static ResourceKey<Block> key(String name) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(BloodRunes.MOD_ID, name));
    }

    public static final DeferredBlock<Block> ALTAR = BLOCKS.register("altar",
            () -> new AltarBlock(BlockBehaviour.Properties.of()
                    .setId(key("altar"))
                    .requiresCorrectToolForDrops()
                    .strength(2.5F)
                    .noOcclusion()));

    public static final net.neoforged.neoforge.registries.DeferredItem<net.minecraft.world.item.BlockItem> ALTAR_ITEM =
            ModItems.ITEMS.registerSimpleBlockItem("altar", () -> ALTAR.get());
}
