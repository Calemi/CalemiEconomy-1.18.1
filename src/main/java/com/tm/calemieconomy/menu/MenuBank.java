package com.tm.calemieconomy.menu;

import com.tm.calemieconomy.api.ICurrencyHolder;
import com.tm.calemieconomy.init.InitItems;
import com.tm.calemieconomy.init.InitMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;

public class MenuBank extends MenuBase implements ICurrencyHolder {

    public final ContainerData data;
    private final ContainerLevelAccess access;

    public MenuBank(int containerID, Inventory playerInv) {
        this(containerID, playerInv, new SimpleContainer(27), new SimpleContainerData(2), ContainerLevelAccess.NULL);
    }

    public MenuBank(int containerID, Inventory playerInv, Container container, ContainerData data, ContainerLevelAccess access) {
        super(InitMenuTypes.BANK.get(), containerID);

        this.container = container;
        this.data = data;
        this.access = access;

        checkContainerSize(container, container.getContainerSize());
        checkContainerDataCount(data, 2);
        container.startOpen(playerInv.player);

        //Coin Slot
        addSlot(new SlotFilter(container, 0, 62, 18, InitItems.COIN_COPPER.get(), InitItems.COIN_SILVER.get(), InitItems.COIN_GOLD.get(), InitItems.COIN_PLATINUM.get()));

        //Wallet Slot
        addSlot(new Slot(container, 1, 98, 18));

        addPlayerInventory(playerInv, 62);

        addDataSlots(data);
    }

    @Override
    public int getCurrency() {
        return data.get(0);
    }

    @Override
    public int getCurrencyCapacity() {
        return data.get(1);
    }

    @Override
    public void setCurrency(int amount) {
        data.set(0, amount);
        access.execute(Level::blockEntityChanged);
    }

    @Override
    public boolean canDepositCurrency(int amount) {
        return getCurrency() + amount <= getCurrencyCapacity();
    }

    @Override
    public boolean canWithdrawCurrency(int amount) {
        return getCurrency() >= amount;
    }

    @Override
    public void depositCurrency(int amount) {

        if (canDepositCurrency(amount)) {
            data.set(0, getCurrency() + amount);
        }

        access.execute(Level::blockEntityChanged);
    }

    @Override
    public void withdrawCurrency(int amount) {

        if (canWithdrawCurrency(amount)) {
            data.set(0, getCurrency() - amount);
        }

        access.execute(Level::blockEntityChanged);
    }
}
