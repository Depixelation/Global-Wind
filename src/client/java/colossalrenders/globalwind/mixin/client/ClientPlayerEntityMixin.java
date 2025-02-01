package colossalrenders.globalwind.mixin.client;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;

import colossalrenders.globalwind.ClientWindInterface;
import colossalrenders.globalwind.GlobalWind;
import colossalrenders.globalwind.GlobalWindConstants;
import colossalrenders.globalwind.PlayerInterface;
import colossalrenders.globalwind.WindCalculator;
import colossalrenders.globalwind.WindSoundManager;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin implements PlayerInterface{
	private boolean wasOutside;
	private final WindSoundManager WIND_GENTLE = WindSoundManager.of(GlobalWind.WIND_GENTLE_IN, GlobalWind.WIND_GENTLE, GlobalWind.WIND_GENTLE_OUT).ofLength(20*20).ofThreshold(1);
	private final WindSoundManager WIND_HOWL = WindSoundManager.of(GlobalWind.WIND_HOWL_IN, GlobalWind.WIND_HOWL, GlobalWind.WIND_HOWL_OUT).ofLength(20*20 + 1).ofThreshold(2);
	Vec3d prevWindVector;

	@Override
	public boolean wasOutside() {
		return wasOutside;
	}

	@Override
	public void setOutside(Boolean outside) {
		wasOutside = outside;
	}

	@Inject(at = @At("HEAD"), method = "tick")
	private void tickInjection(CallbackInfo info){
		ClientPlayerEntity currentPlayer = (ClientPlayerEntity) (Object) this;

		tickSound(WIND_GENTLE, currentPlayer);
		tickSound(WIND_HOWL, currentPlayer);
	}

	private void tickSound(WindSoundManager sound, ClientPlayerEntity currentPlayer){
		if(sound.isPlaying()){
			sound.tick((ClientWorld) currentPlayer.getWorld(), currentPlayer);
		}else{
			if(((ClientWindInterface) (Object) currentPlayer.getWorld()).getWindLevel() >= sound.getThreshold()){
				if(((PlayerInterface) (Object) currentPlayer).isOutside(0))sound.startPlaying();
			}
		}
	}

	@Override
	public boolean isOutside(int threshold) {
		ClientPlayerEntity currentPlayer = (ClientPlayerEntity) (Object) this;
		return currentPlayer.getWorld().getLightLevel(LightType.SKY, currentPlayer.getBlockPos()) > threshold;
	}

	@Override
	public Vec3d getWindVector() {
		return prevWindVector;
	}
}