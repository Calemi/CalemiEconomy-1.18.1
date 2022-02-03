package com.tm.calemieconomy.init;

import com.google.common.collect.Sets;
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

    public static final RegistryObject<BlockEntityType<BlockEntityCurrencyNetworkCable>> CURRENCY_NETWORK_CABLE = BLOCK_ENTITY_TYPES.register(
            "currency_network_cable", () -> new BlockEntityType<>(BlockEntityCurrencyNetworkCable::new, Sets.newHashSet(InitItems.CURRENCY_NETWORK_CABLE.get()),  null));
    public static final RegistryObject<BlockEntityType<BlockEntityCurrencyNetworkGate>> CURRENCY_NETWORK_GATE = BLOCK_ENTITY_TYPES.register(
            "currency_network_gate", () -> new BlockEntityType<>(BlockEntityCurrencyNetworkGate::new, Sets.newHashSet(InitItems.CURRENCY_NETWORK_GATE.get()),  null));
    public static final RegistryObject<BlockEntityType<BlockEntityTradingPost>> TRADING_POST = BLOCK_ENTITY_TYPES.register(
            "trading_post", () -> new BlockEntityType<>(BlockEntityTradingPost::new, Sets.newHashSet(InitItems.TRADING_POST.get()),  null));
    public static final RegistryObject<BlockEntityType<BlockEntityBank>> BANK = BLOCK_ENTITY_TYPES.register(
            "bank", () -> new BlockEntityType<>(BlockEntityBank::new, Sets.newHashSet(InitItems.BANK.get()), null));
}
