package com.tm.calemieconomy.packet;

import com.tm.calemieconomy.blockentity.BlockEntityBank;
import com.tm.calemieconomy.item.ItemWallet;
import com.tm.calemieconomy.util.helper.CurrencyHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketBankDeposit extends PacketBlockEntity {

    public PacketBankDeposit() {}

    public PacketBankDeposit(BlockPos pos) {
        super(pos);
    }

    public PacketBankDeposit(FriendlyByteBuf buf) {
        super(buf);
    }

    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx, ServerPlayer player, BlockEntity blockEntity) {

        if (blockEntity instanceof BlockEntityBank bank) {

            //Checks if there is a Wallet in the Wallet slot.
            if (bank.getItem(1).getItem() instanceof ItemWallet wallet) {

                ItemStack walletStack = bank.getItem(1);

                if (walletStack.getCount() > 1) {
                    return;
                }

                long walletCurrency = wallet.getCurrency(walletStack);
                long amountToAdd = CurrencyHelper.getAmountToAdd(bank.getCurrency(), Math.min(walletCurrency, bank.getTransactionAmount()), bank.getCurrencyCapacity());

                //If the Bank can fit the currency, add it and subtract it from the Wallet.
                if (amountToAdd > 0) {
                    bank.depositCurrency(amountToAdd);
                    wallet.withdrawCurrency(walletStack, amountToAdd);
                }

                //If the Bank can't fit all the money, get how much is needed to fill it, then only used that much.
                else {

                    long remainder = CurrencyHelper.getAmountToFill(bank.getCurrency(), Math.min(walletCurrency, bank.getTransactionAmount()), bank.getCurrencyCapacity());

                    if (remainder > 0) {
                        bank.depositCurrency(remainder);
                        wallet.withdrawCurrency(walletStack, remainder);
                    }
                }

                //CEPacketHandler.INSTANCE.sendToServer(new PacketBankSyncTransactionAmount(bank.getCurrency(), wallet.getCurrency(walletStack), bank.getBlockPos()));
            }

            bank.markUpdated();
        }
    }
}
