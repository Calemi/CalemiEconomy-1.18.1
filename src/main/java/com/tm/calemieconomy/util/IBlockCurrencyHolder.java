package com.tm.calemieconomy.util;

public interface IBlockCurrencyHolder {

    int getCurrency();
    int getCurrencyCapacity();
    void setCurrency(int amount);
    boolean canDepositCurrency(int amount);
    boolean canWithdrawCurrency(int amount);
    void depositCurrency(int amount);
    void withdrawCurrency(int amount);
}
