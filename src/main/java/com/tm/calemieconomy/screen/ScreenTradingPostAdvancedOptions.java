package com.tm.calemieconomy.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemicore.util.screen.ScreenBase;
import com.tm.calemicore.util.screen.widget.SmoothButton;
import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import com.tm.calemieconomy.config.CEConfig;
import com.tm.calemieconomy.main.CEReference;
import com.tm.calemieconomy.packet.*;
import com.tm.calemieconomy.screen.base.OptionLayout;
import com.tm.calemieconomy.screen.base.ScrollableLongField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenTradingPostAdvancedOptions extends ScreenBase {

    private final String key = "screen.trading_post_advanced_options";

    private final BlockEntityTradingPost post;

    private OptionLayout optionLayout;
    EditBox fileKeyBox;
    SmoothButton dynamicPriceButton;
    ScrollableLongField extremumField;
    EditBox varyRateBox;
    EditBox stableRateBox;
    SmoothButton resetCurrentPrice;

    public ScreenTradingPostAdvancedOptions(BlockEntityTradingPost post) {
        super(Minecraft.getInstance().player, InteractionHand.MAIN_HAND);
        this.post = post;
    }

    @Override
    protected void init() {
        super.init();

        long extremumMin = !post.buyMode ? post.price.getStartingPrice() : 0;
        long extremumMax = !post.buyMode ? CEConfig.economy.walletCurrencyCapacity.get() : post.price.getStartingPrice();

        LogHelper.log(CEReference.MOD_NAME, extremumMin + " " + extremumMax);

        //WIDGETS

        fileKeyBox = addRenderableWidget(new EditBox(minecraft.font, 0, 0, 250, 12, new TextComponent("")));

        dynamicPriceButton = addRenderableWidget(new SmoothButton( 0, 0, 70, "screen." + (post.price.isDynamic ? "true" : "false"), (btn) -> {
            boolean value = !post.price.isDynamic;
            post.price.isDynamic = value;
            CEPacketHandler.INSTANCE.sendToServer(new PacketTradingPostSyncDynamicPriceMode(post.getBlockPos(), value));
        }));

        extremumField = new ScrollableLongField(post.price.getExtremum(), extremumMin, extremumMax, 0, 0, 0xFFFFFF, () -> {
            CEPacketHandler.INSTANCE.sendToServer(new PacketTradingPostSyncExtremum(post.getBlockPos(), (int)extremumField.getValue()));
        });

        varyRateBox = addRenderableWidget(new EditBox(minecraft.font, 0, 0, 70, 12, new TextComponent("")));
        stableRateBox = addRenderableWidget(new EditBox(minecraft.font, 0, 0, 70, 12, new TextComponent("")));

        resetCurrentPrice = addRenderableWidget(new SmoothButton( 0, 0, 70, key + ".btn.reset", (btn) -> {
            CEPacketHandler.INSTANCE.sendToServer(new PacketTradingPostResetCurrentPrice(post.getBlockPos()));
        }));

        //LAYOUT

        optionLayout = new OptionLayout(this, width / 2, height / 2);
        optionLayout.addStringEditBox(new TranslatableComponent(key + ".label.file_key"), post.fileKey, fileKeyBox);
        optionLayout.addButton(new TranslatableComponent(key + ".label.dynamic_price"), dynamicPriceButton);
        optionLayout.addScrollableLongField(new TranslatableComponent(key + ".label.extremum"), extremumField);
        optionLayout.addFloatEditBox(new TranslatableComponent(key + ".label.vary_rate"), post.price.varyRate, varyRateBox);
        optionLayout.addFloatEditBox(new TranslatableComponent(key + ".label.stable_rate"), post.price.stableRate, stableRateBox);
        optionLayout.addButton(new TranslatableComponent(key + ".label.reset_current_price"), resetCurrentPrice);
        optionLayout.init();
    }

    @Override
    public void tick() {
        super.tick();
        optionLayout.tick();

        dynamicPriceButton.setMessage(new TranslatableComponent("screen." + (post.price.isDynamic ? "true" : "false")));
    }

    @Override
    protected void drawGuiBackground(PoseStack poseStack, int mouseX, int mouseY) {}

    @Override
    protected void drawGuiForeground(PoseStack poseStack, int mouseX, int mouseY) {

        boolean isAdmin = post.adminMode;
        boolean isDynamic = post.price.isDynamic;

        optionLayout.setRender(0, isAdmin);
        optionLayout.setRender(2, isDynamic);
        optionLayout.setRender(3, isDynamic);
        optionLayout.setRender(4, isDynamic);
        optionLayout.setRender(5, isDynamic);

        optionLayout.render(poseStack, mouseX, mouseY);
    }

    private void confirmEditBoxes() {

        String fileKey = fileKeyBox.getValue();
        float varyRate = parseFloat(varyRateBox.getValue());
        float stableRate = parseFloat(stableRateBox.getValue());

        varyRate = Mth.abs(varyRate);
        stableRate = Mth.abs(stableRate);

        varyRateBox.setValue("" + varyRate);
        stableRateBox.setValue("" + stableRate);

        CEPacketHandler.INSTANCE.sendToServer(new PacketTradingPostSyncFileKey(post.getBlockPos(), fileKey));
        CEPacketHandler.INSTANCE.sendToServer(new PacketTradingPostSyncVaryRate(post.getBlockPos(), varyRate));
        CEPacketHandler.INSTANCE.sendToServer(new PacketTradingPostSyncStableRate(post.getBlockPos(), stableRate));
    }

    private float parseFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException numberformatexception) {
            return 0;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        optionLayout.mouseScrolled(mouseX, mouseY, delta);
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

        //Escape Key
        if (keyCode == 256) {
            minecraft.player.closeContainer();
        }

        //Enter Key
        else if (keyCode == 257) {
            confirmEditBoxes();
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected int getGuiSizeX() {
        return 0;
    }

    @Override
    protected int getGuiSizeY() {
        return 0;
    }

    @Override
    protected boolean canCloseWithInvKey() {
        return !fileKeyBox.isFocused();
    }
}
