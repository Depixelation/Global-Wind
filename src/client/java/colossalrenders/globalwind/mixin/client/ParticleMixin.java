package colossalrenders.globalwind.mixin.client;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import colossalrenders.globalwind.ClientWindInterface;
import colossalrenders.globalwind.WindCalculator;

@Mixin(Particle.class)
public class ParticleMixin{
    @Final
    @Shadow
    protected ClientLevel level;
    protected boolean shouldApplyWind;

    @Unique
    private Vec3 prevWindVector;

    @Inject(at = @At("HEAD"), method = "move(DDD)V")
	private void tickInjection(double dx, double dy, double dz, CallbackInfo info){

        if(prevWindVector == null) prevWindVector = Vec3.ZERO;

        if(((ClientWindInterface) (Object) level).global_Wind$getWindLevel() > 0){
            Vec3 windVector = ((ClientWindInterface) (Object) level).global_Wind$getWind().scale(2.0);

            if(prevWindVector.length() < windVector.length()){
                prevWindVector = WindCalculator.calculateWindVector(prevWindVector, windVector);
            }
        }
	}

    @ModifyVariable(method = "move(DDD)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private double addWindDx(double dx){
        if(prevWindVector == null) return dx;
        return dx + prevWindVector.x;
    }

    @ModifyVariable(method = "move(DDD)V", at = @At("HEAD"), ordinal = 2, argsOnly = true)
    private double addWindDz(double dz){
        if(prevWindVector == null) return dz;
        return dz + prevWindVector.z;
    }
}
