package colossalrenders.globalwind;

import net.minecraft.util.math.Vec3d;

public interface ClientWindInterface {
    public int global_Wind$getWindLevel();
    public Vec3d global_Wind$getWind();
    public void global_Wind$setWindSeed(long seed);
    public void global_Wind$setWindStrength(double strength);
}
