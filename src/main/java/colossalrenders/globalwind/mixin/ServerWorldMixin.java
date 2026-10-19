package colossalrenders.globalwind.mixin;

import colossalrenders.globalwind.*;
import colossalrenders.globalwind.config.ModConfigs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerWorldMixin implements WindInterface{
	@Unique
    private int syncClientCountdownTimer;
	@Unique
    private Vec3 wind;
	
	@Inject(at = @At("HEAD"), method = "advanceWeatherCycle")
	private void init(CallbackInfo info) {
		calculateWind();
	}

	@Unique
    private void calculateWind(){
		if(!((Level) (Object) this).dimension().equals(Level.OVERWORLD)) return;

		long t = ((Level) (Object) this).getOverworldClockTime();
		long seed = ((ServerLevel) (Object) this).getSeed();
		//GlobalWind.LOGGER.info("" + t);

		wind = WindCalculator.calculateWind(t, seed, GlobalWindConstants.SQUASH, ((ServerLevel) (Object) this).isRaining(), ((ServerLevel) (Object) this).isThundering(), GlobalWindConstants.WIND_MULT, ModConfigs.WIND_MULTIPLIER_CONFIG);
		///GlobalWind.LOGGER.info("" + wind.toString());
		//if(t % 200 == 0) GlobalWind.LOGGER.info("Current wind velocity: " + wind.length());

		if(syncClientCountdownTimer == 0){
			syncClientCountdownTimer = 1000;

            for (ServerPlayer s : ((ServerLevel) (Object) this).players()) {
                ServerPlayNetworking.send(s, new SyncSeedPayload(seed));
				ServerPlayNetworking.send(s, new SyncConfigPayload(ModConfigs.WIND_MULTIPLIER_CONFIG));
            }
		}

		syncClientCountdownTimer--;
	}


	@Override
	public int global_Wind$getWindLevel(){
		if(this.wind == null) calculateWind();
		double wind = this.wind.length();
		return WindCalculator.calculateWindLevel(wind);
	}

	@Override
	public Vec3 global_Wind$getWind() {
		//GlobalWind.LOGGER.info("getting wind: " + wind.toString());
		return wind;
	}
}