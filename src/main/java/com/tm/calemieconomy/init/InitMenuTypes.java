package com.tm.calemieconomy.init;

import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemieconomy.config.CEConfig;
import com.tm.calemieconomy.main.CEReference;
import com.tm.calemieconomy.menu.MenuBank;
import com.tm.calemieconomy.menu.MenuTradingPost;
import com.tm.calemieconomy.menu.MenuTradingPostBulkTrade;
import com.tm.calemieconomy.menu.MenuWallet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * Handles setting up the Block Entities for the mod.
 */
public class InitMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, CEReference.MOD_ID);

    public static final RegistryObject<MenuType<MenuBank>> BANK = MENU_TYPES.register("bank", regBlockMenu(MenuBank::new));
    public static final RegistryObject<MenuType<MenuTradingPost>> TRADING_POST = MENU_TYPES.register("trading_post", regBlockMenu(MenuTradingPost::new));
    public static final RegistryObject<MenuType<MenuTradingPostBulkTrade>> TRADING_POST_BULK_TRADE = MENU_TYPES.register("trading_post_bulk_trade", regBlockMenu(MenuTradingPostBulkTrade::new));

    public static final RegistryObject<MenuType<MenuWallet>> WALLET = MENU_TYPES.register("wallet", regItemMenu(MenuWallet::new));

    static <M extends AbstractContainerMenu> Supplier<MenuType<M>> regBlockMenu(CEBlockMenuFactory<M> factory) {
        return () -> new MenuType<>(factory);
    }

    static <M extends AbstractContainerMenu> Supplier<MenuType<M>> regItemMenu(CEItemMenuFactory<M> factory) {
        return () -> new MenuType<>(factory);
    }

    interface CEBlockMenuFactory<M extends AbstractContainerMenu> extends IContainerFactory<M> {

        @Override
        default M create(int windowId, Inventory inv, FriendlyByteBuf data) {
            return create(windowId, inv, data.readBlockPos());
        }

        M create(int id, Inventory inventory, BlockPos pos);
    }

    interface CEItemMenuFactory<M extends AbstractContainerMenu> extends IContainerFactory<M> {

        @Override
        default M create(int windowId, Inventory inv, FriendlyByteBuf data) {
            return create(windowId, inv, data.readItem());
        }

        M create(int id, Inventory inventory, ItemStack stack);
    }
}
