package colossalrenders.globalwind;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class WindSoundManager {
    private int countdown;
    private final SoundEvent IN, LOOP, OUT;
    private int length, threshold, lastWindLevel;
    private boolean isPlaying;

    private WindSoundManager(SoundEvent in, SoundEvent loop, SoundEvent out){
        IN = in;
        LOOP = loop;
        OUT = out;
        length = 20 * 20 - 1;
        threshold = 0;
    }

    public static WindSoundManager of(SoundEvent in, SoundEvent loop, SoundEvent out){
        return new WindSoundManager(in, loop, out);
    }

    public WindSoundManager ofLength(int length){
        this.length = length;
        return this;
    }

    public WindSoundManager ofThreshold(int threshold){
        this.threshold = threshold;
        return this;
    }

    public int getThreshold(){
        return threshold;
    }

    public void tick(ClientLevel world, LocalPlayer player){
        
        if(countdown == 0 && isPlaying){
            isPlaying = playNext(world, player);
            if(isPlaying) countdown = length;
        }
        if(countdown > 0){
            countdown --;
        }
    }

    public boolean isPlaying(){
        return isPlaying;
    }

    public void startPlaying(){
        isPlaying = true;
        GlobalWind.LOGGER.info("Started playing {}", IN);
    }

    /**
     * 
     * @param world
     * @param player
     * @return if there is a next to play
     */
    private boolean playNext(ClientLevel world, LocalPlayer player){
        int windLevel = ((ClientWindInterface) (Object) world).global_Wind$getWindLevel();
        if(!((PlayerInterface) (Object) player).global_Wind$isOutside(0)) windLevel = 0;

        if(windLevel >= threshold){
            if(lastWindLevel >= threshold){
                world.playPlayerSound(LOOP, SoundSource.AMBIENT, 1.0f, 1.0f);
            }else{
                world.playPlayerSound(IN, SoundSource.AMBIENT, 1.0f, 1.0f);
            }
        }else{
            if(lastWindLevel >= threshold){
                world.playPlayerSound(OUT, SoundSource.AMBIENT, 1.0f, 1.0f);
            }else{
                return false;
            }
        }

        lastWindLevel = windLevel;

        return true;
    }
}
