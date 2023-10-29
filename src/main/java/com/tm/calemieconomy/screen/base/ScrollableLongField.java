package com.tm.calemieconomy.screen.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tm.calemicore.util.helper.ScreenHelper;
import com.tm.calemicore.util.screen.ScreenRect;
import com.tm.calemieconomy.util.helper.CurrencyHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScrollableLongField {

    private long value;
    private final long minValue;
    private final long maxValue;
    private int power;

    private int x;
    private int y;
    private final int color;

    private MutableComponent text;
    private MutableComponent[] tooltip;

    private Runnable onScroll;

    public ScrollableLongField(long value, long minValue, long maxValue, int x, int y, int color, Runnable onScroll) {
        this.value = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.x = x;
        this.y = y;
        this.color = color;
        this.onScroll = onScroll;
    }

    public long getValue() {
        return value;
    }

    public int getWidth() {
        return Minecraft.getInstance().font.width(text);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public ScreenRect getRect() {
        return new ScreenRect(x - (70 / 2), y, 70, 8);
    }

    public long getDelta() {
        return (long)Math.pow(10, power);
    }

    public void setText(MutableComponent text) {
        this.text = text;
    }

    public void setTooltip(MutableComponent... tooltip) {
        this.tooltip = tooltip;
    }

    public void mouseScrolled(double mouseX, double mouseY, double delta) {

        if (getRect().contains((int)mouseX, (int)mouseY)) {

            if (Screen.hasShiftDown()) {
                power += (int)delta;
                power = Mth.clamp(power, 0, 15);
            }

            else {
                value += getDelta() * (int)delta;
                value = Mth.clamp(value, minValue, maxValue);
                onScroll.run();
            }
        }
    }

    public void render(PoseStack poseStack, int mouseX, int mouseY) {

        if (text != null) {
            poseStack.pushPose();
            poseStack.translate(0, 0, 100);
            Minecraft.getInstance().font.draw(poseStack, text, x - ((float)getWidth() / 2), y, color);
            poseStack.popPose();
        }

        //MutableComponent delta = new TranslatableComponent("screen.scroll.change").append(": ").append(CurrencyHelper.formatCurrency(getDelta()).append(" ").append(new TranslatableComponent("screen.scroll.shift")));
        //MutableComponent current = new TranslatableComponent("screen.scroll.current").append(": ").append(new TextComponent(CurrencyHelper.insertCommasLong(getValue(), false)).append(new TranslatableComponent("ce.rc")));

        if (tooltip != null) ScreenHelper.drawHoveringTextBox(poseStack, getRect(), 100, mouseX, mouseY, 0xFFFFFF, tooltip);
    }
}
