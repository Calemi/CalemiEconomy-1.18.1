package com.tm.calemieconomy.menu;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlotFilter extends Slot {

    private final List<Item> itemFilters;

    public SlotFilter(Container container, int index, int x, int y, Item... filters) {
        super(container, index, x, y);
        this.itemFilters = new ArrayList<>(Arrays.asList(filters));
    }

    @Override
    public boolean mayPlace(ItemStack stack) {

        if (itemFilters != null) {
            for (Item itemFilter : itemFilters) {
                if (itemFilter == stack.getItem()) return true;
            }
        }

        return false;
    }
}
