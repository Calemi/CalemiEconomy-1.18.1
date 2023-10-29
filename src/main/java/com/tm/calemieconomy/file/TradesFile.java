package com.tm.calemieconomy.file;

import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tm.calemieconomy.util.helper.FileHelper;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class TradesFile {

    public static Map<String, TradeEntry> list;

    public static void init() {
        list = FileHelper.readFileOrCreate("Trades", getDefaults(), new TypeToken<Map<String, TradeEntry>>(){});
    }

    private static Map<String, TradeEntry> getDefaults() {

        Map<String, TradeEntry> entries = new HashMap<>();
        entries.put("test", new TradeEntry(true, true, "minecraft:cobblestone", 100, true, 50, 40, 1.0F, 1.0F));
        return entries;
    }

    public static final class TradeEntry {

        private final boolean buyMode;
        private final boolean adminMode;

        private final String stackForSale;
        private final int amount;

        private final boolean dynamicPrice;
        private final long startingPrice;
        private final long priceExtremum;
        private final float priceVaryRate;
        private final float priceStableRate;

        public TradeEntry(boolean buyMode, boolean adminMode, String stackForSale, int amount, boolean dynamicPrice, long startingPrice, long priceExtremum, float priceVaryRate, float priceStableRate) {
            this.buyMode = buyMode;
            this.adminMode = adminMode;
            this.stackForSale = stackForSale;
            this.amount = amount;
            this.dynamicPrice = dynamicPrice;
            this.startingPrice = startingPrice;
            this.priceExtremum = priceExtremum;
            this.priceVaryRate = priceVaryRate;
            this.priceStableRate = priceStableRate;
        }

        public boolean isBuyMode() {
            return buyMode;
        }

        public boolean isAdminMode() {
            return adminMode;
        }

        public ItemStack getStackForSale() {

            ItemParser parser;

            try {
                parser = (new ItemParser(new StringReader(stackForSale), false)).parse();
            } catch (CommandSyntaxException e) {
                return ItemStack.EMPTY;
            }

            ItemStack stack = new ItemStack(parser.getItem());
            stack.setTag(parser.getNbt());

            return stack;
        }


        public int getAmount() {
            return amount;
        }

        public boolean isDynamicPrice() {
            return dynamicPrice;
        }

        public long getStartingPrice() {
            return startingPrice;
        }

        public long getPriceExtremum() {
            return priceExtremum;
        }

        public float getPriceVaryRate() {
            return priceVaryRate;
        }

        public float getPriceStableRate() {
            return priceStableRate;
        }
    }
}