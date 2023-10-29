package com.tm.calemieconomy.packet;

import com.tm.calemicore.util.helper.ItemHelper;
import com.tm.calemieconomy.init.InitItems;
import com.tm.calemieconomy.item.ItemCoin;
import com.tm.calemieconomy.item.ItemWallet;
import com.tm.calemieconomy.util.helper.CEContainerHelper;
import com.tm.calemieconomy.util.helper.CurrencyHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketExtractWalletCurrency {

    private int buttonId;
    private int multiplier;

    public PacketExtractWalletCurrency () {}

    /**
     * Used to handle withdrawal from the Wallet.
     * @param buttonId The id of the button.
     * @param multiplier The multiplier; from shift-clicking & ctrl-clicking.
     */
    public PacketExtractWalletCurrency (int buttonId, int multiplier) {
        this.buttonId = buttonId;
        this.multiplier = multiplier;
    }

    public PacketExtractWalletCurrency (FriendlyByteBuf buf) {
        buttonId = buf.readInt();
        multiplier = buf.readInt();
    }

    public void toBytes (FriendlyByteBuf buf) {
        buf.writeInt(buttonId);
        buf.writeInt(multiplier);
    }

    public void handle (Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();

            if (player != null) {

                ItemStack walletStack = CurrencyHelper.getCurrentWallet(player);

                //Checks if the Wallet exists.
                if (walletStack != null && !walletStack.isEmpty()) {

                    //Handles syncing the new balance to the server & spawning the coins.
                    if (walletStack.getItem() instanceof ItemWallet wallet) {

                        Item item = InitItems.COIN_COPPER.get();
                        long price = ((ItemCoin) InitItems.COIN_COPPER.get()).value;

                        if (buttonId == 1) {
                            item = InitItems.COIN_SILVER.get();
                            price = ((ItemCoin) InitItems.COIN_SILVER.get()).value;
                        }

                        else if (buttonId == 2) {
                            item = InitItems.COIN_GOLD.get();
                            price = ((ItemCoin) InitItems.COIN_GOLD.get()).value;
                        }

                        else if (buttonId == 3) {
                            item = InitItems.COIN_PLATINUM.get();
                            price = ((ItemCoin) InitItems.COIN_PLATINUM.get()).value;
                        }

                        else if (buttonId == 4) {
                            item = InitItems.COIN_NETHERITE.get();
                            price = ((ItemCoin) InitItems.COIN_NETHERITE.get()).value;
                        }

                        price *= multiplier;

                        if (wallet.getCurrency(walletStack) >= price) {

                            ItemStack coinStack = new ItemStack(item);

                            if (CEContainerHelper.canInsertStack(player.getInventory(), coinStack, multiplier, 0, 36)) {
                                wallet.withdrawCurrency(walletStack, price);
                                CEContainerHelper.insertStack(player.getInventory(), coinStack, multiplier, 0, 36);
                            }
                        }
                    }
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
