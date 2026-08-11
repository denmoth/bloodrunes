package com.denmoth.bloodrunes.neoforge.command;

import com.denmoth.bloodrunes.BloodRunes;
import com.denmoth.bloodrunes.neoforge.network.SyncLanguagePacket;
import com.denmoth.bloodrunes.neoforge.setup.ModAttachments;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collection;

@EventBusSubscriber(modid = BloodRunes.MOD_ID)
public class CommandVikingLanguage {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        
        dispatcher.register(Commands.literal("bloodrunes")
            .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
            .then(Commands.literal("language")
                .then(Commands.argument("targets", EntityArgument.players())
                    .then(Commands.argument("knowsLanguage", BoolArgumentType.bool())
                        .executes(context -> {
                            Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
                            boolean value = BoolArgumentType.getBool(context, "knowsLanguage");
                            
                            for (ServerPlayer player : targets) {
                                player.setData(ModAttachments.VIKING_LANGUAGE, value);
                                PacketDistributor.sendToPlayer(player, new SyncLanguagePacket(value));
                            }
                            
                            context.getSource().sendSuccess(() -> Component.literal("Set Viking Language Knowledge for " + targets.size() + " players to " + value), true);
                            return targets.size();
                        })
                    )
                )
            )
        );
    }
}
