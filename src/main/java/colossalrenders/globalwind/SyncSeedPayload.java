package colossalrenders.globalwind;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SyncSeedPayload(long seed) implements CustomPayload{
    public static final CustomPayload.Id<SyncSeedPayload> ID = new CustomPayload.Id<>(GlobalWind.WIND_UPDATE_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, SyncSeedPayload> CODEC = PacketCodec.tuple(PacketCodecs.VAR_LONG, SyncSeedPayload::seed, SyncSeedPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
