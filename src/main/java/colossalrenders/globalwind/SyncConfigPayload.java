package colossalrenders.globalwind;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record SyncConfigPayload(double windStrength) implements CustomPayload {
    public static final Id<SyncConfigPayload> ID = new CustomPayload.Id<SyncConfigPayload>(GlobalWind.SYNC_CONFIG_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, SyncConfigPayload> CODEC = PacketCodec.of(
            (payload, buf) -> buf.writeDouble(payload.windStrength),
            (buf) -> new SyncConfigPayload(buf.readDouble())
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void registerS2C() {
        PayloadTypeRegistry.playS2C().register(ID, CODEC);
    }
}
