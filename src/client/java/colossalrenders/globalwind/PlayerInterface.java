package colossalrenders.globalwind;

import net.minecraft.util.math.Vec3d;

public interface PlayerInterface {
    public boolean wasOutside();
    public void setOutside(Boolean outside);
    public boolean isOutside(int threshold);
    public Vec3d getWindVector();
}
