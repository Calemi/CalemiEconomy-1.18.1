package com.tm.calemieconomy.util.helper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemicore.util.helper.ScreenHelper;
import com.tm.calemicore.util.helper.StringHelper;
import com.tm.calemicore.util.screen.ScreenRect;
import com.tm.calemieconomy.api.CurrencyHelper;
import com.tm.calemieconomy.api.ICurrencyHolder;
import com.tm.calemieconomy.main.CEReference;
import net.minecraft.client.Minecraft;

public class ScreenTabs {

    /**
     * Renders a currency tab.
     */
    public static void addCurrencyTab (PoseStack poseStack, int x, int y, int mouseX, int mouseY, ICurrencyHolder currencyHolder) {

        String fullName = StringHelper.insertCommas(currencyHolder.getCurrency()) + " / " + CurrencyHelper.formatCurrency(currencyHolder.getCurrencyCapacity()).getString();

        int fullWidth = Minecraft.getInstance().font.width(fullName) + 6;

        ScreenRect rect = new ScreenRect(x - fullWidth, y, fullWidth, 15);
        String text = CurrencyHelper.formatCurrency(currencyHolder.getCurrency()).getString();

        if (rect.contains(mouseX, mouseY)) {
            text = fullName;
        }

        addLeftInfoTab(poseStack, text, x, y, 15);
    }

    public static void addLeftInfoTab (PoseStack poseStack, String text, int x, int y, int sizeY) {

        int width = Minecraft.getInstance().font.width(text) + 6;

        ScreenHelper.bindGuiTexture(CEReference.GUI_TEXTURES);
        ScreenHelper.drawCappedRect(poseStack, x - width, y, 0, 218, 10, width, sizeY, 255, 22);

        if (!text.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0, 0, 5);
            Minecraft.getInstance().font.draw(poseStack, text, x - width + 4, y + (float) (sizeY / 2) - 3, 0x555555);
            poseStack.popPose();
        }
    }
}
