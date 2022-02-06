package com.tm.calemieconomy.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tm.calemicore.util.helper.MathHelper;
import com.tm.calemicore.util.helper.ScreenHelper;
import com.tm.calemicore.util.helper.StringHelper;
import com.tm.calemicore.util.screen.ScreenContainerBase;
import com.tm.calemicore.util.screen.ScreenRect;
import com.tm.calemicore.util.screen.widget.ButtonRect;
import com.tm.calemieconomy.util.helper.CurrencyHelper;
import com.tm.calemieconomy.util.IItemCurrencyHolder;
import com.tm.calemieconomy.init.InitItems;
import com.tm.calemieconomy.item.ItemCoin;
import com.tm.calemieconomy.item.ItemWallet;
import com.tm.calemieconomy.main.CEReference;
import com.tm.calemieconomy.menu.MenuWallet;
import com.tm.calemieconomy.packet.CEPacketHandler;
import com.tm.calemieconomy.packet.PacketExtractWalletCurrency;
import com.tm.calemieconomy.util.helper.ScreenTabs;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ScreenWallet extends ScreenContainerBase<MenuWallet> {

    private final ResourceLocation textureLocation = new ResourceLocation(CEReference.MOD_ID, "textures/gui/wallet.png");
    private final Player player;

    public ScreenWallet(MenuWallet menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        player = playerInv.player;
        inventoryLabelY = Integer.MAX_VALUE;
        imageHeight = 176;
    }

    public int getScreenX() {
        return (this.width - this.imageWidth) / 2;
    }

    public int getScreenY() {
        return (this.height - this.imageHeight) / 2;
    }

    /**
     * Gets the current Wallet Stack, returns empty if missing and closes the screen.
     */
    private ItemStack getCurrentWalletStack () {

        ItemStack walletStack = CurrencyHelper.getCurrentWallet(player);

        if (!walletStack.isEmpty()) {
            return walletStack;
        }

        else {
            player.closeContainer();
            return ItemStack.EMPTY;
        }
    }

    @Override
    protected void init() {
        super.init();

        for (int index = 0; index < 4; index++) {

            int id = index;
            addRenderableWidget(new ButtonRect(getScreenX() + 146, getScreenY() + 15 + (index * 18), 16, "+", (btn) -> addMoney(id)));
        }
    }

    /**
     * Called when a "+" button is pressed.
     * Adds money to the Player from the Wallet.
     */
    private void addMoney (int id) {

        ItemStack walletStack = getCurrentWalletStack();

        //Checks if there is a current Wallet.
        if (!walletStack.isEmpty()) {

            ItemWallet walletItem = (ItemWallet) walletStack.getItem();

            int price = ((ItemCoin) InitItems.COIN_COPPER.get()).value;
            if (id == 1) price = ((ItemCoin) InitItems.COIN_SILVER.get()).value;
            else if (id == 2) price = ((ItemCoin) InitItems.COIN_GOLD.get()).value;
            else if (id == 3) price = ((ItemCoin) InitItems.COIN_PLATINUM.get()).value;

            int multiplier = MathHelper.getShiftCtrlInt(1, 16, 64, 9 * 64);
            price *= multiplier;

            //If the Wallet's balance can afford the requested amount, give it to the player and sync the current balance.
            if (walletItem.getCurrency(walletStack) >= price) {

                CEPacketHandler.INSTANCE.sendToServer(new PacketExtractWalletCurrency(id, multiplier));
                walletItem.withdrawCurrency(walletStack, price);
            }
        }
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {

        if (this.textureLocation != null) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, this.textureLocation);
            ScreenHelper.drawRect(poseStack, 0, 0, new ScreenRect(this.getScreenX(), this.getScreenY(), this.imageWidth, this.imageHeight), 0);
        }

        ScreenHelper.drawItemStack(itemRenderer, new ItemStack(InitItems.COIN_COPPER.get()), getScreenX() + 127, getScreenY() + 15);
        ScreenHelper.drawItemStack(itemRenderer, new ItemStack(InitItems.COIN_SILVER.get()), getScreenX() + 127, getScreenY() + 33);
        ScreenHelper.drawItemStack(itemRenderer, new ItemStack(InitItems.COIN_GOLD.get()), getScreenX() + 127, getScreenY() + 51);
        ScreenHelper.drawItemStack(itemRenderer, new ItemStack(InitItems.COIN_PLATINUM.get()), getScreenX() + 127, getScreenY() + 69);

        ItemStack stack = getCurrentWalletStack();

        if (!stack.isEmpty() && stack.getItem() instanceof IItemCurrencyHolder currencyHolder) {
            ScreenHelper.drawCenteredString(poseStack, getScreenX() + getXSize() / 2 - 16, getScreenY() + 42, 0, 0x555555, new TextComponent(StringHelper.insertCommas(currencyHolder.getCurrency(stack))));
            ScreenHelper.drawCenteredString(poseStack, getScreenX() + getXSize() / 2 - 16, getScreenY() + 51, 0, 0x555555, new TranslatableComponent("ce.rc"));
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
        this.renderTooltip(poseStack, mouseX, mouseY);

        ScreenTabs.addIconTab(poseStack, 0, 0, getScreenX(), getScreenY() + 5, mouseX, mouseY, new TranslatableComponent("screen.tab.info.1"), new TranslatableComponent("screen.tab.info.2"));
    }
}
