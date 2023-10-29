package com.tm.calemieconomy.event.listener;

import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemieconomy.init.InitSounds;
import com.tm.calemieconomy.item.ItemCoin;
import com.tm.calemieconomy.main.CEReference;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CoinPickupSoundEvent {

    /**
     * Handles adding sound effect when coin is picked up.
     */
    @SubscribeEvent
    public void onItemPickup (PlayerEvent.ItemPickupEvent event) {

        if (event.getEntity() instanceof Player) {

            if (event.getStack().getItem() instanceof ItemCoin) {

                LogHelper.logCommon(CEReference.MOD_NAME, event.getPlayer().getLevel(), "COIN PICKUP SOUND");
                event.getPlayer().level.playSound(null, event.getPlayer().getBlockX(), event.getPlayer().getBlockY(), event.getPlayer().getBlockZ(), InitSounds.COIN.get(), SoundSource.PLAYERS, 0.1F, 1);
            }
        }
    }
}
