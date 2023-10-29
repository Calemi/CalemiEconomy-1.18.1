package com.tm.calemieconomy.util;

import net.minecraft.world.item.ItemStack;

public interface IItemCurrencyHolder {

    long getCurrency(ItemStack stack);
    long getCurrencyCapacity();
    void setCurrency(ItemStack stack, long amount);
    boolean canDepositCurrency(ItemStack stack, long amount);
    boolean canWithdrawCurrency(ItemStack stack, long amount);
    void depositCurrency(ItemStack stack, long amount);
    void withdrawCurrency(ItemStack stack, long amount);
}
