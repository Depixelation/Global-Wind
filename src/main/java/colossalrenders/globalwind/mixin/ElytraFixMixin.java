package colossalrenders.globalwind.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import colossalrenders.globalwind.GlobalWind;
import colossalrenders.globalwind.WindInterface;

@Mixin(LivingEntity.class)
public class ElytraFixMixin{
	@ModifyExpressionValue(method = "travel(Lnet/minecraft/world/phys/Vec3;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDeltaMovement()Lnet/minecraft/world/phys/Vec3;", ordinal = 10))
	private Vec3 init(Vec3 vec) {
        return addWind(vec);
	}

    @ModifyExpressionValue(method = "travel(Lnet/minecraft/world/phys/Vec3;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDeltaMovement()Lnet/minecraft/world/phys/Vec3;", ordinal = 11))
	private Vec3 init2(Vec3 vec) {
        return addWind(vec);
	}

    private Vec3 addWind(Vec3 vec){
        if(!((LivingEntity) (Object) this).level().isClientSide()){
            Vec3 windVector = ((WindInterface) (Object) ((LivingEntity) (Object) this).level()).global_Wind$getWind();
            if(windVector != null){ 
                GlobalWind.LOGGER.info("subtracting wind vector");
                return vec.subtract(windVector);
            }
            else GlobalWind.LOGGER.info("wind null");
        }

        return vec;
    }
}