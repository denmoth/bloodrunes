package com.denmoth.bloodrunes.neoforge.event;

import com.denmoth.bloodrunes.BloodRunes;
import com.denmoth.bloodrunes.neoforge.setup.ModAttachments;
import com.denmoth.bloodrunes.neoforge.setup.ModDataComponents;
import com.denmoth.bloodrunes.neoforge.setup.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = BloodRunes.MOD_ID)
public class CraftingAndAnvilHandler {

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        Player player = event.getPlayer();

        if (player == null || !player.getData(ModAttachments.VIKING_LANGUAGE)) {
            return; // Cannot apply runes without knowledge
        }

        if (right.getItem() == ModItems.BLOOD_RUNE.get() || right.getItem() == ModItems.COURAGE_RUNE.get()) {
            boolean isWeapon = left.getItem() instanceof SwordItem || left.getItem() instanceof AxeItem;
            boolean isArmor = left.getItem() instanceof ArmorItem;
            
            // Blood Rune only goes on weapons. Courage goes on weapons and armor.
            if (isWeapon || (isArmor && right.getItem() == ModItems.COURAGE_RUNE.get())) {
                ItemStack result = left.copy();
                String runeId = right.getItem() == ModItems.BLOOD_RUNE.get() ? "blood_rune" : "courage_rune";
                result.set(ModDataComponents.RUNE_DATA, new ModDataComponents.RuneData(runeId, 0));
                
                event.setOutput(result);
                event.setCost(5);
                event.setMaterialCost(1);
            }
        }
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (event.getCrafting().getItem() == ModItems.BLANK_RUNE.get()) {
            Player player = event.getEntity();
            if (!player.getData(ModAttachments.VIKING_LANGUAGE)) {
                event.getCrafting().setCount(0); // Void the crafted item
                
                // Refund ingredients
                player.drop(new ItemStack(Items.AMETHYST_SHARD, 4), false);
                player.drop(new ItemStack(Items.GOLD_INGOT, 1), false);
                player.drop(new ItemStack(Items.BONE_BLOCK, 4), false);
                
                player.displayClientMessage(Component.literal("You do not understand the ancient runes to craft this."), true);
            }
        }
    }
}
