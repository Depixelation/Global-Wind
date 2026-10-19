package colossalrenders.globalwind;

import colossalrenders.globalwind.config.ModConfigs;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalWind implements ModInitializer {
	public static final String MOD_ID = "globalwind";
	public static final SoundEvent WIND_GENTLE = registerSound("wind-gentle");
	public static final SoundEvent WIND_HOWL = registerSound("wind-howl");
	public static final SoundEvent WIND_GENTLE_IN = registerSound("wind-gentle-in");
	public static final SoundEvent WIND_HOWL_IN = registerSound("wind-howl-in");
	public static final SoundEvent WIND_GENTLE_OUT = registerSound("wind-gentle-out");
	public static final SoundEvent WIND_HOWL_OUT = registerSound("wind-howl-out");
	public static final Identifier WIND_UPDATE_PACKET_ID = Identifier.fromNamespaceAndPath(MOD_ID, "wind-sound"); //why is this id pathed to "wind-sound?" What does it have to do with sound??
	public static final Identifier SYNC_CONFIG_PACKET_ID = Identifier.fromNamespaceAndPath(MOD_ID, "sync-wind-strength");

	public static final SoundEvent[] WIND_SOUNDS = {WIND_GENTLE, WIND_GENTLE_IN, WIND_GENTLE_OUT, WIND_HOWL, WIND_HOWL_IN, WIND_HOWL_OUT};

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Global Wind Init");

		SyncSeedPayload.registerS2C();
		SyncConfigPayload.registerS2C();

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {

			server.execute(() -> {
				ServerPlayNetworking.send(handler.getPlayer(), new SyncSeedPayload(handler.getPlayer().level().getSeed()));
				ServerPlayNetworking.send(handler.getPlayer(), new SyncConfigPayload(ModConfigs.WIND_MULTIPLIER_CONFIG));
			});
			
		});

		ModConfigs.registerConfigs();
	}

	private static SoundEvent registerSound(String id) {
		Identifier identifier = Identifier.fromNamespaceAndPath(MOD_ID, id);
		return Registry.register(BuiltInRegistries.SOUND_EVENT, identifier, SoundEvent.createVariableRangeEvent(identifier));
	}
}