package com.tm.calemieconomy.event;

import com.tm.calemieconomy.block.BlockBank;
import com.tm.calemieconomy.item.ItemWallet;
import com.tm.calemieconomy.util.helper.CurrencyHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BankCraftEvent {

    /**
     * Handles all key events.
     */
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onBankCraft (PlayerEvent.ItemCraftedEvent event) {

        ItemStack bank = event.getCrafting();

        if (Block.byItem(bank.getItem()) instanceof BlockBank) {

            ItemStack walletStack = null;

            for (int slot = 0; slot < event.getInventory().getContainerSize(); slot++) {

                if (event.getInventory().getItem(slot).getItem() instanceof ItemWallet) {
                    walletStack = event.getInventory().getItem(slot);
                    break;
                }
            }

            if (walletStack != null && walletStack.getItem() instanceof ItemWallet wallet) {

                CurrencyHelper.saveToNBT(bank.getOrCreateTag(), CurrencyHelper.loadFromNBT(walletStack.getOrCreateTag()));
            }
        }
    }
}