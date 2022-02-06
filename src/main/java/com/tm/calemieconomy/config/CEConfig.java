package com.tm.calemieconomy.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class CEConfig {

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

    public static final CategoryWorldGen worldGen = new CategoryWorldGen(COMMON_BUILDER);
    public static final CategoryEconomy economy = new CategoryEconomy(SERVER_BUILDER);
    public static final CategorySecurity security = new CategorySecurity(SERVER_BUILDER);
    public static final CategoryOverlay overlay = new CategoryOverlay(CLIENT_BUILDER);

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

        public final ForgeConfigSpec.ConfigValue<Integer> walletCurrencyCapacity;
        public final ForgeConfigSpec.ConfigValue<Integer> bankCurrencyCapacity;
        public final ForgeConfigSpec.ConfigValue<Boolean> tradingPostBroadcasts;
        public final ForgeConfigSpec.ConfigValue<Integer> tradingPostBroadcastDelay;
        public final ForgeConfigSpec.ConfigValue<Boolean> tradingPostOverlay;
        public final ForgeConfigSpec.ConfigValue<Integer> cheapMoneyBagMin;
        public final ForgeConfigSpec.ConfigValue<Integer> cheapMoneyBagMax;
        public final ForgeConfigSpec.ConfigValue<Integer> richMoneyBagMin;
        public final ForgeConfigSpec.ConfigValue<Integer> richMoneyBagMax;

        public CategoryEconomy (ForgeConfigSpec.Builder builder) {

            builder.push("Economy");

            walletCurrencyCapacity = builder.comment("Wallet Currency Capacity", "The max amount of currency the Wallet can store.")
                    .defineInRange("walletCurrencyCapacity", 1000000, 0, 99999999);

            bankCurrencyCapacity = builder.comment("Bank Currency Capacity", "The max amount of currency the Bank can store.")
                    .defineInRange("bankCurrencyCapacity", 1000000, 1, 99999999);

            tradingPostBroadcasts = builder.comment("Trading Post Broadcasts", "Disable this to disallow Players broadcasting their Trading Posts")
                    .define("tradingPostBroadcasts", true);

            tradingPostBroadcastDelay = builder.comment("Trading Post Broadcast Delay", "The amount of seconds before a Player can broadcasts their Trading Post.")
                    .defineInRange("tradingPostBroadcastDelay", 10, 0, 3600);

            tradingPostOverlay = builder.comment("Trading Post Overlay", "Disable this to stop the screen overlay when selecting Trading Posts.")
                    .define("tradingPostOverlay", true);

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

    public static class CategoryOverlay {

        public final ForgeConfigSpec.ConfigValue<Boolean> walletOverlay;
        public final ForgeConfigSpec.ConfigValue<String> walletOverlayPosition;

        public CategoryOverlay (ForgeConfigSpec.Builder builder) {

            builder.push("Overlay");

            walletOverlay = builder.comment("Render Wallet Currency Overlay", "Enable this render an overlay on your game screen showing your Wallet stats.")
                    .define("walletOverlay", true);

            walletOverlayPosition = builder
                    .comment("Wallet Currency Overlay Position", "The position of the screen of the Wallet overlay", "The valid values are {TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT}")
                    .define("walletOverlayPosition", WalletOverlayPosition.TOP_LEFT.toString());

            builder.pop();
        }
    }

    public enum WalletOverlayPosition {

        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT;

        private static final Map<String, WalletOverlayPosition> NAME_LOOKUP = Arrays.stream(values()).collect(Collectors.toMap(WalletOverlayPosition::toString, (n) -> n));

        WalletOverlayPosition () {}

        @Nullable
        public static WalletOverlayPosition byName (@Nullable String name) {
            return name == null ? null : NAME_LOOKUP.get(name.toUpperCase(Locale.ROOT));
        }
    }
}