package com.tm.calemieconomy.main;

import com.tm.calemieconomy.config.CEConfig;
import com.tm.calemieconomy.event.CoinPickupSoundEvent;
import com.tm.calemieconomy.event.SecurityEvents;
import com.tm.calemieconomy.event.WrenchEvents;
import com.tm.calemieconomy.event.WrenchLoreEvent;
import com.tm.calemieconomy.init.InitBlockEntityTypes;
import com.tm.calemieconomy.init.InitItems;
import com.tm.calemieconomy.init.InitMenuTypes;
import com.tm.calemieconomy.init.InitSounds;
import com.tm.calemieconomy.packet.CEPacketHandler;
import com.tm.calemieconomy.screen.ScreenBank;
import com.tm.calemieconomy.screen.ScreenTradingPost;
import com.tm.calemieconomy.tab.CETab;
import com.tm.calemieconomy.world.RaritaniumOreGeneration;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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
        MOD_EVENT_BUS.addListener(this::onCommonSetup);
        MOD_EVENT_BUS.addListener(this::onClientSetup);

        InitSounds.SOUNDS.register(MOD_EVENT_BUS);
        InitItems.init();
        InitBlockEntityTypes.BLOCK_ENTITY_TYPES.register(MOD_EVENT_BUS);
        InitMenuTypes.MENU_TYPES.register(MOD_EVENT_BUS);
        CEConfig.init();
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        RaritaniumOreGeneration.init();
        CEPacketHandler.init();
        MinecraftForge.EVENT_BUS.register(new WrenchEvents());
        MinecraftForge.EVENT_BUS.register(new SecurityEvents());
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new CoinPickupSoundEvent());
        MinecraftForge.EVENT_BUS.register(new WrenchLoreEvent());

        MenuScreens.register(InitMenuTypes.BANK.get(), ScreenBank::new);
        MenuScreens.register(InitMenuTypes.TRADING_POST.get(), ScreenTradingPost::new);
    }
}
