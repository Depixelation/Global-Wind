package colossalrenders.globalwind;

import colossalrenders.globalwind.config.ModConfigs;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

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
	public static final Identifier WIND_UPDATE_PACKET_ID = Identifier.of(MOD_ID, "wind-sound");

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

		PayloadTypeRegistry.playS2C().register(SyncSeedPayload.ID, SyncSeedPayload.CODEC);

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {

			server.execute(() -> {
				ServerPlayNetworking.send(handler.getPlayer(), new SyncSeedPayload(handler.getPlayer().getServerWorld().getSeed()));
			});
			
		});

		ModConfigs.registerConfigs();
	}

	private static SoundEvent registerSound(String id) {
		Identifier identifier = Identifier.of(MOD_ID, id);
		return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
	}
}