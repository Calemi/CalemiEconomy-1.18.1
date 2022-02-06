package com.tm.calemieconomy.menu;

import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemicore.util.menu.MenuItemBase;
import com.tm.calemicore.util.menu.slot.SlotFilter;
import com.tm.calemieconomy.init.InitItems;
import com.tm.calemieconomy.init.InitMenuTypes;
import com.tm.calemieconomy.item.ItemCoin;
import com.tm.calemieconomy.item.ItemWallet;
import com.tm.calemieconomy.main.CEReference;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

public class MenuWallet extends MenuItemBase {

    private final Container walletContainer;

    public MenuWallet(int containerID, Inventory playerInv, ItemStack walletStack) {
        super(InitMenuTypes.WALLET.get(), containerID, walletStack);

        walletContainer = new SimpleContainer(1);
        addSlot(new SlotFilter(walletContainer, 0, 17, 42, InitItems.COIN_COPPER.get(), InitItems.COIN_SILVER.get(), InitItems.COIN_GOLD.get(), InitItems.COIN_PLATINUM.get()));

        addPlayerInventory(playerInv, 94);
    }

    /**
     * Called when a slot is clicked.
     * Handles adding money to Wallet.
     */
    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
        super.clicked(slotId, dragType, clickTypeIn, player);

        ItemStack coinStack = walletContainer.getItem(0);

        //Checks if the Stack in the Wallet is a Coin.
        if (coinStack.getItem() instanceof ItemCoin coin) {

            if (getItemStack().getItem() instanceof ItemWallet wallet) {

                int amountToAdd = 0;
                int stacksToRemove = 0;

                //Iterates through every count of the Stack. Ex: a stack of 32 will iterate 32 times.
                for (int i = 0; i < coinStack.getCount(); i++) {

                    //Checks if the Wallet can fit the added money.
                    if (wallet.canDepositCurrency(getItemStack(), coin.value + amountToAdd)) {
                        amountToAdd += coin.value;
                        stacksToRemove++;
                    }

                    else break;
                }

                wallet.depositCurrency(getItemStack(), amountToAdd);
                walletContainer.removeItem(0, stacksToRemove);
                LogHelper.log(CEReference.MOD_NAME, "REMOVING: " + stacksToRemove);
            }
        }
    }

    @Override
    public int getContainerSize() {
        return 1;
    }
}
