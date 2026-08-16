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
                    .icon(() -> new ItemStack(ModItems.BLANK_RUNE.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModBlocks.ALTAR_ITEM.get());
                        output.accept(ModItems.BLANK_RUNE.get());
                        output.accept(ModItems.FEHU_RUNE.get());
                        output.accept(ModItems.URUZ_RUNE.get());
                        output.accept(ModItems.THURISAZ_RUNE.get());
                        output.accept(ModItems.ANSUZ_RUNE.get());
                        output.accept(ModItems.RAIDO_RUNE.get());
                        output.accept(ModItems.KENAZ_RUNE.get());
                        output.accept(ModItems.GEBO_RUNE.get());
                        output.accept(ModItems.WUNJO_RUNE.get());
                        output.accept(ModItems.HAGALAZ_RUNE.get());
                        output.accept(ModItems.NAUTHIZ_RUNE.get());
                        output.accept(ModItems.ISA_RUNE.get());
                        output.accept(ModItems.JERA_RUNE.get());
                        output.accept(ModItems.EIHWAZ_RUNE.get());
                        output.accept(ModItems.PERTHRO_RUNE.get());
                        output.accept(ModItems.ALGIZ_RUNE.get());
                        output.accept(ModItems.SOWILO_RUNE.get());
                        output.accept(ModItems.TIWAZ_RUNE.get());
                        output.accept(ModItems.BERKANO_RUNE.get());
                        output.accept(ModItems.EHWAZ_RUNE.get());
                        output.accept(ModItems.MANNAZ_RUNE.get());
                        output.accept(ModItems.LAGUZ_RUNE.get());
                        output.accept(ModItems.INGWAZ_RUNE.get());
                        output.accept(ModItems.OTHALA_RUNE.get());
                        output.accept(ModItems.DAGAZ_RUNE.get());
                        output.accept(ModItems.RITUAL_RUNE.get());
                    })
                    .build());
}
