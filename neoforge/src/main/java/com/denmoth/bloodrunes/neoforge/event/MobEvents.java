package com.denmoth.bloodrunes.neoforge.event;

import com.denmoth.bloodrunes.BloodRunes;
import com.denmoth.bloodrunes.neoforge.block.AltarBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;

@EventBusSubscriber(modid = BloodRunes.MOD_ID)
public class MobEvents {

    @SubscribeEvent
    public static void onLivingChangeTarget(LivingChangeTargetEvent event) {
        // Temporarily commented out for compilation
    }
}
