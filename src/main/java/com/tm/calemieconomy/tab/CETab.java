package com.tm.calemieconomy.tab;

import com.tm.calemieconomy.init.InitItems;
import com.tm.calemieconomy.main.CEReference;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class CETab extends CreativeModeTab {

    public CETab() {
        super(CEReference.MOD_ID + ".tabMain");
    }

    @Override
    public ItemStack makeIcon () {
        return new ItemStack(InitItems.COIN_GOLD.get());
    }
}
