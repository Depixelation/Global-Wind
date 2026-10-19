package colossalrenders.globalwind;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SyncSeedPayload(long seed) implements CustomPacketPayload{
    public static final CustomPacketPayload.Type<SyncSeedPayload> ID = new CustomPacketPayload.Type<>(GlobalWind.WIND_UPDATE_PACKET_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncSeedPayload> CODEC = StreamCodec.composite(ByteBufCodecs.VAR_LONG, SyncSeedPayload::seed, SyncSeedPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void registerS2C() {
        PayloadTypeRegistry.clientboundPlay().register(ID, CODEC);
    }
}
