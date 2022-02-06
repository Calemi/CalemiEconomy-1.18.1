package com.tm.calemieconomy.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tm.calemicore.util.helper.MathHelper;
import com.tm.calemicore.util.screen.ScreenContainerBase;
import com.tm.calemicore.util.screen.widget.ButtonRect;
import com.tm.calemieconomy.blockentity.BlockEntityBank;
import com.tm.calemieconomy.item.ItemWallet;
import com.tm.calemieconomy.main.CEReference;
import com.tm.calemieconomy.menu.MenuBank;
import com.tm.calemieconomy.packet.CEPacketHandler;
import com.tm.calemieconomy.packet.PacketBank;
import com.tm.calemieconomy.util.helper.ScreenTabs;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenBank extends ScreenContainerBase<MenuBank> {

    private final BlockEntityBank bank;

    public ScreenBank(MenuBank menu, Inventory playerInv, Component useless) {
        super(menu, playerInv, menu.getBlockEntity().getDisplayName());
        textureLocation = new ResourceLocation(CEReference.MOD_ID, "textures/gui/bank.png");
        bank = (BlockEntityBank) getMenu().getBlockEntity();
        inventoryLabelY = Integer.MAX_VALUE;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(new ButtonRect(getScreenX() + (imageWidth / 2) + 30 - 25, getScreenY() + 40, 50, "screen.bank.btn.withdraw", (btn) -> withdraw()));
        addRenderableWidget(new ButtonRect(getScreenX() + (imageWidth / 2) - 30 - 25, getScreenY() + 40, 50, "screen.bank.btn.deposit", (btn) -> deposit()));
    }

    /**
     * Called when the withdraw button is pressed.
     * Handles withdrawals from the Bank.
     */
    private void withdraw () {

        //Checks if there is a Wallet in the Wallet slot.
        if (bank.getItem(1).getItem() instanceof ItemWallet wallet) {

            ItemStack walletStack = bank.getItem(1);

            int walletCurrency = wallet.getCurrency(walletStack);
            int amountToAdd = MathHelper.getAmountToAdd(walletCurrency, bank.getCurrency(), wallet.getCurrencyCapacity());

            //If the Wallet can fit the currency, add it and subtract it from the Bank.
            if (amountToAdd > 0) {
                bank.withdrawCurrency(amountToAdd);
                wallet.depositCurrency(walletStack, amountToAdd);
            }

            //If the Wallet can't fit all the money, get how much is needed to fill it, then only used that much.
            else {

                int remainder = MathHelper.getAmountToFill(walletCurrency, bank.getCurrency(), wallet.getCurrencyCapacity());

                if (remainder > 0) {
                    bank.withdrawCurrency(amountToAdd);
                    wallet.depositCurrency(walletStack, amountToAdd);
                }
            }

            //Syncs the Bank's currency to the server.
            CEPacketHandler.INSTANCE.sendToServer(new PacketBank(bank.getCurrency(), wallet.getCurrency(walletStack), bank.getBlockPos()));
        }
    }

    /**
     * Called when the deposit button is pressed.
     * Handles deposits from the Bank.
     */
    private void deposit () {

        //Checks if there is a Wallet in the Wallet slot.
        if (bank.getItem(1).getItem() instanceof ItemWallet wallet) {

            ItemStack walletStack =bank.getItem(1);

            int walletCurrency = wallet.getCurrency(walletStack);
            int amountToAdd = MathHelper.getAmountToAdd(bank.getCurrency(), walletCurrency, bank.getCurrencyCapacity());

            //If the Bank can fit the currency, add it and subtract it from the Wallet.
            if (amountToAdd > 0) {
                bank.depositCurrency(amountToAdd);
                wallet.withdrawCurrency(walletStack, amountToAdd);
            }

            //If the Bank can't fit all the money, get how much is needed to fill it, then only used that much.
            else {

                int remainder = MathHelper.getAmountToFill(bank.getCurrency(), walletCurrency, bank.getCurrencyCapacity());

                if (remainder > 0) {

                    bank.depositCurrency(remainder);
                    wallet.withdrawCurrency(walletStack, remainder);
                }
            }

            CEPacketHandler.INSTANCE.sendToServer(new PacketBank(bank.getCurrency(), wallet.getCurrency(walletStack), bank.getBlockPos()));
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);

        ScreenTabs.addCurrencyTab(poseStack, getScreenX(), getScreenY() + 5, mouseX, mouseY, bank);

        if (!bank.isOnlyConnectedBank()) {
            ScreenTabs.addIconTab(poseStack, 13, 0, getScreenX(), getScreenY() + 21, mouseX, mouseY, new TranslatableComponent("screen.bank.error.1"), new TranslatableComponent("screen.bank.error.2"));
        }
    }
}
