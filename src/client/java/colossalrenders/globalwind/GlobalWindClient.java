package colossalrenders.globalwind;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class GlobalWindClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(GlobalWind.WIND_UPDATE_PACKET_ID, (client, handler, buf, responseSender) -> {
			long seed = buf.readLong();
    		client.execute(() -> {
				((ClientWindInterface) (Object) client.world).setWindSeed(seed);
    		});
		});
	}
}