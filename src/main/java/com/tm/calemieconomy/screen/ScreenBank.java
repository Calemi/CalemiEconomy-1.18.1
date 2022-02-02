package com.tm.calemieconomy.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tm.calemieconomy.main.CEReference;
import com.tm.calemieconomy.menu.MenuBank;
import com.tm.calemieconomy.packet.CEPacketHandler;
import com.tm.calemieconomy.packet.PacketSyncContainerCurrency;
import com.tm.calemieconomy.util.helper.ScreenTabs;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenBank extends AbstractContainerScreen<MenuBank> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(CEReference.MOD_ID, "textures/gui/bank.png");

    public ScreenBank(MenuBank menu, Inventory playerInv, Component title) {
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
        inventoryLabelY = Integer.MAX_VALUE;

        addRenderableWidget(new ButtonRect(getScreenX() + (imageWidth / 2) + 30 - 25, getScreenY() + 40, 50, "screen.bank.btn.withdraw", (btn) -> withdraw()));
        addRenderableWidget(new ButtonRect(getScreenX() + (imageWidth / 2) - 30 - 25, getScreenY() + 40, 50, "screen.bank.btn.deposit", (btn) -> deposit()));
    }

    /**
     * Called when the withdraw button is pressed.
     * Handles withdrawals from the Bank.
     */
    private void withdraw () {
        getMenu().withdrawCurrency(50);
        CEPacketHandler.INSTANCE.sendToServer(new PacketSyncContainerCurrency(getMenu().getCurrency()));
    }

    /**
     * Called when the deposit button is pressed.
     * Handles deposits from the Bank.
     */
    private void deposit () {
        getMenu().depositCurrency(50);
        CEPacketHandler.INSTANCE.sendToServer(new PacketSyncContainerCurrency(getMenu().getCurrency()));
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

        ScreenTabs.addCurrencyTab(poseStack, getScreenX(), getScreenY() + 5, mouseX, mouseY, getMenu());
    }
}
