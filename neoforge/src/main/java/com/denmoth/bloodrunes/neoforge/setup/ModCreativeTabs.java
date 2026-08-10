package com.denmoth.bloodrunes.neoforge.setup;

import com.denmoth.bloodrunes.BloodRunes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BloodRunes.MOD_ID);

    public static final Supplier<CreativeModeTab> BLOOD_RUNES_TAB = CREATIVE_MODE_TABS.register("blood_runes_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.literal("Blood & Runes"))
                    .icon(() -> new ItemStack(ModItems.BLOOD_RUNE.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModBlocks.ALTAR_ITEM.get());
                        output.accept(ModItems.BLANK_RUNE.get());
                        output.accept(ModItems.BLOOD_RUNE.get());
                        output.accept(ModItems.COURAGE_RUNE.get());
                    })
                    .build());
}
