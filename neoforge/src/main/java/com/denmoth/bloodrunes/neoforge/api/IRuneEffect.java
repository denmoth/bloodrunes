package com.denmoth.bloodrunes.neoforge.api;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;

/**
 * Interface representing custom combat, attribute, and passive effects
 * granted when a rune is inscribed on equipment via the Ristublot ritual.
 */
public interface IRuneEffect {
    
    /**
     * @return The simple rune identifier without prefix, e.g. "uruz_rune"
     */
    String getRuneId();

    /**
     * Called when item attributes are calculated for equipment holding this rune.
     */
    default void applyAttributes(ItemStack stack, ItemAttributeModifierEvent event) {}

    /**
     * Called when the entity holding this weapon attacks a victim.
     */
    default void onAttack(LivingEntity attacker, LivingEntity victim, ItemStack weapon, LivingDamageEvent.Pre event) {}

    /**
     * Called when the entity wearing this armor / holding this shield takes damage.
     */
    default void onDamaged(LivingEntity victim, LivingEntity attacker, ItemStack equippedStack, LivingDamageEvent.Pre event) {}

    /**
     * Appends descriptive stat bonuses / drawbacks to the item tooltip.
     */
    default void appendTooltip(ItemStack stack, List<Component> tooltip) {}
}
