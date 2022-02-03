package com.tm.calemieconomy.api;

import com.tm.calemicore.util.helper.StringHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.List;

public class CurrencyHelper {

    public static MutableComponent formatCurrency(int amount) {
        return new TextComponent(StringHelper.insertCommas(amount)).append(new TranslatableComponent("ce.rc"));
    }

    public static void addCurrencyLore(List<Component> tooltip, int currentCurrency) {
        addCurrencyLore(tooltip, currentCurrency, 0);
    }

    public static void addCurrencyLore(List<Component> tooltip, int currentCurrency, int maxCurrency) {

        MutableComponent amount = formatCurrency(currentCurrency);

        if (maxCurrency > 0) {
            amount.append(" / ").append(formatCurrency(maxCurrency));
        }

        tooltip.add(new TranslatableComponent("lore.currency").withStyle(ChatFormatting.GRAY).append(" ").append(amount.withStyle(ChatFormatting.GOLD)));
    }

    public static int loadFromNBT(CompoundTag tag) {
        CompoundTag currencyTag = tag.getCompound("Currency");
        return currencyTag.getInt("Amount");
    }

    public static void saveToNBT(CompoundTag tag, int amount) {
        CompoundTag currencyTag = new CompoundTag();
        currencyTag.putInt("Amount", amount);
        tag.put("Currency", currencyTag);
    }
}
