package com.tm.calemieconomy.menu;

import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import com.tm.calemieconomy.init.InitMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class MenuTradingPost extends MenuBase {

    public MenuTradingPost(int containerID, Inventory playerInv, BlockEntityTradingPost post) {
        super(InitMenuTypes.TRADING_POST.get(), containerID, post);

        //Trading Post Inventory
        for(int rowY = 0; rowY < 3; rowY++) {
            for(int rowX = 0; rowX < 9; rowX++) {
                addSlot(new Slot(post, rowX + rowY * 9, 8 + rowX * 18, 50 + rowY * 18));
            }
        }

        addPlayerInventory(playerInv, 117);
    }

    public MenuTradingPost(int containerID, Inventory playerInv, BlockPos pos) {
        this(containerID, playerInv, (BlockEntityTradingPost) playerInv.player.level.getBlockEntity(pos));
    }
}
