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
            String name = runeId.replace("bloodrunes:", "");
            
            String path = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
            boolean isChest = path.contains("chestplate");
            boolean isLegs = path.contains("leggings");
            boolean isWeapon = path.contains("sword") || path.contains("axe");
            boolean isShield = path.contains("shield");
            
            // Tier II Runes
            if (name.equals("uruz_rune")) {
                if (isChest) {
                    event.addModifier(Attributes.MAX_HEALTH, new AttributeModifier(Identifier.fromNamespaceAndPath(BloodRunes.MOD_ID, "uruz_health"), 4.0, AttributeModifier.Operation.ADD_VALUE), net.minecraft.world.entity.EquipmentSlotGroup.CHEST);
                } else if (isLegs) {
                    event.addModifier(Attributes.MAX_HEALTH, new AttributeModifier(Identifier.fromNamespaceAndPath(BloodRunes.MOD_ID, "uruz_health"), 2.0, AttributeModifier.Operation.ADD_VALUE), net.minecraft.world.entity.EquipmentSlotGroup.LEGS);
                } else if (isWeapon) {
                    event.addModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(Identifier.fromNamespaceAndPath(BloodRunes.MOD_ID, "uruz_damage"), 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), net.minecraft.world.entity.EquipmentSlotGroup.MAINHAND);
                } else if (isShield) {
                    event.addModifier(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(Identifier.fromNamespaceAndPath(BloodRunes.MOD_ID, "uruz_kb"), 0.20, AttributeModifier.Operation.ADD_VALUE), net.minecraft.world.entity.EquipmentSlotGroup.OFFHAND);
                }
                // Drawback: -10% movement speed per equipped rune item
                event.addModifier(Attributes.MOVEMENT_SPEED, new AttributeModifier(Identifier.fromNamespaceAndPath(BloodRunes.MOD_ID, "uruz_slow_" + path), -0.10, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), net.minecraft.world.entity.EquipmentSlotGroup.ANY);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        LivingEntity victim = event.getEntity();
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            ItemStack weapon = attacker.getMainHandItem();
            if (weapon.has(ModDataComponents.RUNE_DATA.get())) {
                String runeId = weapon.get(ModDataComponents.RUNE_DATA.get()).runeId();
                if (runeId.equals("uruz_rune") || runeId.equals("bloodrunes:uruz_rune")) {
                    // +15% damage is handled by attributes
                }
            }
        }
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.has(ModDataComponents.RUNE_DATA.get())) {
            String runeId = stack.get(ModDataComponents.RUNE_DATA.get()).runeId();
            String name = runeId.replace("bloodrunes:", "");
            
            String path = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
            boolean isChest = path.contains("chestplate");
            boolean isLegs = path.contains("leggings");
            boolean isWeapon = path.contains("sword") || path.contains("axe");
            boolean isShield = path.contains("shield");
            
            event.getToolTip().add(Component.empty());
            event.getToolTip().add(Component.translatable("item.bloodrunes." + name).withStyle(net.minecraft.ChatFormatting.GOLD));
            
            if (name.equals("uruz_rune")) {
                if (isChest) {
                    event.getToolTip().add(Component.literal(" §7+4 макс. здоровья").withStyle(net.minecraft.ChatFormatting.GRAY));
                } else if (isLegs) {
                    event.getToolTip().add(Component.literal(" §7+2 макс. здоровья").withStyle(net.minecraft.ChatFormatting.GRAY));
                } else if (isWeapon) {
                    event.getToolTip().add(Component.literal(" §7+15% урон от атак").withStyle(net.minecraft.ChatFormatting.GRAY));
                } else if (isShield) {
                    event.getToolTip().add(Component.literal(" §7+20% сопротивление отбрасыванию").withStyle(net.minecraft.ChatFormatting.GRAY));
                }
                event.getToolTip().add(Component.literal(" §c-10% скорость передвижения").withStyle(net.minecraft.ChatFormatting.RED));
            }
        }
    }
}
