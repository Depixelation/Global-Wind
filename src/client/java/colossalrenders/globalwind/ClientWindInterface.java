package colossalrenders.globalwind;

import net.minecraft.world.phys.Vec3;

public interface ClientWindInterface {
    public int global_Wind$getWindLevel();
    public Vec3 global_Wind$getWind();
    public void global_Wind$setWindSeed(long seed);
    public void global_Wind$setWindStrength(double strength);
}
