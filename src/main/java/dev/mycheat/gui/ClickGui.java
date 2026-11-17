package dev.mycheat.gui;

import dev.mycheat.MyCheat;
import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import dev.mycheat.util.Config;
import dev.mycheat.util.Friends;
import dev.mycheat.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;
import java.util.List;

/**
 * Меню по колонкам-категориям.
 * ЛКМ по модулю: вкл/выкл. ПКМ: настройки. СКМ: назначить клавишу (Delete — снять, Esc — отмена).
 * Наведи мышь на модуль, чтобы увидеть описание. Список друзей справа, клик по нику убирает друга.
 */
public class ClickGui extends Screen {
    private static final int W = 124, HEAD = 18, ROW = 16, SROW = 14, GAP = 8;
    // ACCENT теперь считается каждый кадр как переливающийся цвет

    private record Region(int x1, int y1, int x2, int y2, Module module, NumberSetting setting, String friend) {
        boolean has(double mx, double my) { return mx >= x1 && mx < x2 && my >= y1 && my < y2; }
    }

    private final List<Region> regions = new ArrayList<>();
    private Module expanded, binding;
    private Region dragging;

    public ClickGui() { super(Text.literal("Vetka v1")); }

    @Override public boolean shouldPause() { return false; }
    @Override public void removed() { Config.save(); }

    private static String keyName(int key) {
        String s = InputUtil.Type.KEYSYM.createFromCode(key).getLocalizedText().getString();
        return s.length() > 8 ? s.substring(0, 8) : s;
    }

