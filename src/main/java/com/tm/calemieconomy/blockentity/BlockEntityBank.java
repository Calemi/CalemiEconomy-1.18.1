package com.tm.calemieconomy.blockentity;

import com.tm.calemicore.util.Location;
import com.tm.calemieconomy.api.ICurrencyHolder;
import com.tm.calemieconomy.block.BlockCurrencyNetworkGate;
import com.tm.calemieconomy.config.CEConfig;
import com.tm.calemieconomy.init.InitBlockEntityTypes;
import com.tm.calemieconomy.init.InitItems;
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
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockEntityBank extends BlockEntityContainerBase implements ICurrencyNetwork, ICurrencyHolder, ISecurityHolder {

    private int currency;
    private final SecurityProfile profile = new SecurityProfile();

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

    @Override
    public Direction[] getConnectedDirections() {
        return Direction.values();
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
            data.set(0, getCurrency() + amount);
        }

        markUpdated();
    }

    @Override
    public void withdrawCurrency(int amount) {

        if (canWithdrawCurrency(amount)) {
            data.set(0, getCurrency() - amount);
        }

        markUpdated();
    }

    private final ContainerData data = new ContainerData() {

        @Override
        public int get(int id) {

            return switch (id) {
                case 0 -> currency;
                case 1 -> CEConfig.economy.bankCurrencyCapacity.get();
                default -> 0;
            };
        }

        @Override
        public void set(int id, int data) {

            if (id == 0) {
               currency = data;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    @Override
    public SecurityProfile getSecurityProfile () {
        return profile;
    }

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
        return new MenuBank(containerID, playerInv, this, data, ContainerLevelAccess.create(this.level, this.getBlockPos()));
    }
}
