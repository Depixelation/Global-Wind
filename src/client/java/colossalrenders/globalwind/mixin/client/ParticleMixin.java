package colossalrenders.globalwind.mixin.client;
import depixelation.gwindlib.WindyWorld;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
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

import java.util.Optional;

@Mixin(Particle.class)
public class ParticleMixin{
    @Final
    @Shadow
    protected ClientWorld world;

    @Unique
    private Vec3d prevWindVector;

    @Inject(at = @At("HEAD"), method = "move(DDD)V")
	private void tickInjection(double dx, double dy, double dz, CallbackInfo info){

        if(prevWindVector == null) prevWindVector = Vec3d.ZERO;

        if(((ClientWindInterface) (Object) world).getWindLevel() > 0){
            Optional<Vec3d> wind = ((WindyWorld) world).getWind();
            if(wind.isEmpty()) return;

            Vec3d windVector = (wind.get().multiply(2.0)).multiply(0.3);

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
