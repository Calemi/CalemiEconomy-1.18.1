package com.tm.calemieconomy.event;

import com.tm.calemicore.util.helper.ScreenHelper;
import com.tm.calemieconomy.util.helper.CurrencyHelper;
import com.tm.calemieconomy.util.IItemCurrencyHolder;
import com.tm.calemieconomy.config.CEConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WalletOverlayEvent {

    /**
     * Handles the Wallet overlay that displays how much currency it has.
     */
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void render (RenderGameOverlayEvent.Post event) {

        //Checks if the config option is true
        if (!CEConfig.overlay.walletOverlay.get()) {
            return;
        }

        boolean isTextLayer = event.getType() == RenderGameOverlayEvent.ElementType.TEXT;
        boolean isHotbarLayer = event.getType() == RenderGameOverlayEvent.ElementType.LAYER;

        //Checks if the current render is on the "TEXT" or "HOTBAR" layer.
        if (isTextLayer || isHotbarLayer) {

            Minecraft mc = Minecraft.getInstance();
            Level level = mc.level;
            LocalPlayer player = mc.player;

            int scaledWidth = mc.getWindow().getGuiScaledWidth();
            int scaledHeight = mc.getWindow().getGuiScaledHeight();
            int midX = scaledWidth / 2;
            int midY = scaledHeight / 2;

            //Checks if the Player exists and is not in Spectator mode.
            if (player != null && !player.isSpectator()) {

                ItemStack walletStack = CurrencyHelper.getCurrentWallet(player);

                //Checks if the Player is not looking at a screen.
                if (mc.screen == null) {

                    if (!walletStack.isEmpty() && walletStack.getItem() instanceof IItemCurrencyHolder currencyHolder) {

                        CEConfig.WalletOverlayPosition walletPosition = CEConfig.WalletOverlayPosition.byName(CEConfig.overlay.walletOverlayPosition.get());

                        int currency = currencyHolder.getCurrency(walletStack);
                        MutableComponent currencyStr = CurrencyHelper.formatCurrency(currency);

                        int xOffsetStr = 0;
                        int xOffsetItem = 0;
                        int yOffset = 0;

                        //If the wallet position is "BOTTOM_LEFT", adds the appropriate offsets.
                        if (walletPosition == CEConfig.WalletOverlayPosition.BOTTOM_LEFT) {
                            yOffset = scaledHeight - 16;
                        }

                        //If the wallet position is "TOP_RIGHT", adds the appropriate offsets.
                        else if (walletPosition == CEConfig.WalletOverlayPosition.TOP_RIGHT) {
                            xOffsetStr = scaledWidth - Minecraft.getInstance().font.width(currencyStr) - 41;
                            xOffsetItem = scaledWidth - 20;
                        }

                        //If the wallet position is "BOTTOM_RIGHT", adds the appropriate offsets.
                        else if (walletPosition == CEConfig.WalletOverlayPosition.BOTTOM_RIGHT) {
                            yOffset = scaledHeight - 16;
                            xOffsetStr = scaledWidth - Minecraft.getInstance().font.width(currencyStr) - 41;
                            xOffsetItem = scaledWidth - 20;
                        }

                        //Draw string on the TEXT layer.
                        if (isTextLayer) Minecraft.getInstance().font.draw(event.getMatrixStack(), currencyStr, 21 + xOffsetStr, 4 + yOffset, 0xFFFFFFFF);
                        //Draw Item on the HOTBAR layer.
                        else ScreenHelper.drawItemStack(Minecraft.getInstance().getItemRenderer(), walletStack, 2 + xOffsetItem, yOffset);
                    }
                }
            }
        }
    }
}
