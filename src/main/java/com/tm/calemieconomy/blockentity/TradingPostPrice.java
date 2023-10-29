package com.tm.calemieconomy.blockentity;

import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemieconomy.config.CEConfig;
import com.tm.calemieconomy.file.ScheduledRandomPriceModifiersFile;
import com.tm.calemieconomy.main.CEReference;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

public class TradingPostPrice {

    private BlockEntityTradingPost post;

    public boolean isDynamic;
    private long startingPrice;
    private double currentPrice;
    private long extremum;
    public float varyRate;
    public float stableRate;
    public float modifier;

    public TradingPostPrice(BlockEntityTradingPost post) {
        this.post = post;
        this.isDynamic = false;
        this.startingPrice = 0;
        this.currentPrice = 0;
        this.extremum = 0;
        this.varyRate = 1;
        this.stableRate = 1;
    }

    private float getModifiers() {
        return post.adminMode ? modifier : 1;
    }

    public long getBulkPrice(long sets) {

        if (!isDynamic) {
            return Math.round(startingPrice * sets * getModifiers());
        }

        double totalPrice = currentPrice;
        double modifiedPrice = currentPrice;

        for (int i = 0; i < sets - 1; i++) {

            if (post.buyMode) {
                modifiedPrice -= varyRate;
                modifiedPrice = Mth.clamp(modifiedPrice, extremum, CEConfig.economy.walletCurrencyCapacity.get());
            }

            else {
                modifiedPrice += varyRate;
                modifiedPrice = Mth.clamp(modifiedPrice, 0, extremum);
            }

            totalPrice += modifiedPrice;
        }

        return Math.round(totalPrice);
    }

    public long getPrice() {
        if (isDynamic) return Math.round(currentPrice);
        return Math.round(startingPrice * getModifiers());
    }

    public void resetCurrentPrice() {
        currentPrice = startingPrice;
    }

    public long getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(long startingPrice) {
        this.startingPrice = startingPrice;

        if (post.buyMode) {
            this.extremum = Mth.clamp(extremum, 0, this.startingPrice);
        }

        else {
            this.extremum = Mth.clamp(extremum, this.startingPrice, CEConfig.economy.walletCurrencyCapacity.get());
        }

        clampCurrentPrice();
    }

    public long getExtremum() {
        return extremum;
    }

    public void setExtremum(long extremum) {

        if (post.buyMode) {
            extremum = Mth.clamp(extremum, 0, this.startingPrice);
        }

        else {
            extremum = Mth.clamp(extremum, this.startingPrice, CEConfig.economy.walletCurrencyCapacity.get());
        }

        LogHelper.log(CEReference.MOD_NAME, extremum);

        this.extremum = extremum;

        clampCurrentPrice();
    }

    public void vary() {

        //GO DOWN
        if (post.buyMode) {
            currentPrice -= varyRate;
        }

        //GO UP
        else {
            currentPrice += varyRate;
        }

        clampCurrentPrice();
    }

    public void stabilize() {

        //GO DOWN
        if (!post.buyMode) {
            currentPrice -= stableRate;
        }

        //GO UP
        else {
            currentPrice += stableRate;
        }

        clampCurrentPrice();
    }

    private void clampCurrentPrice() {

        if (post.buyMode) {
            currentPrice = Mth.clamp(currentPrice, extremum, startingPrice);
        }

        else {
            currentPrice = Mth.clamp(currentPrice, startingPrice, extremum);
        }
    }

    public void save(CompoundTag tag) {
        tag.putBoolean("IsDynamic", isDynamic);
        tag.putLong("StartingPrice", startingPrice);
        tag.putDouble("CurrentPrice", currentPrice);
        tag.putLong("Extremum", extremum);
        tag.putFloat("VaryRate", varyRate);
        tag.putFloat("StableRate", stableRate);
        tag.putFloat("Modifier", modifier);
    }

    public void load(CompoundTag tag) {
        isDynamic = tag.getBoolean("IsDynamic");
        startingPrice = tag.getLong("StartingPrice");
        currentPrice = tag.getDouble("CurrentPrice");
        extremum = tag.getLong("Extremum");
        varyRate = tag.getFloat("VaryRate");
        stableRate = tag.getFloat("StableRate");
        modifier = tag.getFloat("Modifier");
    }
}
