package com.denmoth.bloodrunes.neoforge.setup;

import com.denmoth.bloodrunes.BloodRunes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {
    public static class Items {
        public static final TagKey<Item> RUNES = create("runes");
        public static final TagKey<Item> ELDER_FUTHARK = create("elder_futhark");
        public static final TagKey<Item> TIER_1_RUNES = create("tier_1_runes");
        public static final TagKey<Item> TIER_2_RUNES = create("tier_2_runes");
        public static final TagKey<Item> TIER_3_RUNES = create("tier_3_runes");
        public static final TagKey<Item> TIER_4_RUNES = create("tier_4_runes");

        private static TagKey<Item> create(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(BloodRunes.MOD_ID, name));
        }
    }
}
