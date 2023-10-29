package com.tm.calemieconomy.packet;

import com.tm.calemieconomy.main.CEReference;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class CEPacketHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(CEReference.MOD_ID, CEReference.MOD_ID),
            () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals
    );

    public static void init() {
        int id = 0;
        CEPacketHandler.INSTANCE.registerMessage(++id, PacketBankSyncTransactionAmount.class, PacketBankSyncTransactionAmount::toBytes, PacketBankSyncTransactionAmount::new, PacketBankSyncTransactionAmount::handle);
        CEPacketHandler.INSTANCE.registerMessage(++id, PacketBankWithdraw.class, PacketBankWithdraw::toBytes, PacketBankWithdraw::new, PacketBankWithdraw::handle);
        CEPacketHandler.INSTANCE.registerMessage(++id, PacketBankDeposit.class, PacketBankDeposit::toBytes, PacketBankDeposit::new, PacketBankDeposit::handle);
        CEPacketHandler.INSTANCE.registerMessage(++id, PacketTradingPostBroadcast.class, PacketTradingPostBroadcast::toBytes, PacketTradingPostBroadcast::new, PacketTradingPostBroadcast::handle);
        CEPacketHandler.INSTANCE.registerMessage(++id, PacketTradingPostSyncAmount.class, PacketTradingPostSyncAmount::toBytes, PacketTradingPostSyncAmount::new, PacketTradingPostSyncAmount::handle);
        CEPacketHandler.INSTANCE.registerMessage(++id, PacketTradingPostSyncPrice.class, PacketTradingPostSyncPrice::toBytes, PacketTradingPostSyncPrice::new, PacketTradingPostSyncPrice::handle);
        CEPacketHandler.INSTANCE.registerMessage(++id, PacketTradingPostSyncStack.class, PacketTradingPostSyncStack::toBytes, PacketTradingPostSyncStack::new, PacketTradingPostSyncStack::handle);
        CEPacketHandler.INSTANCE.registerMessage(++id, PacketTradingPostSyncBuyMode.class, PacketTradingPostSyncBuyMode::toBytes, PacketTradingPostSyncBuyMode::new, PacketTradingPostSyncBuyMode::handle);
        CEPacketHandler.INSTANCE.registerMessage(++id, PacketTradingPostBulkTrade.class, PacketTradingPostBulkTrade::toBytes, PacketTradingPostBulkTrade::new, PacketTradingPostBulkTrade::handle);
        CEPacketHandler.INSTANCE.registerMessage(++id, PacketTradingPostSellAll.class, PacketTradingPostSellAll::toBytes, PacketTradingPostSellAll::new, PacketTradingPostSellAll::handle);
        CEPacketHandler.INSTANCE.registerMessage(++id, PacketTradingPostSyncFileKey.class, PacketTradingPostSyncFileKey::toBytes, PacketTradingPostSyncFileKey::new, PacketTradingPostSyncFileKey::handle);
        CEPacketHandler.INSTANCE.registerMessage(++id, PacketTradingPostSyncDynamicPriceMode.class, PacketTradingPostSyncDynamicPriceMode::toBytes, PacketTradingPostSyncDynamicPriceMode::new, PacketTradingPostSyncDynamicPriceMode::handle);
        CEPacketHandler.INSTANCE.registerMessage(++id, PacketTradingPostSyncExtremum.class, PacketTradingPostSyncExtremum::toBytes, PacketTradingPostSyncExtremum::new, PacketTradingPostSyncExtremum::handle);
        CEPacketHandler.INSTANCE.registerMessage(++id, PacketTradingPostSyncVaryRate.class, PacketTradingPostSyncVaryRate::toBytes, PacketTradingPostSyncVaryRate::new, PacketTradingPostSyncVaryRate::handle);
        CEPacketHandler.INSTANCE.registerMessage(++id, PacketTradingPostSyncStableRate.class, PacketTradingPostSyncStableRate::toBytes, PacketTradingPostSyncStableRate::new, PacketTradingPostSyncStableRate::handle);
        CEPacketHandler.INSTANCE.registerMessage(++id, PacketTradingPostResetCurrentPrice.class, PacketTradingPostResetCurrentPrice::toBytes, PacketTradingPostResetCurrentPrice::new, PacketTradingPostResetCurrentPrice::handle);
        CEPacketHandler.INSTANCE.registerMessage(++id, PacketExtractWalletCurrency.class, PacketExtractWalletCurrency::toBytes, PacketExtractWalletCurrency::new, PacketExtractWalletCurrency::handle);
        CEPacketHandler.INSTANCE.registerMessage(++id, PacketOpenWallet.class, PacketOpenWallet::toBytes, PacketOpenWallet::new, PacketOpenWallet::handle);
    }
}
