package colossalrenders.globalwind.mixin;

import colossalrenders.globalwind.config.ModConfigs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import colossalrenders.globalwind.GlobalWindConstants;
import colossalrenders.globalwind.SyncSeedPayload;
import colossalrenders.globalwind.WindCalculator;
import colossalrenders.globalwind.WindInterface;

@Mixin(ServerWorld.class)
public class ServerWorldMixin implements WindInterface{
	@Unique
    private int syncClientCountdownTimer;
	@Unique
    private Vec3d wind;
	
	@Inject(at = @At("HEAD"), method = "tickWeather")
	private void init(CallbackInfo info) {
		calculateWind();
	}

	@Unique
    private void calculateWind(){
		if(!((World) (Object) this).getRegistryKey().equals(World.OVERWORLD)) return;

		long t = ((World) (Object) this).getTimeOfDay();
		long seed = ((ServerWorld) (Object) this).getSeed();
		//GlobalWind.LOGGER.info("" + t);

		wind = WindCalculator.calculateWind(t, seed, GlobalWindConstants.SQUASH, ((ServerWorld) (Object) this).isRaining(), ((ServerWorld) (Object) this).isThundering(), GlobalWindConstants.WIND_MULT, ModConfigs.WIND_MULTIPLIER_CONFIG);
		///GlobalWind.LOGGER.info("" + wind.toString());
		//if(t % 200 == 0) GlobalWind.LOGGER.info("Current wind velocity: " + wind.length());

		if(syncClientCountdownTimer == 0){
			syncClientCountdownTimer = 1000;

            for (ServerPlayerEntity s : ((ServerWorld) (Object) this).getPlayers()) {
                ServerPlayNetworking.send(s, new SyncSeedPayload(seed));
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
	public Vec3d global_Wind$getWind() {
		//GlobalWind.LOGGER.info("getting wind: " + wind.toString());
		return wind;
	}
}