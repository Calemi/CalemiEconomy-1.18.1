package com.tm.calemieconomy.init;

import com.tm.calemieconomy.block.BlockRaritaniumOre;
import com.tm.calemieconomy.block.base.BlockItemBase;
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

    public static final RegistryObject<Block> RARITANIUM_ORE = regBlock("raritanium_ore", CalemiEconomy.TAB, () -> new BlockRaritaniumOre(MaterialColor.STONE, SoundType.STONE));
    public static final RegistryObject<Block> RARITANIUM_ORE_DEEPSLATE = regBlock("raritanium_ore_deepslate", CalemiEconomy.TAB, () -> new BlockRaritaniumOre(MaterialColor.DEEPSLATE, SoundType.DEEPSLATE));

    //----- ITEMS ------\\

    public static final RegistryObject<Item> RARITANIUM = regItem("raritanium", () -> new Item(new Item.Properties()));

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
