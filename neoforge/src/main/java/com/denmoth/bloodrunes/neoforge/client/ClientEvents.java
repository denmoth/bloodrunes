package com.denmoth.bloodrunes.neoforge.client;

import com.denmoth.bloodrunes.BloodRunes;
import com.denmoth.bloodrunes.neoforge.setup.ModBlockEntities;
import com.denmoth.bloodrunes.neoforge.setup.ModDataComponents;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = BloodRunes.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ModDataComponents.RuneData runeData = event.getItemStack().get(ModDataComponents.RUNE_DATA);
        if (runeData != null) {
            String key = "item.bloodrunes." + runeData.runeId();
            event.getToolTip().add(Component.empty());
            event.getToolTip().add(Component.translatable(key).withStyle(net.minecraft.ChatFormatting.GOLD));
            String descKey = key + ".desc";
            String[] lines = Component.translatable(descKey).getString().split("\n");
            for (String line : lines) {
                event.getToolTip().add(Component.literal(line).withStyle(net.minecraft.ChatFormatting.GRAY));
            }
        }
    }

    // Called from BloodRunesNeoForge on the MOD bus (client only)
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.ALTAR_BLOCK_ENTITY.get(), AltarBlockEntityRenderer::new);
    }
}
