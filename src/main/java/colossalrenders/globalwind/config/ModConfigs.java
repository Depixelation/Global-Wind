package colossalrenders.globalwind.config;

import com.mojang.datafixers.util.Pair;

import colossalrenders.globalwind.GlobalWind;

public class ModConfigs {
    public static SimpleConfig CONFIG;
    private static ModConfigProvider configs;

    public static double WIND_MULTIPLIER_CONFIG;
    static public final String WIND_MULTIPLIER_CONFIG_KEY = "wind-multiplier";

    public static void registerConfigs() {
        configs = new ModConfigProvider();
        createConfigs();

        CONFIG = SimpleConfig.of(GlobalWind.MOD_ID + "config").provider(configs).request();

        assignConfigs();
    }

    private static void createConfigs() {
        configs.addKeyValuePair(new Pair<>(WIND_MULTIPLIER_CONFIG_KEY, 1.0), "global multiplier for wind");
    }

    private static void assignConfigs() {
       
        WIND_MULTIPLIER_CONFIG = CONFIG.getOrDefault(WIND_MULTIPLIER_CONFIG_KEY, 1.0);

        System.out.println("All " + configs.getConfigsList().size() + " have been set properly");
    }
}
