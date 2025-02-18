package colossalrenders.globalwind.mixin;

import depixelation.gwindlib.WindyWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import colossalrenders.globalwind.GlobalWindConstants;
import colossalrenders.globalwind.WindCalculator;
import colossalrenders.globalwind.WindInterface;

import java.util.Optional;

@Mixin(Entity.class)
public class EntityMixin{
    Vec3d prevWindVelocity;
	
	@Inject(at = @At("RETURN"), method = "tick")
	private void init(CallbackInfo info) {
        Entity currentEntity = (Entity) (Object) this;
        BlockPos entityPos = currentEntity.getBlockPos();
        boolean isOutside = currentEntity.getWorld().getLightLevel(LightType.SKY, entityPos) > 13;
        //GlobalWind.LOGGER.info("entityMixin");
		if(!currentEntity.getWorld().isClient() && !currentEntity.isInsideWaterOrBubbleColumn() && !(currentEntity instanceof PlayerEntity)  && !(currentEntity instanceof LivingEntity)){
            if(!isOutside) return;

            Optional<Vec3d> wind = ((WindyWorld) currentEntity.getWorld()).getWind();
            if(wind.isEmpty()) return;

            Vec3d windVelocity = wind.get();
            if(windVelocity.length() < GlobalWindConstants.MIN_WIND_SPEED_FOR_MOVEMENT) return;
            if(!currentEntity.isOnGround() && prevWindVelocity != null){

                if(prevWindVelocity.length() < windVelocity.length()){
                    prevWindVelocity = WindCalculator.calculateWindVector(prevWindVelocity, windVelocity);
                }
                ArrowEntity e;
                currentEntity.getServer().execute(() -> {
                    currentEntity.addVelocity(prevWindVelocity);
                    currentEntity.velocityModified = true;
                });

            }else if(prevWindVelocity == null){
                prevWindVelocity = Vec3d.ZERO;
            }else if(currentEntity.isOnGround()){
                prevWindVelocity = Vec3d.ZERO;
                if(windVelocity.length() > GlobalWindConstants.MAX_WIND_SPEED_BEFORE_SLIPPING){

                    prevWindVelocity = windVelocity.multiply(GlobalWindConstants.SLIPPING_MULTIPLIER);

                    currentEntity.getServer().execute(() -> {
                        currentEntity.addVelocity(prevWindVelocity);
                        currentEntity.velocityModified = true;
                    });
                }
            }
        }
	}
}