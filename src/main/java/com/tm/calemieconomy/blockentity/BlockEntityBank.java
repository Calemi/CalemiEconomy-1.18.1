package com.tm.calemieconomy.blockentity;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.blockentity.BlockEntityContainerBase;
import com.tm.calemieconomy.config.CEConfig;
import com.tm.calemieconomy.init.InitBlockEntityTypes;
import com.tm.calemieconomy.item.ItemCoin;
import com.tm.calemieconomy.menu.MenuBank;
import com.tm.calemieconomy.security.ISecurityHolder;
import com.tm.calemieconomy.security.SecurityProfile;
import com.tm.calemieconomy.util.IBlockCurrencyHolder;
import com.tm.calemieconomy.util.helper.CurrencyHelper;
import com.tm.calemieconomy.util.helper.NetworkScanner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BlockEntityBank extends BlockEntityContainerBase implements ISecurityHolder, IBlockCurrencyHolder, ICurrencyNetwork {

    private final SecurityProfile profile = new SecurityProfile();
    private long currency;
    private long transactionAmount = 1;

    private boolean isOnlyConnectedBank = true;

    private NetworkScanner scanner;
    private final List<Location> connectedUnits = new ArrayList<>();

    public BlockEntityBank(BlockPos pos, BlockState state) {
        super(InitBlockEntityTypes.BANK.get(), pos, state);
    }

    /**
     * Called every tick.
     */
    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntityBank bank) {

        handleNetworkScan(level, bank);

        if (!bank.getLevel().isClientSide) {
            handleCoinInput(bank);
        }
    }

    private static void handleNetworkScan(Level level, BlockEntityBank bank) {

        if (bank.scanner == null) {
            bank.scanner = new NetworkScanner(bank.getLocation());
        }

        if (level.getGameTime() % 40 == 0) {

            bank.getConnectedUnits().clear();

            boolean foundAnotherBank = false;

            bank.scanner.reset();
            bank.scanner.startNetworkScan(bank.getConnectedDirections());

            for (Location location : bank.scanner.buffer) {

                if (!location.equals(bank.getLocation()) && location.getBlockEntity() instanceof BlockEntityBank) {
                    foundAnotherBank = true;
                }

                if (location.getBlockEntity() instanceof ICurrencyNetworkUnit unit) {

                    bank.getConnectedUnits().add(location);

                    if (unit.getBankLocation() == null) {
                        unit.setBankLocation(bank.getLocation());
                    }
                }
            }

            bank.setOnlyConnectedBank(!foundAnotherBank);
        }
    }

    private static void handleCoinInput(BlockEntityBank bank) {

        ItemStack stack = bank.getItem(0);

        if (stack.getItem() instanceof ItemCoin) {

            long amountToAdd = ((ItemCoin) stack.getItem()).value;
            int countToRemove = 0;

            for (int i = 0; i < stack.getCount(); i++) {

                if (bank.canDepositCurrency(amountToAdd)) {
                    amountToAdd += ((ItemCoin) stack.getItem()).value;
                    countToRemove++;
                }
            }

            if (countToRemove != 0) {

                bank.depositCurrency(countToRemove * ((ItemCoin) stack.getItem()).value);
                bank.removeItem(0, countToRemove);
            }
        }
    }

    public boolean isOnlyConnectedBank() {
        return isOnlyConnectedBank;
    }

    public void setOnlyConnectedBank(boolean value) {
        isOnlyConnectedBank = value;
        markUpdated();
    }

    public List<Location> getConnectedUnits() {
        return connectedUnits;
    }

    public long getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(long value) {
        value = Mth.clamp(value, 1, CEConfig.economy.bankCurrencyCapacity.get());
        transactionAmount = value;
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
     * @return
     */

    @Override
    public long getCurrency() {
        return currency;
    }

    @Override
    public long getCurrencyCapacity() {
        return CEConfig.economy.bankCurrencyCapacity.get();
    }

    @Override
    public void setCurrency(long amount) {
        currency = amount;
        markUpdated();
    }

    @Override
    public boolean canDepositCurrency(long amount) {
        return getCurrency() + amount <= getCurrencyCapacity();
    }

    @Override
    public boolean canWithdrawCurrency(long amount) {
        return getCurrency() >= amount;
    }

    @Override
    public void depositCurrency(long amount) {

        if (canDepositCurrency(amount)) {
            setCurrency(getCurrency() + amount);
        }

        markUpdated();
    }

    @Override
    public void withdrawCurrency(long amount) {

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

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        profile.loadFromNBT(tag);
        currency = CurrencyHelper.loadFromNBT(tag);
        transactionAmount = tag.getLong("TransactionAmount");
        isOnlyConnectedBank = tag.getBoolean("OnlyBank");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        profile.saveToNBT(tag);
        CurrencyHelper.saveToNBT(tag, currency);
        tag.putLong("TransactionAmount", transactionAmount);
        tag.putBoolean("OnlyBank", isOnlyConnectedBank);
    }
}
