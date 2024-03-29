package com.tm.calemieconomy.main;

import com.tm.calemieconomy.client.render.RenderTradingPost;
import com.tm.calemieconomy.command.EconomyCommand;
import com.tm.calemieconomy.config.CEConfig;
import com.tm.calemieconomy.event.ScheduledRandomPriceModifierHandler;
import com.tm.calemieconomy.event.TradingPostsCleanEvents;
import com.tm.calemieconomy.event.listener.*;
import com.tm.calemieconomy.file.DirtyFile;
import com.tm.calemieconomy.file.ScheduledRandomPriceModifiersFile;
import com.tm.calemieconomy.file.TradesFile;
import com.tm.calemieconomy.init.*;
import com.tm.calemieconomy.packet.CEPacketHandler;
import com.tm.calemieconomy.screen.ScreenBank;
import com.tm.calemieconomy.screen.ScreenTradingPost;
import com.tm.calemieconomy.screen.ScreenTradingPostBulkTrade;
import com.tm.calemieconomy.screen.ScreenWallet;
import com.tm.calemieconomy.tab.CETab;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
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

    public static boolean isCuriosLoaded = false;

    /**
     * Everything starts here.
     */
    public CalemiEconomy() {

        isCuriosLoaded = ModList.get().getModContainerById("curios").isPresent();

        //Initializes the instance.
        instance = this;

        MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        MOD_EVENT_BUS.addListener(this::onCommonSetup);
        MOD_EVENT_BUS.addListener(this::onClientSetup);

        InitSounds.SOUNDS.register(MOD_EVENT_BUS);
        InitItems.init();
        InitBlockEntityTypes.BLOCK_ENTITY_TYPES.register(MOD_EVENT_BUS);
        InitMenuTypes.MENU_TYPES.register(MOD_EVENT_BUS);
        InitStats.STAT_TYPES.register(MOD_EVENT_BUS);
        InitStats.STATS.register(MOD_EVENT_BUS);

        TradesFile.init();
        ScheduledRandomPriceModifiersFile.init();
        DirtyFile.init();

        CEConfig.init();

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        InitOres.init();
        CEPacketHandler.init();
        MinecraftForge.EVENT_BUS.register(new WrenchEvents());
        MinecraftForge.EVENT_BUS.register(new SecurityEvents());
        MinecraftForge.EVENT_BUS.register(new BankCraftEvent());
        MinecraftForge.EVENT_BUS.register(new TradingPostsCleanEvents());
        MinecraftForge.EVENT_BUS.register(new ScheduledRandomPriceModifierHandler());
        InitStats.init();
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        InitKeyBindings.init();

        MinecraftForge.EVENT_BUS.register(new CuriosEvent());
        MinecraftForge.EVENT_BUS.register(new CoinPickupSoundEvent());
        MinecraftForge.EVENT_BUS.register(new WrenchLoreEvent());
        MinecraftForge.EVENT_BUS.register(new WalletOverlayEvent());
        MinecraftForge.EVENT_BUS.register(new WalletKeyEvent());
        MinecraftForge.EVENT_BUS.register(new TradingPostOverlayEvent());

        MenuScreens.register(InitMenuTypes.BANK.get(), ScreenBank::new);
        MenuScreens.register(InitMenuTypes.TRADING_POST.get(), ScreenTradingPost::new);
        MenuScreens.register(InitMenuTypes.TRADING_POST_BULK_TRADE.get(), ScreenTradingPostBulkTrade::new);

        MenuScreens.register(InitMenuTypes.WALLET.get(), ScreenWallet::new);

        BlockEntityRenderers.register(InitBlockEntityTypes.TRADING_POST.get(), RenderTradingPost::new);
    }

    @SubscribeEvent
    public void onServerStarting(RegisterCommandsEvent event) {
        EconomyCommand.register(event.getDispatcher());
    }
}
