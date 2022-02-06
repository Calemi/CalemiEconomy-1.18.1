package com.tm.calemieconomy.event;

import com.tm.calemieconomy.util.helper.CurrencyHelper;
import com.tm.calemieconomy.init.InitKeyBindings;
import com.tm.calemieconomy.packet.CEPacketHandler;
import com.tm.calemieconomy.packet.PacketOpenWallet;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WalletKeyEvent {

    /**
     * Handles all key events.
     */
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onKeyInput (InputEvent.KeyInputEvent event) {

        //Checks if the Wallet key is pressed.
        if (InitKeyBindings.openWalletButton.isDown()) {

            Player player = Minecraft.getInstance().player;

            //Checks if the Player exists.
            if (player != null) {
                ItemStack walletStack = CurrencyHelper.getCurrentWallet(player);

                //If the player has a current Wallet, open its GUI.
                if (!walletStack.isEmpty()) {
                    CEPacketHandler.INSTANCE.sendToServer(new PacketOpenWallet());
                }
            }
        }
    }
}