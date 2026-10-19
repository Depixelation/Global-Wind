package colossalrenders.globalwind.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import colossalrenders.globalwind.GlobalWindConstants;
import colossalrenders.globalwind.WindCalculator;
import colossalrenders.globalwind.WindInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

@Mixin(Entity.class)
public class EntityMixin{
    @Unique
    Vec3 prevWindVelocity;
	
	@Inject(at = @At("RETURN"), method = "tick")
	private void init(CallbackInfo info) {
        Entity currentEntity = (Entity) (Object) this;
        BlockPos entityPos = currentEntity.blockPosition();
        boolean isOutside = currentEntity.level().getBrightness(LightLayer.SKY, entityPos) > 13;
        //GlobalWind.LOGGER.info("entityMixin");
		if(!currentEntity.level().isClientSide() && !currentEntity.isInWater() && !(currentEntity instanceof Player)){
            if(!isOutside) return;
            if(currentEntity instanceof LivingEntity e && e.onClimbable()) return;
            
            Vec3 windVelocity = ((WindInterface) currentEntity.level()).global_Wind$getWind();
            if(windVelocity.length() < GlobalWindConstants.MIN_WIND_SPEED_FOR_MOVEMENT) return;
            if(!currentEntity.onGround() && prevWindVelocity != null){

                if(prevWindVelocity.length() < windVelocity.length()){
                    prevWindVelocity = WindCalculator.calculateWindVector(prevWindVelocity, windVelocity);
                }

                currentEntity.level().getServer().execute(() -> {
                    currentEntity.push(prevWindVelocity);
                    currentEntity.needsSync = true;
                });

            }else if(prevWindVelocity == null){
                prevWindVelocity = Vec3.ZERO;
            }else if(currentEntity.onGround()){
                prevWindVelocity = Vec3.ZERO;
                if(windVelocity.length() > GlobalWindConstants.MAX_WIND_SPEED_BEFORE_SLIPPING){

                    prevWindVelocity = windVelocity.scale(GlobalWindConstants.SLIPPING_MULTIPLIER);

                    currentEntity.level().getServer().execute(() -> {
                        currentEntity.push(prevWindVelocity);
                        currentEntity.needsSync = true;
                    });
                }
            }
        }
	}
}