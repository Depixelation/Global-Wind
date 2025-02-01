package colossalrenders.globalwind;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class GlobalWindClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(SyncSeedPayload.ID, (payload, context) -> {
			long seed = payload.seed();
    		context.client().execute(() -> {
				((ClientWindInterface) (Object) context.player().getWorld()).setWindSeed(seed);
    		});
		});
	}
}