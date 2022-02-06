package com.tm.calemieconomy.event;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.helper.RayTraceHelper;
import com.tm.calemicore.util.helper.ScreenHelper;
import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import com.tm.calemieconomy.config.CEConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TradingPostOverlayEvent {

    /**
     * Handles the Trading Post overlay when the cursor is over it.
     */
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void render (RenderGameOverlayEvent.Post event) {

        if (!CEConfig.economy.tradingPostOverlay.get()) {
            return;
        }

        //Checks if the current render is on the "HOTBAR" layer, so we can use transparency.
        if (event.getType() == RenderGameOverlayEvent.ElementType.CHAT) {

            Minecraft mc = Minecraft.getInstance();
            ClientLevel level = mc.level;
            LocalPlayer player = mc.player;

            //Checks if the World and Player exists.
            if (level != null && player != null) {

                int scaledWidth = mc.getWindow().getGuiScaledWidth();
                int scaledHeight = mc.getWindow().getGuiScaledHeight();
                int midX = scaledWidth / 2;
                int midY = scaledHeight / 2;

                RayTraceHelper.BlockTrace blockTrace = RayTraceHelper.rayTraceBlock(level, player, 5);

                //Checks if the trace hit a block.
                if (blockTrace != null) {

                    Location hit = blockTrace.getHit();

                    //Check if the hit was a Trading Post
                    if (hit.getBlockEntity() instanceof BlockEntityTradingPost post) {

                        //Checks if the Trading Post has a valid trade.
                        if (post.hasValidTradeOffer) {

                            ItemStack stackForSale = post.getStackForSale();

                            MutableComponent postName = new TextComponent(post.getSecurityProfile().getOwnerName()).append("'s ").append(new TranslatableComponent("unit.trading_post.name"));
                            MutableComponent info = post.getTradeInfo(false);

                            ScreenHelper.drawTextBox(event.getMatrixStack(), midX - 3, midY + 12, 0, true, 0xFFFFFF, postName, info);
                        }
                    }
                }
            }
        }
    }
}
