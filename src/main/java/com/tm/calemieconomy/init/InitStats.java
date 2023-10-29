package com.tm.calemieconomy.init;

import com.tm.calemicore.main.CCReference;
import com.tm.calemieconomy.main.CEReference;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class InitStats {

    public static final ResourceKey<Registry<ResourceLocation>> STAT_KEY = ResourceKey.createRegistryKey(new ResourceLocation("custom_stat"));

    public static final DeferredRegister<StatType<?>> STAT_TYPES = DeferredRegister.create(ForgeRegistries.STAT_TYPES, CEReference.MOD_ID);
    public static final DeferredRegister<ResourceLocation> STATS = DeferredRegister.create(STAT_KEY, CEReference.MOD_ID);

    public static final RegistryObject<StatType<Item>> ITEM_SOLD = STAT_TYPES.register("item_sold", () -> new StatType<>(Registry.ITEM));
    public static final RegistryObject<StatType<Item>> ITEM_BOUGHT = STAT_TYPES.register("item_bought", () -> new StatType<>(Registry.ITEM));

    public static void init() {

        for (CustomStats stat : CustomStats.values()) {

            ResourceLocation registryName = stat.getRegistryName();
            Registry.register(Registry.CUSTOM_STAT, registryName.getPath(), registryName);
            Stats.CUSTOM.get(registryName, StatFormatter.DEFAULT);
        }
    }

    public enum CustomStats {

        TOTAL_SOLD("total_sold"),
        TOTAL_BOUGHT("total_bought");

        private final ResourceLocation registryName;

        CustomStats(String id) {
            this.registryName = new ResourceLocation(CCReference.MOD_ID, id);
        }

        public void addToPlayer(Player player, int amount) {
            player.awardStat(this.registryName, amount);
        }

        public ResourceLocation getRegistryName() {
            return registryName;
        }
    }
}