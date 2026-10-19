package colossalrenders.globalwind;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class WindCalculator {
    private WindCalculator(){
        throw new UnsupportedOperationException();
    }

    public static Vec3 calculateWind(long t, long seed, int SQUASH, boolean isRaining, boolean isThundering, double WIND_MULTIPLIER, double WIND_MULTIPLIER_CONFIG){
        double x = Math.sin(2 * (((double) t - seed)/SQUASH)) + Math.sin(Math.PI * (((double) t - seed)/SQUASH));
		double z = Math.sin(2 * (((double) -t + seed)/SQUASH)) + Math.sin(Math.PI * (((double) -t + seed)/SQUASH) + 10);
		double a = Math.sin(2 * (((double) (t - seed)*2)/SQUASH) + 1) + Math.sin(Math.PI * (((double) (-t + seed)*2)/SQUASH) + 1);
		double gusts = Math.sin(2 * (((double) t*2)/20) + 1) + Math.sin(Math.PI * (((double) -t*2)/20) + 1);

		double factor = isRaining ? isThundering ? 1.0 : 0.5 : 0.0;
		
		double c = a * gusts;
		double d = Mth.lerp(0.5 * factor, a, c);
		double multiplier = (0.5 + 0.5 * factor) * d + 4 * factor * factor;

		Vec3 windVector = new Vec3(x, 0, z);
		windVector = windVector.scale(Math.max(multiplier, 0));

        return windVector.scale(WIND_MULTIPLIER).scale(WIND_MULTIPLIER_CONFIG);
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

    public static Vec3 calculateWindVector(Vec3 prevWindVelocity, Vec3 windVelocity){
        return prevWindVelocity.add(windVelocity.scale(0.5 * ((windVelocity.length() - prevWindVelocity.length())/windVelocity.length())));
    }
}
