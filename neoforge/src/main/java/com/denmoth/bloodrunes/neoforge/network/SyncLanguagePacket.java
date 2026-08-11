package com.denmoth.bloodrunes.neoforge.network;

import com.denmoth.bloodrunes.BloodRunes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SyncLanguagePacket(boolean knowsLanguage) implements CustomPacketPayload {
    public static final Type<SyncLanguagePacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(BloodRunes.MOD_ID, "sync_language"));
    
    public static final StreamCodec<FriendlyByteBuf, SyncLanguagePacket> STREAM_CODEC = StreamCodec.ofMember(
        SyncLanguagePacket::write, SyncLanguagePacket::new
    );

    public SyncLanguagePacket(FriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(this.knowsLanguage);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
