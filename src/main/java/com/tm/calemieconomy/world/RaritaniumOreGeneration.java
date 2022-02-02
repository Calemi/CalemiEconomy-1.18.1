package com.tm.calemieconomy.world;

import com.tm.calemieconomy.config.CEConfig;
import com.tm.calemieconomy.init.InitItems;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class RaritaniumOreGeneration {

    private static final ArrayList<PlacedFeature> overworldOres = new ArrayList<>();

    public static void init() {

        if (CEConfig.worldGen.raritaniumOreGen.get()) {

            registerOre("raritanium_ore", InitItems.RARITANIUM_ORE.get(), InitItems.RARITANIUM_ORE_DEEPSLATE.get(),
                    CEConfig.worldGen.raritaniumVeinSize.get(),
                    CEConfig.worldGen.raritaniumVeinsPerChunk.get(),
                    CEConfig.worldGen.raritaniumOreGenMinY.get(),
                    CEConfig.worldGen.raritaniumOreGenMaxY.get());
        }
    }

    private static void registerOre(String name, Block oreBlock, Block deepslateBlock, int veinSize, int veinsPerChunk, int minY, int maxY) {

        List<OreConfiguration.TargetBlockState> ORE_TARGET_LIST = List.of(
                OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, oreBlock.defaultBlockState()),
                OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, deepslateBlock.defaultBlockState()));

        ConfiguredFeature<?, ?> ORE_FEATURE = FeatureUtils
                .register(name, Feature.ORE.configured(new OreConfiguration(ORE_TARGET_LIST, veinSize)));

        List<PlacementModifier> placementModifiers = List.of(
                CountPlacement.of(veinsPerChunk),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(minY), VerticalAnchor.absolute(maxY)), BiomeFilter.biome());

        PlacedFeature ORE_PLACED = PlacementUtils.register(name, ORE_FEATURE.placed(placementModifiers));

        overworldOres.add(ORE_PLACED);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void generateOres(BiomeLoadingEvent event) {

        BiomeGenerationSettingsBuilder generation = event.getGeneration();

        if (!event.getCategory().equals(Biome.BiomeCategory.NETHER) && !event.getCategory().equals(Biome.BiomeCategory.THEEND)) {

            for (PlacedFeature ore : overworldOres) {
                if (ore != null) event.getGeneration().getFeatures(GenerationStep.Decoration.UNDERGROUND_ORES).add(() -> ore);
            }
        }
    }
}