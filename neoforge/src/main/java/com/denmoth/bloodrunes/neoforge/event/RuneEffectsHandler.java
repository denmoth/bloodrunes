package com.denmoth.bloodrunes.neoforge.event;

import com.denmoth.bloodrunes.BloodRunes;
import com.denmoth.bloodrunes.neoforge.setup.ModAttachments;
import com.denmoth.bloodrunes.neoforge.setup.ModDataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

@EventBusSubscriber(modid = BloodRunes.MOD_ID)
public class RuneEffectsHandler {

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (event.getSource().getEntity() instanceof Player attacker) {
            ItemStack weapon = attacker.getMainHandItem();
            ModDataComponents.RuneData runeData = weapon.get(ModDataComponents.RUNE_DATA);
            
            if (runeData != null) {
                boolean knowsLanguage = attacker.getData(ModAttachments.VIKING_LANGUAGE);
                long currentTime = attacker.level().getGameTime();
                
                if (runeData.runeId().equals("blood_rune")) {
                    float healAmount = event.getOriginalDamage() * (knowsLanguage ? (0.3f + attacker.getRandom().nextFloat() * 0.2f) : (0.15f + attacker.getRandom().nextFloat() * 0.1f));
                    if (attacker.getRandom().nextFloat() < 0.15f) {
                        attacker.heal(healAmount);
                    }
                }
                
                if (runeData.runeId().equals("courage_rune")) {
                    if (runeData.cooldownEndTimestamp() <= currentTime) {
                        if (attacker.getHealth() / attacker.getMaxHealth() < 0.3f) {
                            float mult = knowsLanguage ? 1.5f : 1.25f;
                            event.setNewDamage(event.getNewDamage() * mult);
                            weapon.set(ModDataComponents.RUNE_DATA, new ModDataComponents.RuneData("courage_rune", currentTime + 2400));
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.tickCount % 20 != 0) return;
            
            boolean knowsLanguage = player.getData(ModAttachments.VIKING_LANGUAGE);
            long currentTime = player.level().getGameTime();
            int buffDuration = knowsLanguage ? 200 : 100;

            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (!slot.isArmor()) continue;
                
                ItemStack armor = player.getItemBySlot(slot);
                ModDataComponents.RuneData runeData = armor.get(ModDataComponents.RUNE_DATA);
                
                if (runeData != null && runeData.runeId().equals("courage_rune")) {
                    if (runeData.cooldownEndTimestamp() <= currentTime) {
                        if (player.getHealth() / player.getMaxHealth() < 0.3f) {
                            if (slot == EquipmentSlot.FEET) {
                                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, buffDuration, 0));
                            } else {
                                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, buffDuration, 0));
                            }
                            armor.set(ModDataComponents.RUNE_DATA, new ModDataComponents.RuneData("courage_rune", currentTime + 2400));
                        }
                    }
                }
            }
        }
    }
}
