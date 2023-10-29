package com.tm.calemieconomy.util.helper;

import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemieconomy.init.InitItems;
import com.tm.calemieconomy.item.ItemWallet;
import com.tm.calemieconomy.main.CEReference;
import com.tm.calemieconomy.main.CalemiEconomy;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

import java.text.DecimalFormat;
import java.util.List;

public class CurrencyHelper {

    /**
     * Used to find the Player's Wallet. If multiple, chooses by priority.
     */
    public static ItemStack getCurrentWallet(Player player) {

        //Priority #1 - Held mainhand.
        if (player.getMainHandItem().getItem() instanceof ItemWallet) {
            return player.getMainHandItem();
        }

        //Priority #2 - Held offhand.
        if (player.getOffhandItem().getItem() instanceof ItemWallet) {
            return player.getOffhandItem();
        }

        //Priority #3 - Curios slot.
        if (CalemiEconomy.isCuriosLoaded) {

            if (CuriosApi.getCuriosHelper().findEquippedCurio(InitItems.WALLET.get(), player).isPresent()) {
                return CuriosApi.getCuriosHelper().findEquippedCurio(InitItems.WALLET.get(), player).get().right;
            }
        }

        //Priority #4 - Inventory (lowest slot id wins).
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {

            ItemStack stack = player.getInventory().getItem(i);

            if (stack.getItem() instanceof ItemWallet) {
                return stack;
            }
        }

        //No Wallet was found.
        return ItemStack.EMPTY;
    }

    public static MutableComponent formatCurrency(long amount, boolean showFullNumber) {

        if (!showFullNumber) {

            if (amount > 999999999999999L) {
                return new TextComponent(insertCommasLong(amount / 1000000000000000D, true)).append("q ").append(new TranslatableComponent("ce.rc"));
            }

            if (amount > 999999999999L) {
                return new TextComponent(insertCommasLong(amount / 1000000000000D, true)).append("T ").append(new TranslatableComponent("ce.rc"));
            }

            if (amount > 999999999) {
                return new TextComponent(insertCommasLong(amount / 1000000000D, true)).append("B ").append(new TranslatableComponent("ce.rc"));
            }

            if (amount > 999999) {
                return new TextComponent(insertCommasLong(amount / 1000000D, true)).append("M ").append(new TranslatableComponent("ce.rc"));
            }
        }

        return new TextComponent(insertCommasLong(amount, false)).append(new TranslatableComponent("ce.rc"));
    }

    public static String insertCommasLong(double amount, boolean showDecimals) {
        DecimalFormat formatter = new DecimalFormat(showDecimals ? "#,###.00" : "#,###");
        return formatter.format(amount);
    }

    public static void addCurrencyLore(List<Component> tooltip, long currentCurrency) {
        addCurrencyLore(tooltip, currentCurrency, 0);
    }

    public static void addCurrencyLore(List<Component> tooltip, long currentCurrency, long maxCurrency) {

        MutableComponent amount = formatCurrency(currentCurrency, false);

        if (maxCurrency > 0) {
            amount.append(" / ").append(formatCurrency(maxCurrency, false));
        }

        tooltip.add(new TranslatableComponent("lore.currency").withStyle(ChatFormatting.GRAY).append(" ").append(amount.withStyle(ChatFormatting.GOLD)));
    }

    public static long loadFromNBT(CompoundTag tag) {
        CompoundTag currencyTag = tag.getCompound("Currency");
        return currencyTag.getLong("Amount");
    }

    public static void saveToNBT(CompoundTag tag, long amount) {
        CompoundTag currencyTag = new CompoundTag();
        currencyTag.putLong("Amount", amount);
        tag.put("Currency", currencyTag);
    }

    public static long getAmountToAdd(long startingValue, long amountToAdd, long maxAmount) {
        return startingValue + amountToAdd > maxAmount ? 0 : amountToAdd;
    }

    public static long getAmountToFill(long startingValue, long amountToAdd, long maxAmount) {
        return startingValue + amountToAdd > maxAmount ? maxAmount - startingValue : 0;
    }
}
