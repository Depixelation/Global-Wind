package colossalrenders.globalwind.mixin.client;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import colossalrenders.globalwind.ClientWindInterface;
import colossalrenders.globalwind.GlobalWindConstants;
import colossalrenders.globalwind.WindCalculator;

@Mixin(ClientWorld.class)
public class ClientWorldMixin implements ClientWindInterface{
    private int windLevel;
    private Vec3d wind;
    private long windSeed;
    @Override
    public int getWindLevel() {
        if(!((World) (Object) this).getRegistryKey().equals(World.OVERWORLD)) return 0;
        return windLevel;
    }

    @Inject(at = @At("RETURN"), method = "tickTime")
	private void tickInjection(CallbackInfo info){
        long t = ((ClientWorld) (Object) this).getTimeOfDay();
        
        wind = WindCalculator.calculateWind(t, windSeed, GlobalWindConstants.SQUASH, ((ClientWorld) (Object) this).isRaining(), ((ClientWorld) (Object) this).isThundering(), GlobalWindConstants.WIND_MULT);
        windLevel = WindCalculator.calculateWindLevel(wind.length());
	}

    @Override
    public Vec3d getWind() {
        return wind;
    }

    @Override
    public void setWindSeed(long seed) {
        windSeed = seed;
    }
	
}
