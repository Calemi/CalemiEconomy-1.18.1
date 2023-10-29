package com.tm.calemieconomy.file;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemieconomy.file.ScheduledRandomPriceModifiersFile;
import com.tm.calemieconomy.main.CEReference;
import com.tm.calemieconomy.util.helper.FileHelper;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Calendar;
import java.util.Random;

public final class ScheduledRandomPriceModifier {

    private static final Random random = new Random();

    private String name;

    private String pickedItem;
    private String[] possibleItems;

    private int cachedCycleIndex;
    private int daysUntilRefresh;

    private boolean buyMode;
    private float priceModifier;

    public ScheduledRandomPriceModifier(String name, String[] possibleItems, int daysUntilRefresh, boolean buyMode, float priceModifier) {
        this.name = name;

        this.pickedItem = "";
        this.possibleItems = possibleItems;

        cachedCycleIndex = -1;
        this.daysUntilRefresh = Mth.clamp(daysUntilRefresh, 1, Integer.MAX_VALUE);

        this.buyMode = buyMode;
        this.priceModifier = priceModifier;
    }

    public String getName() {
        return name;
    }

    public boolean isBuyMode() {
        return buyMode;
    }

    public float getPriceModifier() {
        return priceModifier;
    }

    public void checkForRefresh() {

        int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        int cycleIndex = dayOfYear / daysUntilRefresh;

        LogHelper.log(CEReference.MOD_NAME, "DAY: " + dayOfYear);
        LogHelper.log(CEReference.MOD_NAME, "CYCLE: " + cycleIndex);

        if (cycleIndex != cachedCycleIndex) {
            cachedCycleIndex = cycleIndex;
            refresh();
        }
    }

    private void refresh() {

        int possibleItemsSize = possibleItems.length;

        if (possibleItemsSize <= 1) {
            return;
        }

        while (true) {

            int chosenIndex = random.nextInt(possibleItemsSize);
            String chosenItem = possibleItems[chosenIndex];

            if (!chosenItem.equalsIgnoreCase(pickedItem)) {
                pickedItem = chosenItem;
                FileHelper.saveToFile("ScheduledRandomPriceModifiersFile", ScheduledRandomPriceModifiersFile.list);
                return;
            }
        }
    }

    public ItemStack getPickedStack() {

        ItemParser parser;

        try {
            parser = (new ItemParser(new StringReader(pickedItem), false)).parse();
        }
        catch (CommandSyntaxException e) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = new ItemStack(parser.getItem());
        stack.setTag(parser.getNbt());

        return stack;
    }
}