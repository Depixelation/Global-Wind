package colossalrenders.globalwind.mixin;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import java.util.Iterator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import colossalrenders.globalwind.GlobalWindConstants;
import colossalrenders.globalwind.SyncSeedPayload;
import colossalrenders.globalwind.WindCalculator;
import colossalrenders.globalwind.WindInterface;

@Mixin(ServerWorld.class)
public class ServerWorldMixin implements WindInterface{
	private int syncClientCountdownTimer;
	private Vec3d wind;
	
	@Inject(at = @At("HEAD"), method = "tickWeather")
	private void init(CallbackInfo info) {
		calculateWind();
	}

	private void calculateWind(){
		if(!((World) (Object) this).getRegistryKey().equals(World.OVERWORLD)) return;

		long t = ((World) (Object) this).getTimeOfDay();
		long seed = ((ServerWorld) (Object) this).getSeed();
		//GlobalWind.LOGGER.info("" + t);

		wind = WindCalculator.calculateWind(t, seed, GlobalWindConstants.SQUASH, ((ServerWorld) (Object) this).isRaining(), ((ServerWorld) (Object) this).isThundering(), GlobalWindConstants.WIND_MULT);
		///GlobalWind.LOGGER.info("" + wind.toString());
		//if(t % 200 == 0) GlobalWind.LOGGER.info("Current wind velocity: " + wind.length());

		if(syncClientCountdownTimer == 0){
			syncClientCountdownTimer = 1000;
			Iterator<ServerPlayerEntity> i = ((ServerWorld) (Object) this).getPlayers().iterator();

			while(i.hasNext()){
				ServerPlayerEntity s = i.next();

				ServerPlayNetworking.send(s, new SyncSeedPayload(seed));
			}
		}

		syncClientCountdownTimer--;
	}


	@Override
	public int getWindLevel(){
		if(this.wind == null) calculateWind();
		double wind = this.wind.length();
		return WindCalculator.calculateWindLevel(wind);
	}

	@Override
	public Vec3d getWind() {
		//GlobalWind.LOGGER.info("getting wind: " + wind.toString());
		return wind;
	}
}