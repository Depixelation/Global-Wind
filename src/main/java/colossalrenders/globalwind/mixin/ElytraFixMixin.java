package colossalrenders.globalwind.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import colossalrenders.globalwind.GlobalWind;
import colossalrenders.globalwind.WindInterface;

@Mixin(LivingEntity.class)
public class ElytraFixMixin{
	@ModifyExpressionValue(method = "travel(Lnet/minecraft/util/math/Vec3d;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getVelocity()Lnet/minecraft/util/math/Vec3d;", ordinal = 10))
	private Vec3d init(Vec3d vec) {
        return addWind(vec);
	}

    @ModifyExpressionValue(method = "travel(Lnet/minecraft/util/math/Vec3d;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getVelocity()Lnet/minecraft/util/math/Vec3d;", ordinal = 11))
	private Vec3d init2(Vec3d vec) {
        return addWind(vec);
	}

    private Vec3d addWind(Vec3d vec){
        if(!((LivingEntity) (Object) this).getWorld().isClient()){
            Vec3d windVector = ((WindInterface) (Object) ((LivingEntity) (Object) this).getWorld()).getWind();
            if(windVector != null){ 
                GlobalWind.LOGGER.info("subtracting wind vector");
                return vec.subtract(windVector);
            }
            else GlobalWind.LOGGER.info("wind null");
        }

        return vec;
    }
}