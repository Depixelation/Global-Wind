package colossalrenders.globalwind;

import colossalrenders.globalwind.config.ModConfigs;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;

public final class WindCalculator {
    private WindCalculator(){
        throw new UnsupportedOperationException();
    }

    public static Vec3d calculateWind(long t, long seed, int SQUASH, boolean isRaining, boolean isThundering, double WIND_MULTIPLIER){
        double x = Math.sin(2 * (((double) t - seed)/SQUASH)) + Math.sin(Math.PI * (((double) t - seed)/SQUASH));
		double z = Math.sin(2 * (((double) -t + seed)/SQUASH)) + Math.sin(Math.PI * (((double) -t + seed)/SQUASH) + 10);
		double a = Math.sin(2 * (((double) (t - seed)*2)/SQUASH) + 1) + Math.sin(Math.PI * (((double) (-t + seed)*2)/SQUASH) + 1);
		double gusts = Math.sin(2 * (((double) t*2)/20) + 1) + Math.sin(Math.PI * (((double) -t*2)/20) + 1);

		double factor = isRaining ? isThundering ? 1.0 : 0.5 : 0.0;
		
		double c = a * gusts;
		double d = MathHelper.lerp(0.5 * factor, a, c);
		double multiplier = (0.5 + 0.5 * factor) * d + 4 * factor * factor;

		Vec3d windVector = new Vec3d(x, 0, z);
		windVector = windVector.multiply(Math.max(multiplier, 0));

        return windVector.multiply(WIND_MULTIPLIER).multiply(ModConfigs.WIND_MULTIPLIER_CONFIG);
    }

    public static int calculateWindLevel(double windMagnitude){
        if(windMagnitude > GlobalWindConstants.WIND_LVL_1){
			if(windMagnitude > GlobalWindConstants.WIND_LVL_2){
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
