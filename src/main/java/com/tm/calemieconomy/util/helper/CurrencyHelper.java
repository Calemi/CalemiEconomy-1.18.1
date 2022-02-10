package com.tm.calemieconomy.util.helper;

import com.tm.calemicore.util.helper.StringHelper;
import com.tm.calemieconomy.init.InitItems;
import com.tm.calemieconomy.item.ItemWallet;
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
