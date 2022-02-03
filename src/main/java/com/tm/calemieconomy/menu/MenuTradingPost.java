package com.tm.calemieconomy.menu;

import com.tm.calemieconomy.init.InitMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;

public class MenuTradingPost extends MenuBase {

    public final ContainerData data;

    public MenuTradingPost(int containerID, Inventory playerInv) {
        this(containerID, playerInv, new SimpleContainer(27), new SimpleContainerData(2));
    }

    public MenuTradingPost(int containerID, Inventory playerInv, Container container, ContainerData data) {
        super(InitMenuTypes.TRADING_POST.get(), containerID);

        this.container = container;
        this.data = data;

        checkContainerSize(container, container.getContainerSize());
        checkContainerDataCount(data, 2);
        container.startOpen(playerInv.player);

        //Trading Post Inventory
        for(int rowY = 0; rowY < 3; rowY++) {
            for(int rowX = 0; rowX < 9; rowX++) {
                addSlot(new Slot(container, rowX + rowY * 9, 8 + rowX * 18, 50 + rowY * 18));
            }
        }

        addPlayerInventory(playerInv, 117);

        addDataSlots(data);
    }
}
