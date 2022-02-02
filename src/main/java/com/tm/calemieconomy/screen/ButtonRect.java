package com.tm.calemieconomy.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tm.calemicore.util.helper.ScreenHelper;
import com.tm.calemicore.util.screen.ScreenRect;
import com.tm.calemieconomy.main.CEReference;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ButtonRect extends Button {

    public final ScreenRect rect;

    /**
     * Used as the basic button for anything in the mod.
     * @param textKey The text rendered on the button.
     * @param onPress Called when the button is pressed.
     */
    public ButtonRect (int x, int y, int width, String textKey, Button.OnPress onPress) {
        super(width,16, x, y, new TranslatableComponent(textKey), onPress);

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = 16;

        rect = new ScreenRect(x, y, width, 16);
    }

    public void setPosition (int x, int y) {
        rect.x = x;
        this.x = x;
        rect.y = y;
        this.y = y;
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {

        if (visible) {

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            ScreenHelper.bindGuiTexture(CEReference.GUI_TEXTURES);

            if (rect.contains(mouseX, mouseY)) {
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
                isHovered = true;
            }

            else {
                RenderSystem.setShaderColor(0.8F, 0.8F, 0.8F, alpha);
                isHovered = false;
            }

            if (!active) {
                RenderSystem.setShaderColor(0.5F, 0.5F, 0.5F, alpha);
            }

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();

            ScreenHelper.drawCappedRect(poseStack, rect.x, rect.y, 0, 240, 5, rect.width, rect.height, 256, 16);
            ScreenHelper.drawCenteredString(poseStack, (TranslatableComponent) getMessage(), rect.x + (rect.width / 2), rect.y + (rect.height - 8) / 2, 100, 0xFFFFFF);
        }
    }
}
