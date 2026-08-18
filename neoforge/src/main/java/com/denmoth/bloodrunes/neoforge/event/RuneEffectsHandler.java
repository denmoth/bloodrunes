package com.denmoth.bloodrunes.neoforge.event;

import com.denmoth.bloodrunes.BloodRunes;
import com.denmoth.bloodrunes.neoforge.setup.ModDataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = BloodRunes.MOD_ID)
public class RuneEffectsHandler {

    private static final net.minecraft.world.entity.EquipmentSlot[] ALL_EQUIPMENT_SLOTS = net.minecraft.world.entity.EquipmentSlot.values();

    // Helper to get runes from an entity's equipment
    public static List<String> getRunes(LivingEntity entity) {
        List<String> runes = new ArrayList<>(6);
        for (net.minecraft.world.entity.EquipmentSlot slot : ALL_EQUIPMENT_SLOTS) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (stack.has(ModDataComponents.RUNE_DATA.get())) {
                runes.add(stack.get(ModDataComponents.RUNE_DATA.get()).runeId());
            }
        }
        return runes;
    }

    public static boolean hasRune(LivingEntity entity, String runeName) {
        String cleanRune = runeName.startsWith("bloodrunes:") ? runeName.substring(11) : runeName;
        for (net.minecraft.world.entity.EquipmentSlot slot : ALL_EQUIPMENT_SLOTS) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (stack.has(ModDataComponents.RUNE_DATA.get())) {
                String id = stack.get(ModDataComponents.RUNE_DATA.get()).runeId();
                String cleanId = id.startsWith("bloodrunes:") ? id.substring(11) : id;
                if (cleanId.equals(cleanRune)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean hasRuneOnWeapon(LivingEntity entity, String runeName) {
        ItemStack mainHand = entity.getMainHandItem();
        if (mainHand.has(ModDataComponents.RUNE_DATA.get())) {
            String rId = mainHand.get(ModDataComponents.RUNE_DATA.get()).runeId();
            String cleanId = rId.startsWith("bloodrunes:") ? rId.substring(11) : rId;
            String cleanRune = runeName.startsWith("bloodrunes:") ? runeName.substring(11) : runeName;
            return cleanId.equals(cleanRune);
        }
        return false;
    }

    @SubscribeEvent
    public static void onAttributeModifiers(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.has(ModDataComponents.RUNE_DATA.get())) {
            String runeId = stack.get(ModDataComponents.RUNE_DATA.get()).runeId();
            com.denmoth.bloodrunes.neoforge.api.RuneRegistry.get(runeId).ifPresent(effect -> effect.applyAttributes(stack, event));
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        LivingEntity victim = event.getEntity();
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            ItemStack weapon = attacker.getMainHandItem();
            if (weapon.has(ModDataComponents.RUNE_DATA.get())) {
                String runeId = weapon.get(ModDataComponents.RUNE_DATA.get()).runeId();
                com.denmoth.bloodrunes.neoforge.api.RuneRegistry.get(runeId).ifPresent(effect -> effect.onAttack(attacker, victim, weapon, event));
            }
        }
        for (net.minecraft.world.entity.EquipmentSlot slot : ALL_EQUIPMENT_SLOTS) {
            ItemStack stack = victim.getItemBySlot(slot);
            if (stack.has(ModDataComponents.RUNE_DATA.get())) {
                String runeId = stack.get(ModDataComponents.RUNE_DATA.get()).runeId();
                LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity le ? le : null;
                com.denmoth.bloodrunes.neoforge.api.RuneRegistry.get(runeId).ifPresent(effect -> effect.onDamaged(victim, attacker, stack, event));
            }
        }
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.has(ModDataComponents.RUNE_DATA.get())) {
            String runeId = stack.get(ModDataComponents.RUNE_DATA.get()).runeId();
            String cleanName = runeId.startsWith("bloodrunes:") ? runeId.substring(11) : runeId;
            
            event.getToolTip().add(Component.empty());
            event.getToolTip().add(Component.translatable("item.bloodrunes." + cleanName).withStyle(net.minecraft.ChatFormatting.GOLD));
            
            com.denmoth.bloodrunes.neoforge.api.RuneRegistry.get(runeId).ifPresent(effect -> effect.appendTooltip(stack, event.getToolTip()));
        }
    }
}
