package com.tm.calemieconomy.event;

import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import com.tm.calemieconomy.file.ScheduledRandomPriceModifier;
import com.tm.calemieconomy.file.ScheduledRandomPriceModifiersFile;
import com.tm.calemieconomy.util.TradingPostHelper;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ScheduledRandomPriceModifierHandler {

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {

        if (event.world.isClientSide()) {
            return;
        }

        if (event.world.getGameTime() % 20 == 0) {

            for (ScheduledRandomPriceModifier priceModifier : ScheduledRandomPriceModifiersFile.list) {
                priceModifier.checkForRefresh();
            }

            for (BlockPos pos : TradingPostHelper.allTradingPosts.keySet()) {

                if (event.world.isLoaded(pos)) {

                    BlockEntityTradingPost post = TradingPostHelper.allTradingPosts.get(pos);
                    post.price.modifier = ScheduledRandomPriceModifiersFile.getModifier(post.getStackForSale(), post.buyMode);
                    post.markUpdated();
                }
            }
        }
    }
}
