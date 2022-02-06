package com.tm.calemieconomy.util;

import net.minecraft.world.item.ItemStack;

public interface IItemCurrencyHolder {

    int getCurrency(ItemStack stack);
    int getCurrencyCapacity();
    void setCurrency(ItemStack stack, int amount);
    boolean canDepositCurrency(ItemStack stack, int amount);
    boolean canWithdrawCurrency(ItemStack stack, int amount);
    void depositCurrency(ItemStack stack, int amount);
    void withdrawCurrency(ItemStack stack, int amount);
}
