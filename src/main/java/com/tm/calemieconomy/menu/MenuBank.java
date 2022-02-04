package com.tm.calemieconomy.menu;

import com.tm.calemieconomy.blockentity.BlockEntityBank;
import com.tm.calemieconomy.init.InitItems;
import com.tm.calemieconomy.init.InitMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class MenuBank extends MenuBase {

    public MenuBank(int containerID, Inventory playerInv, BlockEntityBank bank) {
        super(InitMenuTypes.BANK.get(), containerID, bank);

        //Coin Slot
        addSlot(new SlotFilter(bank, 0, 62, 18, InitItems.COIN_COPPER.get(), InitItems.COIN_SILVER.get(), InitItems.COIN_GOLD.get(), InitItems.COIN_PLATINUM.get()));

        //Wallet Slot
        addSlot(new Slot(bank, 1, 98, 18));

        addPlayerInventory(playerInv, 62);
    }

    public MenuBank(int containerID, Inventory playerInv, BlockPos pos) {
        this(containerID, playerInv, (BlockEntityBank) playerInv.player.level.getBlockEntity(pos));
    }
}
