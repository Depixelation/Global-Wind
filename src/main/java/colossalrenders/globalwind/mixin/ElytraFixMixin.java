package colossalrenders.globalwind.mixin;

import depixelation.gwindlib.WindyWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import colossalrenders.globalwind.GlobalWind;
import colossalrenders.globalwind.WindInterface;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class ElytraFixMixin{
	@ModifyExpressionValue(method = "travel(Lnet/minecraft/util/math/Vec3d;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getVelocity()Lnet/minecraft/util/math/Vec3d;", ordinal = 10))
	private Vec3d init(Vec3d vec) {
        return addWind(vec);
	}

    @ModifyExpressionValue(method = "travel(Lnet/minecraft/util/math/Vec3d;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getVelocity()Lnet/minecraft/util/math/Vec3d;", ordinal = 11))
	private Vec3d init2(Vec3d vec) {
        return addWind(vec);
	}

    private Vec3d addWind(Vec3d vec){
        if(((LivingEntity) (Object) this).getWorld() instanceof ServerWorld world && !world.isClient()){
            Optional<Vec3d> wind = ((WindyWorld) world).getWind();
            if(wind.isEmpty()) {
                GlobalWind.LOGGER.info("Wind null");
                return vec;
            }
            Vec3d windVector = wind.get();
            if(windVector != null){ 
                GlobalWind.LOGGER.info("subtracting wind vector");
                return vec.subtract(windVector);
            }
        }

        return vec;
    }
}