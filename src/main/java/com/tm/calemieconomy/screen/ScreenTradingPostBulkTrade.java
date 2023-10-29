package com.tm.calemieconomy.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tm.calemicore.util.helper.ScreenHelper;
import com.tm.calemicore.util.screen.ScreenContainerBase;
import com.tm.calemicore.util.screen.ScreenRect;
import com.tm.calemicore.util.screen.widget.SmoothButton;
import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import com.tm.calemieconomy.main.CEReference;
import com.tm.calemieconomy.menu.MenuTradingPostBulkTrade;
import com.tm.calemieconomy.packet.CEPacketHandler;
import com.tm.calemieconomy.packet.PacketBankDeposit;
import com.tm.calemieconomy.packet.PacketTradingPostBulkTrade;
import com.tm.calemieconomy.packet.PacketTradingPostSellAll;
import com.tm.calemieconomy.screen.base.ScrollableLongField;
import com.tm.calemieconomy.util.IItemCurrencyHolder;
import com.tm.calemieconomy.util.helper.CEScreenHelper;
import com.tm.calemieconomy.util.helper.CurrencyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ScreenTradingPostBulkTrade extends ScreenContainerBase<MenuTradingPostBulkTrade> {

    private final BlockEntityTradingPost post;

    private ScrollableLongField sets;
    private SmoothButton tradeBtn;
    private SmoothButton sellAllBtn;

    public ScreenTradingPostBulkTrade(MenuTradingPostBulkTrade menu, Inventory playerInv, Component useless) {
        super(menu, playerInv, new TranslatableComponent("container.trading_post_bulk_trade"));
        textureLocation = new ResourceLocation(CEReference.MOD_ID, "textures/gui/trading_post_bulk_trade.png");
        post = (BlockEntityTradingPost) getMenu().getBlockEntity();
        inventoryLabelY = Integer.MAX_VALUE;
        imageHeight = 184;
    }

    @Override
    protected void init() {
        super.init();

        sets = new ScrollableLongField(1, 1, 1000, getScreenX() + (imageWidth / 2), getScreenY() + 66, 0x555555, () -> {});

        tradeBtn = addRenderableWidget(new SmoothButton(getScreenX() + 52, getScreenY() + 81, 72, post.buyMode ? "screen.trading_post_bulk_trade.btn.sell" : "screen.trading_post_bulk_trade.btn.buy", (btn) -> trade()));

        if (post.buyMode) {
            tradeBtn.setPosition(getScreenX() + 7, getScreenY() + 81);

            sellAllBtn = addRenderableWidget(new SmoothButton(getScreenX() + 97, getScreenY() + 81, 72, "screen.trading_post_bulk_trade.btn.sell_all", (btn) -> sellAll()));
        }
    }

    private void trade () {
        CEPacketHandler.INSTANCE.sendToServer(new PacketTradingPostBulkTrade(post.getBlockPos(), (int)sets.getValue()));
    }

    private void sellAll() {
        CEPacketHandler.INSTANCE.sendToServer(new PacketTradingPostSellAll(post.getBlockPos()));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        sets.mouseScrolled(mouseX, mouseY, delta);
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        super.renderBg(poseStack, partialTick, mouseX, mouseY);

        minecraft.font.draw(poseStack, new TextComponent("x" + post.tradeAmount * sets.getValue()), getScreenX() + (imageWidth / 2) + 15, getScreenY() + 23, 0x555555);

        minecraft.font.draw(poseStack, new TranslatableComponent("screen.trading_post_bulk_trade.txt.price"), getScreenX() + 10, getScreenY() + 40, 0x555555);
        CEScreenHelper.drawCurrencyStringCentered(poseStack, getScreenX() + imageWidth / 2, getScreenY() + 40, mouseX, mouseY, post.price.getBulkPrice(sets.getValue()));

        minecraft.font.draw(poseStack, new TranslatableComponent("screen.trading_post_bulk_trade.txt.sets"), getScreenX() + 10, getScreenY() + 66, 0x555555);

        ItemStack walletStack = CurrencyHelper.getCurrentWallet(minecraft.player);

        if (!walletStack.isEmpty() && walletStack.getItem() instanceof IItemCurrencyHolder currencyHolder) {
            minecraft.font.draw(poseStack, new TranslatableComponent("screen.trading_post_bulk_trade.txt.wallet"), getScreenX() + 10, getScreenY() + 50, 0x555555);
            CEScreenHelper.drawCurrencyStringCentered(poseStack, getScreenX() + imageWidth / 2, getScreenY() + 50, mouseX, mouseY, currencyHolder.getCurrency(walletStack));
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);

        MutableComponent amountDelta = new TranslatableComponent("screen.scroll.change").append(": ").append(CurrencyHelper.insertCommasLong(sets.getDelta(), false)).append(" ").append(new TranslatableComponent("screen.scroll.shift"));
        sets.setText(new TextComponent("" + sets.getValue()));
        sets.setTooltip(amountDelta);
        sets.render(poseStack, mouseX, mouseY);

        List<Component> list = post.getStackForSale().getTooltipLines(Minecraft.getInstance().player, TooltipFlag.Default.NORMAL);
        MutableComponent[] tooltip = new MutableComponent[list.size()];

        for (int i = 0 ; i < tooltip.length; i++) {
            tooltip[i] = list.get(i).copy();
        }

        int stackForSaleX = getScreenX() + 80;
        int stackForSaleY = getScreenY() + 19;

        ScreenHelper.drawItemStack(Minecraft.getInstance().getItemRenderer(), post.getStackForSale(), stackForSaleX, stackForSaleY);
        ScreenHelper.drawHoveringTextBox(poseStack, new ScreenRect(stackForSaleX, stackForSaleY, 16, 16), 0, mouseX, mouseY, 0xFFFFFF, tooltip);
    }
}
