package colossalrenders.globalwind;


import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;

public final class WindCalculator {
    private WindCalculator(){
        throw new UnsupportedOperationException();
    }

    public static int calculateWindLevel(double windMagnitude){
        //GlobalWind.LOGGER.info("{}", windMagnitude);
        if(windMagnitude > GlobalWindConstants.WIND_LVL_1){
			//GlobalWind.LOGGER.info("1");
			if(windMagnitude > GlobalWindConstants.WIND_LVL_2){
				//GlobalWind.LOGGER.info("2");
				return 2;
			}
			return 1;
		}else{
			return 0;
		}
    }

	public static double calculateKph(Vec3d wind){
		return (wind.length() * 20 * 60 * 60)/1000;
	}

    public static Vec3d calculateWindVector(Vec3d prevWindVelocity, Vec3d windVelocity){
        return prevWindVelocity.add(windVelocity.multiply(0.5 * ((windVelocity.length() - prevWindVelocity.length())/windVelocity.length())));
    }

	public static boolean isOutside(Entity entity, int threshold){
		return entity.getWorld().getLightLevel(LightType.SKY, entity.getBlockPos()) > threshold;
	}
}
