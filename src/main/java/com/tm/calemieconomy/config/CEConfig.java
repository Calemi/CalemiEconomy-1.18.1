package com.tm.calemieconomy.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class CEConfig {

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

    public static final CategoryWorldGen worldGen = new CategoryWorldGen(COMMON_BUILDER);
    public static final CategoryEconomy economy = new CategoryEconomy(SERVER_BUILDER);
    public static final CategorySecurity security = new CategorySecurity(SERVER_BUILDER);

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
            raritaniumVeinsPerChunk = builder.comment("Raritanium Veins Per Chunk").define("raritaniumOreVeinsPerChunk", 8);
            raritaniumVeinSize = builder.comment("Raritanium Vein Size").define("raritaniumVeinSize", 8);
            raritaniumOreGenMinY = builder.comment("Raritanium Ore Min Y").define("raritaniumOreGenMinY", -60);
            raritaniumOreGenMaxY = builder.comment("Raritanium Ore Max Y").define("raritaniumOreGenMaxY", 30);

            builder.pop();
        }
    }

    public static class CategoryEconomy {

        public final ForgeConfigSpec.ConfigValue<Integer> bankCurrencyCapacity;
        public final ForgeConfigSpec.ConfigValue<Integer> cheapMoneyBagMin;
        public final ForgeConfigSpec.ConfigValue<Integer> cheapMoneyBagMax;
        public final ForgeConfigSpec.ConfigValue<Integer> richMoneyBagMin;
        public final ForgeConfigSpec.ConfigValue<Integer> richMoneyBagMax;

        public CategoryEconomy (ForgeConfigSpec.Builder builder) {

            builder.push("Economy");

            bankCurrencyCapacity = builder.comment("Bank Currency Capacity", "The max amount of currency the Bank can store.")
                    .defineInRange("bankCurrencyCapacity", 1000000, 1, 99999999);

            cheapMoneyBagMin = builder.comment("Cheap Money Bag Minimum Coins Amount", "The minimum of the random amount of currency the Cheap Money Bag gives.")
                    .defineInRange("cheapMoneyBagMin", 10, 0, 10000);

            cheapMoneyBagMax = builder.comment("Cheap Money Bag Maximum Coins Amount", "The maximum of the random amount of currency the Cheap Money Bag gives.")
                    .defineInRange("cheapMoneyBagMax", 100, 0, 10000);

            richMoneyBagMin = builder.comment("Rich Money Bag Minimum Coins Amount", "The minimum of the random amount of currency the Rich Money Bag gives.")
                    .defineInRange("richMoneyBagMin", 75, 0, 10000);

            richMoneyBagMax = builder.comment("Rich Money Bag Maximum Coins Amount", "The maximum of the random amount of currency the Rich Money Bag gives.")
                    .defineInRange("richMoneyBagMax", 300, 0, 10000);

            builder.pop();
        }
    }

    public static class CategorySecurity {

        public final ForgeConfigSpec.ConfigValue<Boolean> useSecurity;

        public CategorySecurity (ForgeConfigSpec.Builder builder) {

            builder.push("Security");

            useSecurity = builder.comment("Use Security", "Disable this to allow everyone access to anyone's Blocks.")
                    .define("useSecurity", true);

            builder.pop();
        }
    }
}