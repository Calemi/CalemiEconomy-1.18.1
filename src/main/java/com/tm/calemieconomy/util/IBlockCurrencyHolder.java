package com.tm.calemieconomy.util;

public interface IBlockCurrencyHolder {

    long getCurrency();
    long getCurrencyCapacity();
    void setCurrency(long amount);
    boolean canDepositCurrency(long amount);
    boolean canWithdrawCurrency(long amount);
    void depositCurrency(long amount);
    void withdrawCurrency(long amount);
}
