package colossalrenders.globalwind.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import colossalrenders.globalwind.ClientWindInterface;
import colossalrenders.globalwind.GlobalWind;
import colossalrenders.globalwind.GlobalWindConstants;
import colossalrenders.globalwind.PlayerInterface;
import colossalrenders.globalwind.WindCalculator;
import colossalrenders.globalwind.WindSoundManager;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

@Mixin(LocalPlayer.class)
public class ClientPlayerEntityMixin implements PlayerInterface{
	@Unique
    private boolean wasOutside;
	//@Unique
    private final WindSoundManager WIND_GENTLE = WindSoundManager.of(GlobalWind.WIND_GENTLE_IN, GlobalWind.WIND_GENTLE, GlobalWind.WIND_GENTLE_OUT).ofLength(20*20).ofThreshold(1);
	//@Unique
    private final WindSoundManager WIND_HOWL = WindSoundManager.of(GlobalWind.WIND_HOWL_IN, GlobalWind.WIND_HOWL, GlobalWind.WIND_HOWL_OUT).ofLength(20*20 + 1).ofThreshold(2);
	@Unique
    Vec3 prevWindVector;

	@Override
	public boolean global_Wind$wasOutside() {
		return wasOutside;
	}

	@Override
	public void global_Wind$setOutside(Boolean outside) {
		wasOutside = outside;
	}

	@Inject(at = @At("HEAD"), method = "tick")
	private void tickInjection(CallbackInfo info){
		LocalPlayer currentPlayer = (LocalPlayer) (Object) this;

		tickSound(WIND_GENTLE, currentPlayer);
		tickSound(WIND_HOWL, currentPlayer);

		if(prevWindVector == null) prevWindVector = Vec3.ZERO;
		if(!currentPlayer.isCreative() && !currentPlayer.isSpectator() && !currentPlayer.isInLava() && !currentPlayer.isInWater() && !currentPlayer.onClimbable() && global_Wind$isOutside(13)){
			if(((ClientWindInterface) (Object) currentPlayer.level()).global_Wind$getWindLevel() > 0){
				//GlobalWind.LOGGER.info("windlevel more than 0");
				Vec3 windVector = ((ClientWindInterface) (Object) currentPlayer.level()).global_Wind$getWind();
				if(windVector.length() < GlobalWindConstants.MIN_WIND_SPEED_FOR_MOVEMENT) return;

				if(!currentPlayer.onGround()){
					//GlobalWind.LOGGER.info("player not on ground");
					if(prevWindVector.length() < windVector.length()){
						prevWindVector = WindCalculator.calculateWindVector(prevWindVector, windVector);
					}

					addVelocity(currentPlayer, prevWindVector);
				}else{
					prevWindVector = Vec3.ZERO;

					if(windVector.length() > GlobalWindConstants.MAX_WIND_SPEED_BEFORE_SLIPPING){
						prevWindVector = windVector.scale(GlobalWindConstants.SLIPPING_MULTIPLIER);
						addVelocity(currentPlayer, prevWindVector);
					}
				}
			}
		}
	}

	@Unique
    private static void addVelocity(LocalPlayer player, Vec3 vector){
		player.push(vector);
		player.needsSync = true;
		//GlobalWind.LOGGER.info("Adding velocity: " + vector.length());
	}

	@Unique
    private void tickSound(WindSoundManager sound, LocalPlayer currentPlayer){
		if(sound.isPlaying()){
			sound.tick((ClientLevel) currentPlayer.level(), currentPlayer);
		}else{
			if(((ClientWindInterface) (Object) currentPlayer.level()).global_Wind$getWindLevel() >= sound.getThreshold()){
				if(((PlayerInterface) (Object) currentPlayer).global_Wind$isOutside(0))sound.startPlaying();
			}
		}
	}

	@Override
	public boolean global_Wind$isOutside(int threshold) {
		LocalPlayer currentPlayer = (LocalPlayer) (Object) this;
		return currentPlayer.level().getBrightness(LightLayer.SKY, currentPlayer.blockPosition()) > threshold;
	}
}