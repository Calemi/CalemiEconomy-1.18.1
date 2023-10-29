package com.tm.calemieconomy.packet;

import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemicore.util.helper.MathHelper;
import com.tm.calemieconomy.blockentity.BlockEntityBank;
import com.tm.calemieconomy.item.ItemWallet;
import com.tm.calemieconomy.main.CEReference;
import com.tm.calemieconomy.util.helper.CurrencyHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketBankWithdraw extends PacketBlockEntity {

    public PacketBankWithdraw() {}

    public PacketBankWithdraw(BlockPos pos) {
        super(pos);
    }

    public PacketBankWithdraw(FriendlyByteBuf buf) {
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
                long amountToAdd = CurrencyHelper.getAmountToAdd(walletCurrency, Math.min(bank.getCurrency(), bank.getTransactionAmount()), wallet.getCurrencyCapacity());

                //If the Wallet can fit the currency, add it and subtract it from the Bank.
                if (amountToAdd > 0) {
                    bank.withdrawCurrency(amountToAdd);
                    wallet.depositCurrency(walletStack, amountToAdd);
                }

                //If the Wallet can't fit all the money, get how much is needed to fill it, then only used that much.
                else {

                    long remainder = CurrencyHelper.getAmountToFill(walletCurrency, Math.min(bank.getCurrency(), bank.getTransactionAmount()), wallet.getCurrencyCapacity());

                    if (remainder > 0) {
                        bank.withdrawCurrency(remainder);
                        wallet.depositCurrency(walletStack, remainder);
                    }
                }
            }

            bank.markUpdated();
        }
    }
}
