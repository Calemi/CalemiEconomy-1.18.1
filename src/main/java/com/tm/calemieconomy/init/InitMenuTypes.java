package com.tm.calemieconomy.init;

import com.tm.calemieconomy.main.CEReference;
import com.tm.calemieconomy.menu.MenuBank;
import com.tm.calemieconomy.menu.MenuTradingPost;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Handles setting up the Block Entities for the mod.
 */
public class InitMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, CEReference.MOD_ID);

    public static final RegistryObject<MenuType<MenuBank>> BANK = MENU_TYPES.register("bank", () -> new MenuType<>(MenuBank::new));
    public static final RegistryObject<MenuType<MenuTradingPost>> TRADING_POST = MENU_TYPES.register("trading_post", () -> new MenuType<>(MenuTradingPost::new));
}
