package colossalrenders.globalwind.mixin;

import depixelation.gwindlib.WindyWorld;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import java.util.Iterator;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import colossalrenders.globalwind.GlobalWind;
import colossalrenders.globalwind.GlobalWindConstants;
import colossalrenders.globalwind.WindCalculator;
import colossalrenders.globalwind.WindInterface;

@Mixin(ServerWorld.class)
public class ServerWorldMixin implements WindInterface{
	@Override
	public int getWindLevel(){
		Optional<Vec3d> wind = ((WindyWorld) this).getWind();
		if(wind.isEmpty()) return 0;

		double windSpeed = wind.get().length();
		return WindCalculator.calculateWindLevel(windSpeed);
	}
}