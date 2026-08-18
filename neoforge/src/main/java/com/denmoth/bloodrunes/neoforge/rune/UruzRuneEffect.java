package com.denmoth.bloodrunes.neoforge.rune;

import com.denmoth.bloodrunes.BloodRunes;
import com.denmoth.bloodrunes.neoforge.api.IRuneEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

import java.util.List;

public class UruzRuneEffect implements IRuneEffect {

    @Override
    public String getRuneId() {
        return "uruz_rune";
    }

    @Override
    public void applyAttributes(ItemStack stack, ItemAttributeModifierEvent event) {
        String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
        boolean isChest = path.contains("chestplate");
        boolean isLegs = path.contains("leggings");
        boolean isWeapon = path.contains("sword") || path.contains("axe");
        boolean isShield = path.contains("shield");

        if (isChest) {
            event.addModifier(Attributes.MAX_HEALTH, new AttributeModifier(Identifier.fromNamespaceAndPath(BloodRunes.MOD_ID, "uruz_health"), 4.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.CHEST);
        } else if (isLegs) {
            event.addModifier(Attributes.MAX_HEALTH, new AttributeModifier(Identifier.fromNamespaceAndPath(BloodRunes.MOD_ID, "uruz_health"), 2.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.LEGS);
        } else if (isWeapon) {
            event.addModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(Identifier.fromNamespaceAndPath(BloodRunes.MOD_ID, "uruz_damage"), 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), EquipmentSlotGroup.MAINHAND);
        } else if (isShield) {
            event.addModifier(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(Identifier.fromNamespaceAndPath(BloodRunes.MOD_ID, "uruz_kb"), 0.20, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.OFFHAND);
        }
        // Drawback: -10% movement speed per equipped rune item
        event.addModifier(Attributes.MOVEMENT_SPEED, new AttributeModifier(Identifier.fromNamespaceAndPath(BloodRunes.MOD_ID, "uruz_slow_" + path), -0.10, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), EquipmentSlotGroup.ANY);
    }

    @Override
    public void appendTooltip(ItemStack stack, List<Component> tooltip) {
        String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
        boolean isChest = path.contains("chestplate");
        boolean isLegs = path.contains("leggings");
        boolean isWeapon = path.contains("sword") || path.contains("axe");
        boolean isShield = path.contains("shield");

        if (isChest) {
            tooltip.add(Component.literal(" §7+4 макс. здоровья").withStyle(ChatFormatting.GRAY));
        } else if (isLegs) {
            tooltip.add(Component.literal(" §7+2 макс. здоровья").withStyle(ChatFormatting.GRAY));
        } else if (isWeapon) {
            tooltip.add(Component.literal(" §7+15% урон от атак").withStyle(ChatFormatting.GRAY));
        } else if (isShield) {
            tooltip.add(Component.literal(" §7+20% сопротивление отбрасыванию").withStyle(ChatFormatting.GRAY));
        }
        tooltip.add(Component.literal(" §c-10% скорость передвижения").withStyle(ChatFormatting.RED));
    }
}
