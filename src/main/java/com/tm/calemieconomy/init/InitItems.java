package com.tm.calemieconomy.init;

import com.tm.calemieconomy.block.*;
import com.tm.calemieconomy.block.base.BlockItemBase;
import com.tm.calemieconomy.item.ItemCoin;
import com.tm.calemieconomy.item.ItemMoneyBag;
import com.tm.calemieconomy.item.ItemSecurityWrench;
import com.tm.calemieconomy.main.CEReference;
import com.tm.calemieconomy.main.CalemiEconomy;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * Handles setting up the Items for the mod.
 */
public class InitItems {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CEReference.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CEReference.MOD_ID);

    /**
     * Called to initialize the Items.
     */
    public static void init() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    //----- BLOCKS ------\\

    public static final RegistryObject<Block> RARITANIUM_ORE =                regBlock("raritanium_ore", CalemiEconomy.TAB, () -> new BlockRaritaniumOre(MaterialColor.STONE, SoundType.STONE));
    public static final RegistryObject<Block> RARITANIUM_ORE_DEEPSLATE =      regBlock("raritanium_ore_deepslate", CalemiEconomy.TAB, () -> new BlockRaritaniumOre(MaterialColor.DEEPSLATE, SoundType.DEEPSLATE));

    public static final RegistryObject<Block> COIN_STACK_COPPER =      BLOCKS.register("coin_stack_copper", BlockCoinStack::new);
    public static final RegistryObject<Block> COIN_STACK_SILVER =      BLOCKS.register("coin_stack_silver", BlockCoinStack::new);
    public static final RegistryObject<Block> COIN_STACK_GOLD =        BLOCKS.register("coin_stack_gold", BlockCoinStack::new);
    public static final RegistryObject<Block> COIN_STACK_PLATINUM =    BLOCKS.register("coin_stack_platinum", BlockCoinStack::new);

    public static final RegistryObject<Block> CURRENCY_NETWORK_CABLE =        regBlock("currency_network_cable", CalemiEconomy.TAB, BlockCurrencyNetworkCable::new);
    public static final RegistryObject<Block> CURRENCY_NETWORK_CABLE_OPAQUE = regBlock("currency_network_cable_opaque", CalemiEconomy.TAB, BlockCurrencyNetworkCableOpaque::new);
    public static final RegistryObject<Block> BANK =                          regBlock("bank", CalemiEconomy.TAB, BlockBank::new);

    //----- ITEMS ------\\

    public static final RegistryObject<Item> RARITANIUM =      regItem("raritanium", () -> new Item(new Item.Properties().tab(CalemiEconomy.TAB)));
    public static final RegistryObject<Item> COIN_COPPER =     regItem("coin_copper", () -> new ItemCoin(1, COIN_STACK_COPPER.get()));
    public static final RegistryObject<Item> COIN_SILVER =     regItem("coin_silver", () -> new ItemCoin(5, COIN_STACK_SILVER.get()));
    public static final RegistryObject<Item> COIN_GOLD =       regItem("coin_gold", () -> new ItemCoin(25, COIN_STACK_GOLD.get()));
    public static final RegistryObject<Item> COIN_PLATINUM =   regItem("coin_platinum", () -> new ItemCoin(100, COIN_STACK_PLATINUM.get()));

    public static final RegistryObject<Item> MONEY_BAG_CHEAP = regItem("money_bag_cheap", () -> new ItemMoneyBag(false));
    public static final RegistryObject<Item> MONEY_BAG_RICH =  regItem("money_bag_rich", () -> new ItemMoneyBag(true));

    public static final RegistryObject<Item> SECURITY_WRENCH = regItem("security_wrench", ItemSecurityWrench::new);

    /**
     * Used to register a Block.
     * @param name The name of the Block.
     * @param tab The Creative Tab for the Block.
     * @param sup The Item class.
     */
    public static RegistryObject<Block> regBlock(String name, CreativeModeTab tab, final Supplier<? extends Block> sup) {
        RegistryObject<Block> registryBlock = BLOCKS.register(name, sup);
        RegistryObject<Item> registryItem = ITEMS.register(name, () -> new BlockItemBase(registryBlock.get(), tab));
        return registryBlock;
    }

    /**
     * Used to register an Item.
     * @param name The name of the Item.
     * @param sup The Item class.
     */
    public static RegistryObject<Item> regItem(String name, final Supplier<? extends Item> sup) {
        return ITEMS.register(name, sup);
    }
}