    private static String fmt(NumberSetting s) {
        double v = s.get();
        return v == Math.rint(v) ? String.valueOf((long) v) : String.format("%.2f", v);
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        regions.clear();
        int ACCENT = RenderUtil.rainbow(System.currentTimeMillis() / 1000.0, 0);
        ctx.fill(0, 0, width, height, 0x90000000);
        ctx.drawCenteredTextWithShadow(textRenderer, "Vetka v1", width / 2, 6, ACCENT);

        Category[] cats = Category.values();
        int panels = cats.length + 1;
        int startX = (width - (panels * W + (panels - 1) * GAP)) / 2;
        int top = 22;
        Module hovered = null;

        for (int i = 0; i < cats.length; i++) {
            int x = startX + i * (W + GAP), y = top;
            ctx.fill(x, y, x + W, y + HEAD, 0xF0221833);
            ctx.drawCenteredTextWithShadow(textRenderer, cats[i].title, x + W / 2, y + 5, ACCENT);
            y += HEAD;
            for (Module m : MyCheat.MODULES.getAll()) {
                if (m.getCategory() != cats[i]) continue;
                boolean hov = mx >= x && mx < x + W && my >= y && my < y + ROW;
                int bg = m.isEnabled() ? (hov ? 0xD07A4BC0 : 0xC0603A99) : (hov ? 0xC02A2A3C : 0xC0141420);
                ctx.fill(x, y, x + W, y + ROW, bg);
                ctx.drawTextWithShadow(textRenderer, m.getName(), x + 6, y + 4, m.isEnabled() ? 0xFFFFFF : 0xAAAAAA);

                String right = binding == m ? "[...]" : (m.getKey() >= 0 ? "[" + keyName(m.getKey()) + "]" : "");
                if (!m.getSettings().isEmpty()) right += expanded == m ? " -" : " +";
                ctx.drawTextWithShadow(textRenderer, right, x + W - 5 - textRenderer.getWidth(right), y + 4, 0x9A9AB0);
                regions.add(new Region(x, y, x + W, y + ROW, m, null, null));
                if (hov) hovered = m;
                y += ROW;

                if (expanded == m) {
                    for (NumberSetting s : m.getSettings()) {
                        ctx.fill(x, y, x + W, y + SROW, 0xB00C0C14);
                        ctx.fill(x, y, x + (int) (W * s.normalized()), y + SROW, 0x908A52D6);
                        ctx.drawTextWithShadow(textRenderer, s.name + ": " + fmt(s), x + 6, y + 3, 0xFFFFFF);
                        regions.add(new Region(x, y, x + W, y + SROW, null, s, null));
                        y += SROW;
                    }
                }
            }
        }

        // друзья
        int fx = startX + cats.length * (W + GAP), fy = top;
        ctx.fill(fx, fy, fx + W, fy + HEAD, 0xF0221833);
        ctx.drawCenteredTextWithShadow(textRenderer, "Друзья", fx + W / 2, fy + 5, ACCENT);
        fy += HEAD;
        List<String> friends = Friends.all();
        if (friends.isEmpty()) {
            ctx.fill(fx, fy, fx + W, fy + 28, 0xC0141420);
            ctx.drawTextWithShadow(textRenderer, "СКМ по игроку", fx + 6, fy + 4, 0x9A9AB0);
            ctx.drawTextWithShadow(textRenderer, "в мире — добавить", fx + 6, fy + 15, 0x9A9AB0);
        }
        for (String n : friends) {
            boolean hov = mx >= fx && mx < fx + W && my >= fy && my < fy + ROW;
            ctx.fill(fx, fy, fx + W, fy + ROW, hov ? 0xC03A1F2A : 0xC0141420);
            ctx.drawTextWithShadow(textRenderer, "§a" + n, fx + 6, fy + 4, 0xFFFFFF);
            ctx.drawTextWithShadow(textRenderer, "✕", fx + W - 14, fy + 4, 0xFF6666);
            regions.add(new Region(fx, fy, fx + W, fy + ROW, null, null, n));
            fy += ROW;
        }

        // подсказка внизу
        ctx.drawCenteredTextWithShadow(textRenderer,
                "ЛКМ — вкл/выкл   ПКМ — настройки   СКМ — бинд   Right Shift — закрыть", width / 2, height - 12, 0x9A9AB0);

        // всплывающее описание на русском
        if (hovered != null && binding == null && dragging == null) {
            List<OrderedText> lines = textRenderer.wrapLines(Text.literal(hovered.getDescription()), 170);
            int bw = 178, bh = lines.size() * 10 + 8;
            int bx = Math.min(mx + 10, width - bw - 4), by = Math.min(my + 10, height - bh - 4);
            ctx.fill(bx, by, bx + bw, by + bh, 0xF0100014);
            ctx.fill(bx, by, bx + 2, by + bh, ACCENT);
            int ly = by + 4;
            for (OrderedText l : lines) { ctx.drawTextWithShadow(textRenderer, l, bx + 6, ly, 0xFFFFFF); ly += 10; }
        }
    }

    private void slide(Region r, double mx) {
        NumberSetting s = r.setting();
        double t = Math.max(0, Math.min(1, (mx - r.x1()) / (r.x2() - r.x1())));
        s.set(s.min + t * (s.max - s.min));
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        binding = null;
        for (Region r : regions) {
            if (!r.has(mx, my)) continue;
            if (r.setting() != null) {
                if (button == 0) { dragging = r; slide(r, mx); }
            } else if (r.friend() != null) {
                Friends.remove(r.friend());
                Config.save();
            } else if (r.module() != null) {
                Module m = r.module();
                if (button == 0) m.toggle();
                else if (button == 1) expanded = expanded == m ? null : m;
                else if (button == 2) binding = m;
            }
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (dragging != null) { slide(dragging, mx); return true; }
        return super.mouseDragged(mx, my, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        dragging = null;
        return super.mouseReleased(mx, my, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (binding != null) {
            if (keyCode == GLFW.GLFW_KEY_DELETE || keyCode == GLFW.GLFW_KEY_BACKSPACE) binding.setKey(-1);
            else if (keyCode != GLFW.GLFW_KEY_ESCAPE) binding.setKey(keyCode);
            binding = null;
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) { close(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
