package com.tm.calemieconomy.event.listener;

import com.tm.calemieconomy.util.helper.CurrencyHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WrenchLoreEvent {

    /**
     * Handles adding Lore to an Item storing currency.
     */
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onLoreEvent (ItemTooltipEvent event) {

        if (event.getItemStack().getTag() != null) {

            CompoundTag tag = event.getItemStack().getOrCreateTag();

            long currency = CurrencyHelper.loadFromNBT(tag);

            if (currency != 0) {
                event.getToolTip().add(new TextComponent(" "));
                CurrencyHelper.addCurrencyLore(event.getToolTip(), currency);
            }
        }
    }
}
