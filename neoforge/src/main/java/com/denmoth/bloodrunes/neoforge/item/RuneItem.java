package com.denmoth.bloodrunes.neoforge.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

public class RuneItem extends Item {
    private final String tooltipKey;

    public RuneItem(Properties properties, String tooltipKey) {
        super(properties);
        this.tooltipKey = tooltipKey;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, display, tooltipComponents, tooltipFlag);
        if (this.tooltipKey != null) {
            String[] lines = Component.translatable(this.tooltipKey).getString().split("\n");
            for (String line : lines) {
                tooltipComponents.accept(Component.literal(line));
            }
        }
    }
}
