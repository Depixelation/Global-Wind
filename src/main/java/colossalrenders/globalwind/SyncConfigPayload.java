package colossalrenders.globalwind;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SyncConfigPayload(double windStrength) implements CustomPacketPayload {
    public static final Type<SyncConfigPayload> ID = new CustomPacketPayload.Type<SyncConfigPayload>(GlobalWind.SYNC_CONFIG_PACKET_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncConfigPayload> CODEC = StreamCodec.ofMember(
            (payload, buf) -> buf.writeDouble(payload.windStrength),
            (buf) -> new SyncConfigPayload(buf.readDouble())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void registerS2C() {
        PayloadTypeRegistry.playS2C().register(ID, CODEC);
    }
}
