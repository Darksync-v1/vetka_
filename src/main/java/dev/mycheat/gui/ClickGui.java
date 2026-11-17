package dev.mycheat.gui;

import dev.mycheat.MyCheat;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class ClickGui extends Screen {
    public ClickGui() { super(Text.literal("Vetka v1")); }

    private static Text label(Module m) {
        return Text.literal(m.getName() + ": " + (m.isEnabled() ? "§aON" : "§cOFF"));
    }

    private static class Slider extends SliderWidget {
        private final NumberSetting s;
        Slider(int x, int y, int w, NumberSetting s) {
            super(x, y, w, 20, Text.empty(), s.normalized());
            this.s = s;
            updateMessage();
        }
        @Override protected void updateMessage() {
            double v = s.get();
            String t = (v == Math.rint(v)) ? String.valueOf((int) v) : String.format("%.1f", v);
            setMessage(Text.literal(s.name + ": " + t));
        }
        @Override protected void applyValue() { s.set(s.min + value * (s.max - s.min)); }
    }

    @Override
    protected void init() {
        int y = 24, x = 20;
        for (Module m : MyCheat.MODULES.getAll()) {
            addDrawableChild(ButtonWidget.builder(label(m), b -> {
                m.toggle();
                b.setMessage(label(m));
            }).dimensions(x, y, 140, 20).build());
            int sx = x + 146;
            for (NumberSetting s : m.getSettings()) {
                addDrawableChild(new Slider(sx, y, 110, s));
                sx += 114;
            }
            y += 22;
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawTextWithShadow(textRenderer, "Vetka v1 (1.21.1)", 20, 8, 0xFFFFFF);
    }
}
