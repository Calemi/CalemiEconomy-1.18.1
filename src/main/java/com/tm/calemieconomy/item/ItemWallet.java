package com.tm.calemieconomy.item;

import com.tm.calemicore.util.UnitMessenger;
import com.tm.calemicore.util.helper.LoreHelper;
import com.tm.calemieconomy.config.CEConfig;
import com.tm.calemieconomy.curios.CuriosIntegration;
import com.tm.calemieconomy.main.CalemiEconomy;
import com.tm.calemieconomy.menu.MenuWallet;
import com.tm.calemieconomy.tab.CETab;
import com.tm.calemieconomy.util.IItemCurrencyHolder;
import com.tm.calemieconomy.util.helper.CurrencyHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class ItemWallet extends Item implements IItemCurrencyHolder {

    public static final UnitMessenger MESSENGER = new UnitMessenger("wallet");

    public ItemWallet() {
        super(new Item.Properties().tab(CalemiEconomy.TAB).stacksTo(1));
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
        super.fillItemCategory(tab, items);

        if (tab == CalemiEconomy.TAB) {
            ItemStack stack = new ItemStack(this);
            setCurrency(stack, CEConfig.economy.walletCurrencyCapacity.get());
            items.add(stack);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipList, TooltipFlag advanced) {

        if (CalemiEconomy.isCuriosLoaded) {
            LoreHelper.addBlankLine(tooltipList);
        }

        LoreHelper.addInformationLoreFirst(tooltipList, new TranslatableComponent("ce.lore.wallet"));
        LoreHelper.addControlsLoreFirst(tooltipList, new TranslatableComponent("ce.lore.wallet.use"), LoreHelper.ControlType.USE);
    }

    /**
     * Handles opening the GUI.
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);

        if (stack.getCount() > 1) {
            return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
        }

        //Checks if on server & if the Player is a Server Player.
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {

            //Checks if Wallet is not disabled by config.
            if (CEConfig.economy.walletCurrencyCapacity.get() > 0) {
                openGui(serverPlayer, stack, player.getInventory().selected);
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
            }
        }

        return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
    }

    /**
     * Opens the GUI.
     */
    private void openGui (ServerPlayer player, ItemStack stack, int selectedSlot) {
        NetworkHooks.openGui(player, new SimpleMenuProvider((id, playerInventory, unused) -> {
            return new MenuWallet(id, playerInventory, stack);
        }, new TranslatableComponent("container.wallet")), buffer -> {
            buffer.writeItem(stack);
        });
    }

    @Override
    public long getCurrency(ItemStack stack) {
        return CurrencyHelper.loadFromNBT(stack.getOrCreateTag());
    }

    @Override
    public long getCurrencyCapacity() {
        return CEConfig.economy.walletCurrencyCapacity.get();
    }

    @Override
    public void setCurrency(ItemStack stack, long amount) {
        CurrencyHelper.saveToNBT(stack.getOrCreateTag(), amount);
    }

    @Override
    public boolean canDepositCurrency(ItemStack stack, long amount) {
        return getCurrency(stack) + amount <= getCurrencyCapacity();
    }

    @Override
    public boolean canWithdrawCurrency(ItemStack stack, long amount) {
        return getCurrency(stack) >= amount;
    }

    @Override
    public void depositCurrency(ItemStack stack, long amount) {

        if (canDepositCurrency(stack, amount)) {
            setCurrency(stack, getCurrency(stack) + amount);
        }
    }

    @Override
    public void withdrawCurrency(ItemStack stack, long amount) {

        if (canWithdrawCurrency(stack, amount)) {
            setCurrency(stack, getCurrency(stack) - amount);
        }
    }

    /**
     * Adds behaviours to the Wallet as a curios Item.
     */
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @org.jetbrains.annotations.Nullable CompoundTag nbt) {

        if (CalemiEconomy.isCuriosLoaded) {
            return CuriosIntegration.walletCapability();
        }

        return super.initCapabilities(stack, nbt);
    }
}
