package colossalrenders.globalwind.mixin.client;
import depixelation.gwindlib.WindyWorld;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import colossalrenders.globalwind.ClientWindInterface;
import colossalrenders.globalwind.GlobalWind;
import colossalrenders.globalwind.GlobalWindConstants;
import colossalrenders.globalwind.WindCalculator;

import java.util.Optional;

@Mixin(ClientWorld.class)
public class ClientWorldMixin implements ClientWindInterface{
    private int windLevel;

    @Override
    public int getWindLevel() {
        if(!((World) (Object) this).getRegistryKey().equals(World.OVERWORLD)) return 0;
        return windLevel;
    }

    @Inject(at = @At("RETURN"), method = "tickTime")
	private void tickInjection(CallbackInfo info){
        Optional<Vec3d> wind = ((WindyWorld) (Object) this).getWind();
        wind.ifPresent(windVector -> windLevel = WindCalculator.calculateWindLevel(windVector.length()));
	}
}
