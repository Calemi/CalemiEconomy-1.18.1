package com.tm.calemieconomy.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tm.calemicore.util.screen.ScreenContainerBase;
import com.tm.calemicore.util.screen.widget.FakeSlot;
import com.tm.calemicore.util.screen.widget.SmoothButton;
import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import com.tm.calemieconomy.config.CEConfig;
import com.tm.calemieconomy.main.CEReference;
import com.tm.calemieconomy.menu.MenuTradingPost;
import com.tm.calemieconomy.packet.*;
import com.tm.calemieconomy.screen.base.ScrollableLongField;
import com.tm.calemieconomy.util.helper.CurrencyHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenTradingPost extends ScreenContainerBase<MenuTradingPost> {

    private final BlockEntityTradingPost post;
    private FakeSlot fakeSlot;
    private SmoothButton buyModeBtn;

    private ScrollableLongField amount;
    private ScrollableLongField price;

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

        amount = new ScrollableLongField(post.tradeAmount, 1, 1728, getScreenX() + (imageWidth / 2), getScreenY() + 44, 0x555555, () -> {
            CEPacketHandler.INSTANCE.sendToServer(new PacketTradingPostSyncAmount(post.getBlockPos(), (int)amount.getValue()));
        });

        price = new ScrollableLongField(post.price.getStartingPrice(), 0, CEConfig.economy.walletCurrencyCapacity.get(), getScreenX() + (imageWidth / 2), getScreenY() + 63, 0x555555, () -> {
            CEPacketHandler.INSTANCE.sendToServer(new PacketTradingPostSyncPrice(post.getBlockPos(), price.getValue()));
        });

        buyModeBtn = addRenderableWidget(new SmoothButton(getScreenX() + 21, getScreenY() + 19, 38, post.buyMode ? "screen.trading_post.btn.buying" : "screen.trading_post.btn.selling", (btn) -> toggleMode()));
        if (CEConfig.economy.tradingPostBroadcasts.get()) addRenderableWidget(new SmoothButton(getScreenX() + 105, getScreenY() + 19, 60, "screen.trading_post.btn.broadcast", (btn) -> broadcast()));

        SmoothButton advancedOptions = addRenderableWidget(new SmoothButton(getScreenX() + 149, getScreenY() + 59, 16, "screen.trading_post.btn.advanced_options", (btn) -> advancedOptions()));
    }

    private void setFakeSlot () {

        ItemStack stack = getMenu().getCarried().copy();
        stack.setCount(1);
        CEPacketHandler.INSTANCE.sendToServer(new PacketTradingPostSyncStack(post.getBlockPos(), stack));
        post.setStackForSale(stack);
        fakeSlot.setItemStack(stack);
    }

    private void toggleMode () {
        post.buyMode = !post.buyMode;
        CEPacketHandler.INSTANCE.sendToServer(new PacketTradingPostSyncBuyMode(post.getBlockPos(), post.buyMode));
    }

    private void broadcast () {
        CEPacketHandler.INSTANCE.sendToServer(new PacketTradingPostBroadcast(post.getBlockPos()));
    }

    private void advancedOptions() {
        minecraft.setScreen(new ScreenTradingPostAdvancedOptions(post));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {

        amount.mouseScrolled(mouseX, mouseY, delta);

        price.mouseScrolled(mouseX, mouseY, delta);

        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        super.renderBg(poseStack, partialTick, mouseX, mouseY);

        // Titles
        minecraft.font.draw(poseStack, new TranslatableComponent("screen.trading_post.txt.amount"), getScreenX() + 10, getScreenY() + 44, 0x555555);
        minecraft.font.draw(poseStack, new TranslatableComponent("screen.trading_post.txt.price"), getScreenX() + 10, getScreenY() + 63, 0x555555);

        buyModeBtn.setMessage(new TranslatableComponent(post.buyMode ? "screen.trading_post.btn.buying" : "screen.trading_post.btn.selling"));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);

        MutableComponent amountDelta = new TranslatableComponent("screen.scroll.change").append(": ").append(CurrencyHelper.insertCommasLong(amount.getDelta(), false)).append(" ").append(new TranslatableComponent("screen.scroll.shift"));
        amount.setText(new TextComponent("" + amount.getValue()));
        amount.setTooltip(amountDelta);
        amount.render(poseStack, mouseX, mouseY);

        MutableComponent priceDelta = new TranslatableComponent("screen.scroll.change").append(": ").append(CurrencyHelper.formatCurrency(price.getDelta(), true).append(" ").append(new TranslatableComponent("screen.scroll.shift")));
        MutableComponent priceCurrent = new TranslatableComponent("screen.scroll.current").append(": ").append(new TextComponent(CurrencyHelper.insertCommasLong(price.getValue(), false)));
        price.setText(CurrencyHelper.formatCurrency(price.getValue(), false));
        price.setTooltip(priceDelta, priceCurrent);
        price.render(poseStack, mouseX, mouseY);

        fakeSlot.renderButton(poseStack, mouseX, mouseY, partialTick);
    }
}
