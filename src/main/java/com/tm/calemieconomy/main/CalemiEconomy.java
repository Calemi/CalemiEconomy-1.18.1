package com.tm.calemieconomy.main;

import com.tm.calemieconomy.config.CEConfig;
import com.tm.calemieconomy.init.InitItems;
import com.tm.calemieconomy.tab.CETab;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * The main class for Calemi's Economy
 */
@Mod(CEReference.MOD_ID)
public class CalemiEconomy {

    /**
     * A reference to the instance of the mod.
     */
    public static CalemiEconomy instance;

    /**
     * Used to register the client and common setup methods.
     */
    public static IEventBus MOD_EVENT_BUS;

    public static final CreativeModeTab TAB = new CETab();

    /**
     * Everything starts here.
     */
    public CalemiEconomy() {

        //Initializes the instance.
        instance = this;

        MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        MOD_EVENT_BUS.addListener(this::onClientSetup);

        InitItems.init();
        CEConfig.init();
    }

    private void onClientSetup(final FMLClientSetupEvent event) {

    }
}
