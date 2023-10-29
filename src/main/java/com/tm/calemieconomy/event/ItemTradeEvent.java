package com.tm.calemieconomy.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class ItemTradeEvent extends PlayerEvent {

    private final ItemStack stack;
    private final int amount;
    private final long price;

    public ItemTradeEvent(Player player, ItemStack stack, int amount, long price) {
        super(player);
        this.stack = stack;
        this.amount = amount;
        this.price = price;
    }

    public ItemStack getStack() {
        return stack;
    }

    public int getAmount() {
        return amount;
    }

    public long getPrice() {
        return price;
    }

    public static class Sell extends ItemTradeEvent {

        public Sell(Player player, ItemStack stack, int amount, long price) {
            super(player, stack, amount, price);
        }
    }

    public static class Buy extends ItemTradeEvent {

        public Buy(Player player, ItemStack stack, int amount, long price) {
            super(player, stack, amount, price);
        }
    }
}