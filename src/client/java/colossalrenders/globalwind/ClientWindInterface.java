package colossalrenders.globalwind;

import net.minecraft.util.math.Vec3d;

public interface ClientWindInterface {
    public int getWindLevel();
    public Vec3d getWind();
    public void setWindSeed(long seed);
    public void setWindStrength(double strength);
}
