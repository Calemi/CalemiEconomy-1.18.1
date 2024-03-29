package com.tm.calemieconomy.menu;

import com.tm.calemicore.util.menu.MenuBlockBase;
import com.tm.calemicore.util.menu.slot.SlotFilter;
import com.tm.calemieconomy.blockentity.BlockEntityBank;
import com.tm.calemieconomy.init.InitItems;
import com.tm.calemieconomy.init.InitMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;

public class MenuBank extends MenuBlockBase {

    public MenuBank(int containerID, Inventory playerInv, BlockEntityBank bank) {
        super(InitMenuTypes.BANK.get(), containerID, bank);

        //Coin Slot
        addSlot(new SlotFilter(bank, 0, 26, 17, InitItems.COIN_COPPER.get(), InitItems.COIN_SILVER.get(), InitItems.COIN_GOLD.get(), InitItems.COIN_PLATINUM.get(), InitItems.COIN_NETHERITE.get()));

        //Wallet Slot
        addSlot(new SlotFilter(bank, 1, 26, 53, InitItems.WALLET.get()));

        addPlayerInventory(playerInv, 95);
    }

    public MenuBank(int containerID, Inventory playerInv, BlockPos pos) {
        this(containerID, playerInv, (BlockEntityBank) playerInv.player.level.getBlockEntity(pos));
    }

    @Override
    public int getContainerSize() {
        return 2;
    }
}
