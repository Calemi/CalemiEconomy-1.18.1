package com.tm.calemieconomy.util.helper;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tm.calemicore.util.helper.ScreenHelper;
import com.tm.calemicore.util.helper.StringHelper;
import com.tm.calemicore.util.screen.ScreenRect;
import com.tm.calemieconomy.main.CEReference;
import com.tm.calemieconomy.util.IBlockCurrencyHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;

public class ScreenTabs {

    public static void addIconTab(PoseStack poseStack, int u, int v, int x, int y, int mouseX, int mouseY, TranslatableComponent... text) {
        //Tab
        RenderSystem.setShaderTexture(0, CEReference.GUI_TABS);
        ScreenHelper.drawRect(poseStack, u, v, new ScreenRect(x - 13, y, 13, 15), 0);

        //Hover Text
        ScreenRect hoverRect = new ScreenRect(x - 13, y, 13, 15);
        ScreenHelper.drawHoveringTextBox(poseStack, hoverRect, 170, mouseX, mouseY, 0xFFFFFF, text);
    }

    /**
     * Renders a currency tab.
     */
    public static void addCurrencyTab (PoseStack poseStack, int x, int y, int mouseX, int mouseY, IBlockCurrencyHolder currencyHolder) {

        String fullName = StringHelper.insertCommas(currencyHolder.getCurrency()) + " / " + CurrencyHelper.formatCurrency(currencyHolder.getCurrencyCapacity()).getString();

        int fullWidth = Minecraft.getInstance().font.width(fullName) + 6;

        ScreenRect rect = new ScreenRect(x - fullWidth, y, fullWidth, 15);
        String text = CurrencyHelper.formatCurrency(currencyHolder.getCurrency()).getString();

        if (rect.contains(mouseX, mouseY)) {
            text = fullName;
        }

        addLeftMessageTab(poseStack, text, x, y, 15);
    }

    public static void addLeftMessageTab(PoseStack poseStack, String text, int x, int y, int sizeY) {

        int width = Minecraft.getInstance().font.width(text) + 6;

        RenderSystem.setShaderTexture(0, CEReference.GUI_TABS);

        ScreenHelper.drawExpandableRect(poseStack, 0, 218, new ScreenRect(x - width, y, width, sizeY), 255, 22, 10);

        if (!text.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0, 0, 100);
            Minecraft.getInstance().font.draw(poseStack, text, x - width + 4, y + (float) (sizeY / 2) - 3, 0x555555);
            poseStack.popPose();
        }
    }
}
