package com.tm.calemieconomy.init;

import com.tm.calemicore.util.helper.WorldGenHelper;
import com.tm.calemieconomy.config.CEConfig;

public class InitOres {

    public static void init() {

        if (CEConfig.worldGen.raritaniumOreGen.get()) {

            WorldGenHelper.registerOre("raritanium_ore", InitItems.RARITANIUM_ORE.get(), InitItems.RARITANIUM_ORE_DEEPSLATE.get(),
                    CEConfig.worldGen.raritaniumVeinSize.get(),
                    CEConfig.worldGen.raritaniumVeinsPerChunk.get(),
                    CEConfig.worldGen.raritaniumOreGenMinY.get(),
                    CEConfig.worldGen.raritaniumOreGenMaxY.get(),
                    WorldGenHelper.oresOverworld);
        }
    }
}
