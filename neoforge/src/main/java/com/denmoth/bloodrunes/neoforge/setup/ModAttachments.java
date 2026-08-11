package com.denmoth.bloodrunes.neoforge.setup;

import com.denmoth.bloodrunes.BloodRunes;
import com.mojang.serialization.Codec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, BloodRunes.MOD_ID);

    public static final Supplier<AttachmentType<Boolean>> VIKING_LANGUAGE = ATTACHMENT_TYPES.register("viking_language",
            () -> AttachmentType.builder(() -> false).serialize(com.mojang.serialization.Codec.BOOL.fieldOf("knows_language")).build());
}
