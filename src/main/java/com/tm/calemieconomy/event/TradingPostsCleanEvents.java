package com.tm.calemieconomy.event;

import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import com.tm.calemieconomy.util.TradingPostHelper;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TradingPostsCleanEvents {

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {

        if (event.world.isClientSide()) {
            return;
        }

        if (event.world.getGameTime() % 20 == 0) {

            for (BlockPos pos : TradingPostHelper.allTradingPosts.keySet()) {

                if (event.world.isLoaded(pos)) {

                    if (!(event.world.getBlockEntity(pos) instanceof BlockEntityTradingPost)) {
                        TradingPostHelper.allTradingPosts.remove(pos);
                        return;
                    }
                }
            }
        }
    }
}
