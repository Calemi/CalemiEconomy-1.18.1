package com.tm.calemieconomy.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tm.calemicore.util.screen.ScreenContainerBase;
import com.tm.calemicore.util.screen.widget.SmoothButton;
import com.tm.calemieconomy.blockentity.BlockEntityBank;
import com.tm.calemieconomy.config.CEConfig;
import com.tm.calemieconomy.main.CEReference;
import com.tm.calemieconomy.menu.MenuBank;
import com.tm.calemieconomy.packet.CEPacketHandler;
import com.tm.calemieconomy.packet.PacketBankDeposit;
import com.tm.calemieconomy.packet.PacketBankSyncTransactionAmount;
import com.tm.calemieconomy.packet.PacketBankWithdraw;
import com.tm.calemieconomy.screen.base.ScrollableLongField;
import com.tm.calemieconomy.util.IItemCurrencyHolder;
import com.tm.calemieconomy.util.helper.CEScreenHelper;
import com.tm.calemieconomy.util.helper.CurrencyHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenBank extends ScreenContainerBase<MenuBank> {

    private final BlockEntityBank bank;

    private ScrollableLongField transactionAmount;

    public ScreenBank(MenuBank menu, Inventory playerInv, Component useless) {
        super(menu, playerInv, menu.getBlockEntity().getDisplayName());
        textureLocation = new ResourceLocation(CEReference.MOD_ID, "textures/gui/bank.png");
        bank = (BlockEntityBank) getMenu().getBlockEntity();
        inventoryLabelY = Integer.MAX_VALUE;
        imageHeight = 177;
    }

    @Override
    protected void init() {
        super.init();

        transactionAmount = new ScrollableLongField(bank.getTransactionAmount(), 1, CEConfig.economy.bankCurrencyCapacity.get(), getScreenX() + (imageWidth / 2), getScreenY() + 39, 0x555555, () -> {
            CEPacketHandler.INSTANCE.sendToServer(new PacketBankSyncTransactionAmount(bank.getBlockPos(), transactionAmount.getValue()));
        });

        addRenderableWidget(new SmoothButton(getScreenX() + 7, getScreenY() + 74, 72, "screen.bank.btn.withdraw", (btn) -> withdraw()));
        addRenderableWidget(new SmoothButton(getScreenX() + 97, getScreenY() + 74, 72, "screen.bank.btn.deposit", (btn) -> deposit()));
    }

    /**
     * Called when the withdraw button is pressed.
     * Handles withdrawals from the Bank.
     */
    private void withdraw () {
        CEPacketHandler.INSTANCE.sendToServer(new PacketBankWithdraw(bank.getBlockPos()));
    }

    /**
     * Called when the deposit button is pressed.
     * Handles deposits from the Bank.
     */
    private void deposit () {
        CEPacketHandler.INSTANCE.sendToServer(new PacketBankDeposit(bank.getBlockPos()));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        transactionAmount.mouseScrolled(mouseX, mouseY, delta);
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);

        MutableComponent delta = new TranslatableComponent("screen.scroll.change").append(": ").append(CurrencyHelper.formatCurrency(transactionAmount.getDelta(), true).append(" ").append(new TranslatableComponent("screen.scroll.shift")));
        MutableComponent current = new TranslatableComponent("screen.scroll.current").append(": ").append(new TextComponent(CurrencyHelper.insertCommasLong(transactionAmount.getValue(), false)));
        transactionAmount.setText(CurrencyHelper.formatCurrency(transactionAmount.getValue(), false));
        transactionAmount.setTooltip(delta, current);
        transactionAmount.render(poseStack, mouseX, mouseY);

        CEScreenHelper.drawCurrencyStringCentered(poseStack, getScreenX() + imageWidth / 2, getScreenY() + 21, mouseX, mouseY, bank.getCurrency());

        if (!bank.getItem(1).isEmpty()) {

            if (bank.getItem(1).getItem() instanceof IItemCurrencyHolder currencyHolder) {

                CEScreenHelper.drawCurrencyStringCentered(poseStack, getScreenX() + imageWidth / 2, getScreenY() + 57, mouseX, mouseY, currencyHolder.getCurrency(bank.getItem(1)));
            }
        }

        if (!bank.isOnlyConnectedBank()) {
            CEScreenHelper.addIconTab(poseStack, 13, 0, getScreenX(), getScreenY() + 5, mouseX, mouseY, new TranslatableComponent("screen.bank.error.1"), new TranslatableComponent("screen.bank.error.2"));
        }
    }
}
