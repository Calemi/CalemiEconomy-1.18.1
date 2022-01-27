package com.tm.calemieconomy.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class CEConfig {

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

    public static final CategoryWorldGen worldGen = new CategoryWorldGen(COMMON_BUILDER);

    public static void init() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_BUILDER.build());
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_BUILDER.build());
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_BUILDER.build());
    }

    public static class CategoryWorldGen {

        public final ForgeConfigSpec.ConfigValue<Boolean> raritaniumOreGen;
        public final ForgeConfigSpec.ConfigValue<Integer> raritaniumVeinsPerChunk;
        public final ForgeConfigSpec.ConfigValue<Integer> raritaniumVeinSize;
        public final ForgeConfigSpec.ConfigValue<Integer> raritaniumOreGenMinY;
        public final ForgeConfigSpec.ConfigValue<Integer> raritaniumOreGenMaxY;

        public CategoryWorldGen (ForgeConfigSpec.Builder builder) {

            builder.push("WorldGen");

            raritaniumOreGen = builder.comment("Raritanium Ore Gen").define("raritaniumOreGen", true);
            raritaniumVeinsPerChunk = builder.comment("Raritanium Veins Per Chunk").define("raritaniumOreVeinsPerChunk", 16);
            raritaniumVeinSize = builder.comment("Raritanium Vein Size").define("raritaniumVeinSize", 8);
            raritaniumOreGenMinY = builder.comment("Raritanium Ore Min Y").define("raritaniumOreGenMinY", -60);
            raritaniumOreGenMaxY = builder.comment("Raritanium Ore Max Y").define("raritaniumOreGenMaxY", 30);
            builder.pop();
        }
    }
}