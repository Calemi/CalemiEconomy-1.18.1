package com.tm.calemieconomy.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tm.calemieconomy.main.CEReference;
import com.tm.calemieconomy.menu.MenuTradingPost;
import com.tm.calemieconomy.util.helper.ScreenTabs;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenTradingPost extends AbstractContainerScreen<MenuTradingPost> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(CEReference.MOD_ID, "textures/gui/trading_post.png");

    public ScreenTradingPost(MenuTradingPost menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
    }

    private int getScreenX() {
        return (width - imageWidth) / 2;
    }

    private int getScreenY() {
        return (height - imageHeight) / 2;
    }

    @Override
    protected void init() {
        super.init();
        titleLabelY = -27;
        inventoryLabelY = Integer.MAX_VALUE;
        imageHeight = 232;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
        this.renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        blit(poseStack, getScreenX(), getScreenY(), 0, 0, imageWidth, imageHeight);

        //ScreenTabs.addCurrencyTab(poseStack, getScreenX(), getScreenY() + 5, mouseX, mouseY, getMenu());
    }
}
