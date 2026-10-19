package colossalrenders.globalwind.mixin.client;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import colossalrenders.globalwind.ClientWindInterface;
import colossalrenders.globalwind.GlobalWindConstants;
import colossalrenders.globalwind.WindCalculator;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@Mixin(ClientLevel.class)
public class ClientWorldMixin implements ClientWindInterface{
    @Unique
    private int windLevel;
    @Unique
    private Vec3 wind;
    @Unique
    private long windSeed;
    @Unique
    private double windStrength;
    @Override
    public int global_Wind$getWindLevel() {
        if(!((Level) (Object) this).dimension().equals(Level.OVERWORLD)) return 0;
        return windLevel;
    }

    @Inject(at = @At("RETURN"), method = "tickTime")
	private void tickInjection(CallbackInfo info){
        long t = ((ClientLevel) (Object) this).getDayTime();
        
        wind = WindCalculator.calculateWind(t, windSeed, GlobalWindConstants.SQUASH, ((ClientLevel) (Object) this).isRaining(), ((ClientLevel) (Object) this).isThundering(), GlobalWindConstants.WIND_MULT, windStrength);
        windLevel = WindCalculator.calculateWindLevel(wind.length());
	}

    @Override
    public Vec3 global_Wind$getWind() {
        return wind;
    }

    @Override
    public void global_Wind$setWindSeed(long seed) {
        windSeed = seed;
    }

    @Override
    public void global_Wind$setWindStrength(double strength) {windStrength = strength;}
}
