package com.tm.calemieconomy.util.helper;

import com.tm.calemicore.main.CCReference;
import com.tm.calemicore.util.helper.LogHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CEContainerHelper {

    /**
     * @param container The Container to test.
     * @param stack     The Item Stack to test.
     * @param amount    The amount to check for.
     * @return true, if the given Item Stack can be inserted in the Container.
     */
    public static boolean canInsertStack(Container container, ItemStack stack, int amount, int slotStart, int slotAmount) {

        int amountLeft = amount;

        for (int slotIndex = slotStart; slotIndex < slotAmount; slotIndex++) {

            ItemStack stackInSlot = container.getItem(slotIndex);

            if (!container.canPlaceItem(slotIndex, stack)) {
                continue;
            }

            if (stackInSlot.isEmpty()) {
                amountLeft -= stack.getMaxStackSize();
                continue;
            }

            if (!ItemStack.isSameItemSameTags(stackInSlot, stack)) {
                continue;
            }

            int spaceLeftInStack = stack.getMaxStackSize() - stackInSlot.getCount();
            amountLeft -= spaceLeftInStack;
        }

        return amountLeft <= 0;
    }

    /**
     * Inserts the given ItemStack into the Container.
     * @param container The Container to insert in.
     * @param stack     The ItemStack to insert.
     * @param amount    The amount to insert.
     */
    public static void insertStack(Container container, ItemStack stack, int amount, int slotStart, int slotAmount) {

        if (!canInsertStack(container, stack, amount, slotStart, slotAmount)) {
            return;
        }

        int amountLeft = amount;
        int maxStackSize = stack.getMaxStackSize();

        for (int slotIndex = slotStart; slotIndex < slotAmount; slotIndex++) {

            if (amountLeft <= 0) break;

            ItemStack stackInSlot = container.getItem(slotIndex);

            if (stackInSlot.isEmpty()) {

                int insertAmount = Mth.clamp(amountLeft, 0, maxStackSize);
                LogHelper.log(CCReference.MOD_NAME, insertAmount);

                ItemStack stackCopy = stack.copy();
                stackCopy.setCount(insertAmount);
                container.setItem(slotIndex, stackCopy);

                amountLeft -= insertAmount;

                continue;
            }

            if (!ItemStack.isSameItemSameTags(stackInSlot, stack)) {
                continue;
            }

            int insertAmount = Mth.clamp(amountLeft, 0, maxStackSize - stackInSlot.getCount());

            stackInSlot.setCount(stackInSlot.getCount() + insertAmount);
            amountLeft -= insertAmount;
        }
    }
}
