package com.tm.calemieconomy.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemicore.util.helper.MathHelper;
import com.tm.calemicore.util.helper.ScreenHelper;
import com.tm.calemicore.util.helper.StringHelper;
import com.tm.calemicore.util.screen.ButtonRect;
import com.tm.calemicore.util.screen.ScreenContainerBase;
import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import com.tm.calemieconomy.config.CEConfig;
import com.tm.calemieconomy.main.CEReference;
import com.tm.calemieconomy.menu.MenuTradingPost;
import com.tm.calemieconomy.packet.CEPacketHandler;
import com.tm.calemieconomy.packet.PacketTradingPost;
import com.tm.calemieconomy.util.helper.ScreenTabs;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenTradingPost extends ScreenContainerBase<MenuTradingPost> {

    private final BlockEntityTradingPost post;
    private FakeSlot fakeSlot;
    private ButtonRect sellModeBtn;

    private final int upY = 40;
    private final int downY = 59;

    public ScreenTradingPost(MenuTradingPost menu, Inventory playerInv, Component useless) {
        super(menu, playerInv, menu.getBlockEntity().getDisplayName());
        textureLocation = new ResourceLocation(CEReference.MOD_ID, "textures/gui/trading_post.png");
        post = (BlockEntityTradingPost) getMenu().getBlockEntity();
        inventoryLabelY = Integer.MAX_VALUE;
        imageHeight = 232;
    }

    @Override
    protected void init() {
        super.init();

        fakeSlot = addRenderableWidget(new FakeSlot(getScreenX() + 80, getScreenY() + 19, this, itemRenderer, (btn) -> setFakeSlot()));
        fakeSlot.setItemStack(post.getStackForSale());

        //Subtract Amount
        addRenderableWidget(new ButtonRect(getScreenX() + 50, getScreenY() + upY, 16, "-", (btn) -> {
            changeAmount(-MathHelper.getShiftCtrlInt(1, 10, 100, 1000));
        }));
        //Add Amount
        addRenderableWidget(new ButtonRect(getScreenX() + 110, getScreenY() + upY, 16, "+", (btn) -> {
            changeAmount(MathHelper.getShiftCtrlInt(1, 10, 100, 1000));
        }));

        //Subtract Price
        addRenderableWidget(new ButtonRect(getScreenX() + 50, getScreenY() + downY, 16, "-", (btn) -> {
            changePrice(-MathHelper.getShiftCtrlInt(1, 10, 100, 1000));
        }));
        //Add Price
        addRenderableWidget(new ButtonRect(getScreenX() + 110, getScreenY() + downY, 16, "+", (btn) -> {
            changePrice(MathHelper.getShiftCtrlInt(1, 10, 100, 1000));
        }));

        //Reset Amount
        addRenderableWidget(new ButtonRect(getScreenX() + 128, getScreenY() + upY, 16, "R", (btn) -> {
            resetAmount();
        }));
        //Reset Price
        addRenderableWidget(new ButtonRect(getScreenX() + 128, getScreenY() + downY, 16, "R", (btn) -> {
            resetPrice();
        }));

        sellModeBtn = addRenderableWidget(new ButtonRect(getScreenX() + 21, getScreenY() + 19, 38, post.buyMode ? "screen.trading_post.btn.buying" : "screen.trading_post.btn.selling", (btn) -> toggleMode()));
        if (CEConfig.economy.tradingPostBroadcasts.get()) addRenderableWidget(new ButtonRect(getScreenX() + 105, getScreenY() + 19, 60, "screen.trading_post.btn.broadcast", (btn) -> broadcast()));
    }

    /**
     * Called when a fakeSlot button is pressed.
     * Sets fakeSlot's icon to the hovered Stack and syncs it.
     */
    private void setFakeSlot () {

        ItemStack stack = getMenu().getCarried().copy();
        stack.setCount(1);
        LogHelper.log(CEReference.MOD_NAME, stack);
        CEPacketHandler.INSTANCE.sendToServer(new PacketTradingPost("syncstack", post.getBlockPos(), stack));
        post.setStackForSale(stack);
        fakeSlot.setItemStack(stack);
    }

    /**
     * Called when a "-" or "+" amount button is pressed.
     * Adds to or subtracts from the amount value and syncs it.
     */
    private void changeAmount (int change) {
        post.tradeAmount = Mth.clamp(post.tradeAmount + change, 1, 64);
        syncOptionsToServer();
    }

    /**
     * Called when a "-" or "+" price button is pressed.
     * Adds to or subtracts from the price value and syncs it.
     */
    private void changePrice (int change) {
        post.tradePrice = Mth.clamp(post.tradePrice + change, 0, 9999);
        syncOptionsToServer();
    }

    /**
     * Called when a "R" amount button is pressed.
     * Resets the amount value and syncs it.
     */
    private void resetAmount () {
        post.tradeAmount = 1;
        syncOptionsToServer();
    }

    /**
     * Called when a "R" price button is pressed.
     * Resets the price value and syncs it.
     */
    private void resetPrice () {
        post.tradePrice = 0;
        syncOptionsToServer();
    }

    private void syncOptionsToServer() {
        CEPacketHandler.INSTANCE.sendToServer(new PacketTradingPost("syncoptions", post.getBlockPos(), post.tradeAmount, post.tradePrice));
    }

    /**
     * Called when a sellModeBtn is pressed.
     * Toggles the current mode and syncs it.
     */
    private void toggleMode () {
        post.buyMode = !post.buyMode;
        CEPacketHandler.INSTANCE.sendToServer(new PacketTradingPost("syncmode", post.getBlockPos(), post.buyMode));
    }

    /**
     * Called when a broadcastBtn is pressed.
     * Sends a message to everyone containing information about the Trading Post.
     */
    private void broadcast () {
        CEPacketHandler.INSTANCE.sendToServer(new PacketTradingPost("broadcast", post.getBlockPos()));
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        super.renderBg(poseStack, partialTick, mouseX, mouseY);

        // Titles
        minecraft.font.draw(poseStack, new TranslatableComponent("screen.trading_post.txt.amount"), getScreenX() + 10, getScreenY() + upY + 4, 0x555555);
        minecraft.font.draw(poseStack, new TranslatableComponent("screen.trading_post.txt.price"), getScreenX() + 10, getScreenY() + downY + 4, 0x555555);

        ScreenHelper.drawCenteredString(poseStack, getScreenX() + getXSize() / 2, getScreenY() + upY + 4, 0, 0x555555, new TextComponent(StringHelper.insertCommas(post.tradeAmount)));
        ScreenHelper.drawCenteredString(poseStack, getScreenX() + getXSize() / 2, getScreenY() + downY + 4, 0, 0x555555, new TextComponent(StringHelper.insertCommas(post.tradePrice)));

        sellModeBtn.setMessage(new TranslatableComponent(post.buyMode ? "screen.trading_post.btn.buying" : "screen.trading_post.btn.selling"));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);

        ScreenTabs.addIconTab(poseStack, 0, 0, getScreenX(), getScreenY() + 5, mouseX, mouseY, new TranslatableComponent("screen.tab.info.1"), new TranslatableComponent("screen.tab.info.2"));

        fakeSlot.renderButton(poseStack, mouseX, mouseY, partialTick);
    }
}
