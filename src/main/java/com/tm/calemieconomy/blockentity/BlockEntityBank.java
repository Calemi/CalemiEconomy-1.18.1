package com.tm.calemieconomy.blockentity;

import com.tm.calemieconomy.api.ICurrencyHolder;
import com.tm.calemieconomy.config.CEConfig;
import com.tm.calemieconomy.init.InitBlockEntityTypes;
import com.tm.calemieconomy.item.ItemCoin;
import com.tm.calemieconomy.menu.MenuBank;
import com.tm.calemieconomy.security.ISecurityHolder;
import com.tm.calemieconomy.security.SecurityProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockEntityBank extends BlockEntityContainerBase implements ISecurityHolder, ICurrencyHolder, ICurrencyNetwork {

    private final SecurityProfile profile = new SecurityProfile();
    private int currency;

    public BlockEntityBank(BlockPos pos, BlockState state) {
        super(InitBlockEntityTypes.BANK.get(), pos, state);
    }

    /**
     * Called every tick.
     */
    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntityBank bank) {

        if (bank.getLevel() != null) {

            if (!bank.getLevel().isClientSide) {

                ItemStack stack = bank.getItem(0);

                if (stack.getItem() instanceof ItemCoin) {

                    int amountToAdd = ((ItemCoin) stack.getItem()).value;
                    int stackSize = 0;

                    for (int i = 0; i < stack.getCount(); i++) {

                        if (bank.canDepositCurrency(amountToAdd)) {
                            stackSize++;
                            amountToAdd += ((ItemCoin) stack.getItem()).value;
                        }
                    }

                    if (stackSize != 0) {

                        bank.depositCurrency(stackSize * ((ItemCoin) stack.getItem()).value);
                        bank.removeItem(0, stackSize);
                    }
                }
            }
        }
    }

    /**
     * Security Methods
     */

    @Override
    public SecurityProfile getSecurityProfile () {
        return profile;
    }

    /**
     * Currency Methods
     */

    @Override
    public int getCurrency() {
        return currency;
    }

    @Override
    public int getCurrencyCapacity() {
        return CEConfig.economy.bankCurrencyCapacity.get();
    }

    @Override
    public void setCurrency(int amount) {
        currency = amount;
        markUpdated();
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
            setCurrency(getCurrency() + amount);
        }

        markUpdated();
    }

    @Override
    public void withdrawCurrency(int amount) {

        if (canWithdrawCurrency(amount)) {
            setCurrency(getCurrency() - amount);
        }

        markUpdated();
    }

    /**
     * Network Methods
     */

    @Override
    public Direction[] getConnectedDirections() {
        return Direction.values();
    }

    /**
     * Container Methods
     */

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container.bank");
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerID, Inventory playerInv, Player player) {
        return new MenuBank(containerID, playerInv, this);
    }
}
