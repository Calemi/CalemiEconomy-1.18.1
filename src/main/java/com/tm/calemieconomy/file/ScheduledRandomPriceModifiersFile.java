package com.tm.calemieconomy.file;

import com.google.gson.reflect.TypeToken;
import com.tm.calemieconomy.util.helper.FileHelper;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public class ScheduledRandomPriceModifiersFile {

    public static ArrayList<ScheduledRandomPriceModifier> list;

    public static void init() {
        list = FileHelper.readFileOrCreate("ScheduledRandomPriceModifiersFile", getDefaults(), new TypeToken<ArrayList<ScheduledRandomPriceModifier>>() {});
    }

    private static ArrayList<ScheduledRandomPriceModifier> getDefaults() {
        ArrayList<ScheduledRandomPriceModifier> entries = new ArrayList<>();
        entries.add(new ScheduledRandomPriceModifier("Test", new String[]{"minecraft:bedrock", "minecraft:barrier"}, 3, true, 1.5F));
        return entries;
    }

    public static float getModifier(ItemStack stack, boolean buyMode) {

        float modifier = 1;

        for (ScheduledRandomPriceModifier priceModifier : list) {

            if (priceModifier.isBuyMode() == buyMode) {

                if (stack.sameItem(priceModifier.getPickedStack())) {
                    modifier *= priceModifier.getPriceModifier();
                }
            }
        }

        return modifier;
    }
}