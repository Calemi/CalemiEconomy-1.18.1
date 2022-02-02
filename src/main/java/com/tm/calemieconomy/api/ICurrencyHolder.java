package com.tm.calemieconomy.api;

public interface ICurrencyHolder {

    int getCurrency();
    int getCurrencyCapacity();
    void setCurrency(int amount);
    boolean canDepositCurrency(int amount);
    boolean canWithdrawCurrency(int amount);
    void depositCurrency(int amount);
    void withdrawCurrency(int amount);
}
