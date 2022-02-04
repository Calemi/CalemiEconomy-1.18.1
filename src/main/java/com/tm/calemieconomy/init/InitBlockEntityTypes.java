package com.tm.calemieconomy.init;

import com.tm.calemieconomy.blockentity.BlockEntityBank;
import com.tm.calemieconomy.blockentity.BlockEntityCurrencyNetworkCable;
import com.tm.calemieconomy.blockentity.BlockEntityCurrencyNetworkGate;
import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import com.tm.calemieconomy.main.CEReference;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Handles setting up the Block Entities for the mod.
 */
public class InitBlockEntityTypes {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, CEReference.MOD_ID);

    public static RegistryObject<BlockEntityType<BlockEntityCurrencyNetworkCable>> CURRENCY_NETWORK_CABLE = BLOCK_ENTITY_TYPES.register(
            "currency_network_cable", () -> BlockEntityType.Builder.of(BlockEntityCurrencyNetworkCable::new, InitItems.CURRENCY_NETWORK_CABLE.get(), InitItems.CURRENCY_NETWORK_CABLE_OPAQUE.get()).build(null));
    public static RegistryObject<BlockEntityType<BlockEntityCurrencyNetworkGate>> CURRENCY_NETWORK_GATE = BLOCK_ENTITY_TYPES.register(
            "currency_network_gate", () -> BlockEntityType.Builder.of(BlockEntityCurrencyNetworkGate::new, InitItems.CURRENCY_NETWORK_GATE.get()).build(null));

    public static RegistryObject<BlockEntityType<BlockEntityBank>> BANK = BLOCK_ENTITY_TYPES.register(
            "bank", () -> BlockEntityType.Builder.of(BlockEntityBank::new, InitItems.BANK.get()).build(null));
    public static RegistryObject<BlockEntityType<BlockEntityTradingPost>> TRADING_POST = BLOCK_ENTITY_TYPES.register(
            "trading_post", () -> BlockEntityType.Builder.of(BlockEntityTradingPost::new, InitItems.TRADING_POST.get()).build(null));
}
