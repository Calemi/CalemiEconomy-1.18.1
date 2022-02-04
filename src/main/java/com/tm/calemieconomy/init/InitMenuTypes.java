package com.tm.calemieconomy.init;

import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemieconomy.main.CEReference;
import com.tm.calemieconomy.menu.MenuBank;
import com.tm.calemieconomy.menu.MenuTradingPost;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
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

    public static final RegistryObject<MenuType<MenuBank>> BANK = MENU_TYPES.register("bank", regMenu(MenuBank::new));
    public static final RegistryObject<MenuType<MenuTradingPost>> TRADING_POST = MENU_TYPES.register("trading_post", regMenu(MenuTradingPost::new));

    static <M extends AbstractContainerMenu> Supplier<MenuType<M>> regMenu(CEMenuFactory<M> factory) {
        return () -> new MenuType<>(factory);
    }

    interface CEMenuFactory<M extends AbstractContainerMenu> extends IContainerFactory<M> {

        @Override
        default M create(int windowId, Inventory inv, FriendlyByteBuf data) {
            LogHelper.log(CEReference.MOD_NAME, data);
            return create(windowId, inv, data.readBlockPos());
        }

        M create(int id, Inventory inventory, BlockPos te);
    }
}
