package com.tm.calemieconomy.screen.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tm.calemicore.util.screen.widget.SmoothButton;
import com.tm.calemieconomy.util.helper.CurrencyHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class OptionLayout {

    private final Minecraft mc = Minecraft.getInstance();

    private final Screen screen;
    private int x, y;
    private final List<Option> options = new ArrayList<>();

    public OptionLayout(Screen screen, int x, int y) {
        this.x = x;
        this.y = y;
        this.screen = screen;
    }

    public void addButton(Component label, SmoothButton button) {
        options.add(new Option(label, button));
    }

    public void addScrollableLongField(Component label, ScrollableLongField field) {
        options.add(new Option(label, field));
    }

    public void addFloatEditBox(Component label, float value, EditBox editBox) {
        editBox.setMaxLength(20);
        editBox.setValue("" + value);
        options.add(new Option(label, editBox));
    }

    public void addStringEditBox(Component label, String value, EditBox editBox) {
        editBox.setMaxLength(100);
        editBox.setValue(value);
        options.add(new Option(label, editBox));
    }

    public void setRender(int optionIndex, boolean value) {

        if (options.size() >= optionIndex + 1) {
            options.get(optionIndex).setRender(value);
        }
    }

    public void mouseScrolled(double mouseX, double mouseY, double delta) {

        for (Option option : options) {

            if (option.getWidget() instanceof ScrollableLongField scrollableLongField) {
                scrollableLongField.mouseScrolled(mouseX, mouseY, delta);
            }
        }
    }

    public void init() {

        for (Option option : options) {

            if (option.getWidget() instanceof SmoothButton button) {
                screen.renderables.add(button);
            }
        }
    }

    public void tick() {

        for (Option option : options) {

            if (option.getWidget() instanceof EditBox editBox) {
                editBox.tick();
            }
        }
    }

    public void render(PoseStack poseStack, int mouseX, int mouseY) {

        float height = 0;

        for (Option option : options) {

            if (!option.shouldRender()) {
                continue;
            }

            height += 20;
        }

        for (int i = 0; i < options.size(); i++) {

            Option option = options.get(i);
            Object obj = option.getWidget();

            if (!option.shouldRender()) {

                if (obj instanceof EditBox editBox) {
                    editBox.y = 10000;
                }

                continue;
            }

            int y = (this.y - (int)(height / 2) + i * 20) + 3;

            mc.font.draw(poseStack, option.getLabel(), x - (mc.font.width(option.getLabel()) + 5), y + 3, 0xFFFFFF);

            if (obj instanceof SmoothButton button) {
                button.setPosition(x + 4, y - 1);
            }

            else if (obj instanceof ScrollableLongField scrollableLongField) {

                MutableComponent amountDelta = new TranslatableComponent("screen.scroll.change").append(": ").append(CurrencyHelper.insertCommasLong(scrollableLongField.getDelta(), false)).append(" ").append(new TranslatableComponent("screen.scroll.shift"));

                scrollableLongField.setPosition(x + 5 + (scrollableLongField.getRect().width / 2), y + 2);
                scrollableLongField.setText(CurrencyHelper.formatCurrency(scrollableLongField.getValue(), false));
                scrollableLongField.setTooltip(amountDelta);
                scrollableLongField.render(poseStack, mouseX, mouseY);
            }

            else if (obj instanceof EditBox editBox) {
                editBox.x = x + 5;
                editBox.y = y + 1;
            }
        }
    }

    public static class Option {

        private final Component label;
        private final Object widget;
        private boolean render = true;

        public Option(Component label, Object widget) {
            this.label = label;
            this.widget = widget;
        }

        public Component getLabel() {
            return label;
        }

        public Object getWidget() {
            return widget;
        }

        public boolean shouldRender() {
            return render;
        }

        public void setRender(boolean value) {
            render = value;
        }
    }
}
