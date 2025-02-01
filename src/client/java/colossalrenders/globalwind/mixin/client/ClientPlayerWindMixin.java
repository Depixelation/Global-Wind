package colossalrenders.globalwind.mixin.client;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import colossalrenders.globalwind.ClientWindInterface;
import colossalrenders.globalwind.GlobalWind;
import colossalrenders.globalwind.GlobalWindConstants;
import colossalrenders.globalwind.PlayerInterface;
import colossalrenders.globalwind.WindCalculator;

@Mixin(LivingEntity.class)
public abstract class ClientPlayerWindMixin extends Entity{
    /*@ModifyExpressionValue(method = "travel(Lnet/minecraft/util/math/Vec3d;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getVelocity()Lnet/minecraft/util/math/Vec3d;", ordinal = 10))
    private Vec3d init(Vec3d vec) {
        return subtractWind(vec);
        }

        @ModifyExpressionValue(method = "travel(Lnet/minecraft/util/math/Vec3d;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getVelocity()Lnet/minecraft/util/math/Vec3d;", ordinal = 11))
    private Vec3d init2(Vec3d vec) {
        return subtractWind(vec);
        } /* */
        public ClientPlayerWindMixin(EntityType<?> type, World world){
                super(type, world);
        }

    @ModifyArgs(method = "travel(Lnet/minecraft/util/math/Vec3d;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(DDD)V", ordinal = 3))
    private void modifyDrag(Args args){
        if((LivingEntity) (Object) this instanceof ClientPlayerEntity player){
            BlockPos blockPos = this.getVelocityAffectingPos();
            float p = ((LivingEntity)player).getWorld().getBlockState(blockPos).getBlock().getSlipperiness();
            double f = player.isOnGround() ? p * 0.91F : 0.91F;

            double x = (double) args.get(0)/f;
            double z = (double) args.get(2)/f;

            if(player.getWorld() instanceof ClientWorld && ((PlayerInterface) (Object) player).isOutside(13)){
                Vec3d windVector = ((ClientWindInterface) (Object) player.getWorld()).getWind();
                if(windVector == null) return;
                double windSlipperiness = ((p-0.55) * (1/(1-0.55)));
                if((windVector.length() < GlobalWindConstants.MAX_WIND_SPEED_BEFORE_SLIPPING && p <= 0.8)) windSlipperiness = 0;
                windVector = windVector.multiply(10);
                GlobalWind.LOGGER.info("Wind Velocity " + WindCalculator.calculateKph(windVector) + "kph");
                if(player.isOnGround()) windVector = windVector.multiply(windSlipperiness);
                args.set(0, x + (1-f) * (windVector.x - x));
                args.set(2, z + (1-f) * (windVector.z - z));
                //args.set(0, x * f);
                //args.set(2, z * f);
            }
        }
    }
}