package colossalrenders.globalwind.mixin;

import depixelation.gwindlib.WindyWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import colossalrenders.globalwind.GlobalWind;
import colossalrenders.globalwind.GlobalWindConstants;
import colossalrenders.globalwind.WindCalculator;
import colossalrenders.globalwind.WindInterface;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity{
    public LivingEntityMixin(EntityType<?> type, World world){
            super(type, world);
    }

    @ModifyArgs(method = "travel(Lnet/minecraft/util/math/Vec3d;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(DDD)V", ordinal = 3))
    private void modifyDrag(Args args){
        if(!((LivingEntity) (Object) this instanceof PlayerEntity)){
            LivingEntity currentEntity = (LivingEntity) (Object) this;
            BlockPos blockPos = this.getVelocityAffectingPos();
            float p = currentEntity.getWorld().getBlockState(blockPos).getBlock().getSlipperiness();
            double f = currentEntity.isOnGround() ? p * 0.91F : 0.91F;

            double x = (double) args.get(0)/f;
            double z = (double) args.get(2)/f;

            if(currentEntity.getWorld() instanceof ServerWorld && WindCalculator.isOutside(currentEntity, 13)){
                Optional<Vec3d> wind = ((WindyWorld) currentEntity.getWorld()).getWind();
                if(wind.isEmpty()) return;
                Vec3d windVector = wind.get();

                if(windVector == null) return;
                double windSlipperiness = ((p-0.55) * (1/(1-0.55)));
                //GlobalWind.LOGGER.info("" + windVector.length());
                if((windVector.length() < GlobalWindConstants.MAX_WIND_SPEED_BEFORE_SLIPPING && p <= 0.8)) windSlipperiness = 0;
                if(currentEntity.isOnGround()) windVector = windVector.multiply(windSlipperiness);
                args.set(0, x + (1-f) * (windVector.x - x));
                args.set(2, z + (1-f) * (windVector.z - z));
                //args.set(0, x * f);
                //args.set(2, z * f);
            }
        }
    }
}