package com.tm.calemieconomy.menu;

import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemicore.util.menu.MenuBlockBase;
import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import com.tm.calemieconomy.init.InitMenuTypes;
import com.tm.calemieconomy.main.CEReference;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class MenuTradingPostBulkTrade extends MenuBlockBase {

    public MenuTradingPostBulkTrade(int containerID, Inventory playerInv, BlockEntityTradingPost post) {
        super(InitMenuTypes.TRADING_POST_BULK_TRADE.get(), containerID, post);

        addPlayerInventory(playerInv, 102);
    }

    public MenuTradingPostBulkTrade(int containerID, Inventory playerInv, BlockPos pos) {
        this(containerID, playerInv, (BlockEntityTradingPost) playerInv.player.level.getBlockEntity(pos));
    }

    @Override
    public int getContainerSize() {
        return 0;
    }
}
