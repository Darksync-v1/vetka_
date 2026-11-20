package dev.mycheat.gui;

import dev.mycheat.MyCheat;
import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import dev.mycheat.setting.TextSetting;
import dev.mycheat.util.Config;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;
import java.util.List;
import java.util.EnumMap;
import java.util.Map;

/** ThunderHack-inspired compact ClickGUI for Vetka v1. */
public class ClickGui extends Screen {
    private static final int PANEL_W = 178, PANEL_H = 196, GAP = 9;
    private static final int HEADER_H = 25, ROW_H = 19, SETTING_H = 17;
    private static final int ACCENT = 0xFF9B68E8;
    private record Region(int x1, int y1, int x2, int y2, Module module, NumberSetting setting, TextSetting textSetting) {
        boolean has(double mx, double my) { return mx >= x1 && mx < x2 && my >= y1 && my < y2; }
    }

    private final List<Region> regions = new ArrayList<>();
    private final Map<Category, Integer> scrollOffsets = new EnumMap<>(Category.class);
    private Module expanded, binding;
    private Region dragging;
    private TextSetting editingText;
    private TextFieldWidget textField;

    public ClickGui() { super(Text.literal("Vetka | ClickGUI")); }
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
    private void panel(DrawContext ctx, int x, int y, Category category, int mx, int my, float delta) {
        int bottom = y + PANEL_H;
        int contentTop = y + HEADER_H + 5;
        int contentBottom = bottom - 5;
        int viewHeight = contentBottom - contentTop;
        ctx.fill(x, y, x + PANEL_W, bottom, 0xD0101119);
        ctx.fill(x, y, x + PANEL_W, y + HEADER_H, 0xFF191B27);
        ctx.fill(x, y, x + PANEL_W, y + 2, ACCENT);
        ctx.drawTextWithShadow(textRenderer, category.title.toUpperCase(), x + 10, y + 9, 0xFFF0ECF8);
        int count = 0;
        for (Module m : MyCheat.MODULES.getAll()) if (m.getCategory() == category) count++;
        String total = String.valueOf(count);
        ctx.drawTextWithShadow(textRenderer, total, x + PANEL_W - 10 - textRenderer.getWidth(total), y + 9, 0xFF77798C);

        // Calculate the full content height so the wheel can reach every module and setting.
        int contentHeight = 0;
        for (Module m : MyCheat.MODULES.getAll()) {
            if (m.getCategory() != category) continue;
            contentHeight += ROW_H + 2;
            if (expanded == m) contentHeight += m.getSettings().size() * (SETTING_H + 1)
                    + m.getTextSettings().size() * (SETTING_H + 1);
        }
        int maxScroll = Math.max(0, contentHeight - viewHeight);
        int scroll = Math.max(0, Math.min(maxScroll, scrollOffsets.getOrDefault(category, 0)));
        scrollOffsets.put(category, scroll);

        ctx.enableScissor(x + 2, contentTop, x + PANEL_W - 2, contentBottom);
        int cy = contentTop - scroll;
        for (Module m : MyCheat.MODULES.getAll()) {
            if (m.getCategory() != category) continue;
            int rowTop = cy;
            int rowBottom = cy + ROW_H;
            if (rowBottom > contentTop && rowTop < contentBottom) {
                boolean hov = mx >= x + 5 && mx < x + PANEL_W - 5 && my >= Math.max(rowTop, contentTop) && my < Math.min(rowBottom, contentBottom);
                ctx.fill(x + 5, rowTop, x + PANEL_W - 5, rowBottom, hov ? 0xFF292B3A : 0xFF1D1F2A);
                ctx.fill(x + 5, rowTop + 3, x + 7, rowBottom - 3, m.isEnabled() ? ACCENT : 0xFF444653);
                ctx.drawTextWithShadow(textRenderer, m.getName(), x + 13, rowTop + 6, m.isEnabled() ? 0xFFF4F0FF : 0xFFB1B2BE);
                String right = binding == m ? "[...]" : (m.getKey() >= 0 ? "[" + keyName(m.getKey()) + "]" : "");
                if (!m.getSettings().isEmpty() || !m.getTextSettings().isEmpty()) right += expanded == m ? "  v" : "  >";
                ctx.drawTextWithShadow(textRenderer, right, x + PANEL_W - 10 - textRenderer.getWidth(right), rowTop + 6, 0xFF85879A);
                regions.add(new Region(x + 5, Math.max(rowTop, contentTop), x + PANEL_W - 5, Math.min(rowBottom, contentBottom), m, null, null));
            }
            cy += ROW_H + 2;

            if (expanded == m) {
                for (NumberSetting setting : m.getSettings()) {
                    int st = cy, sb = cy + SETTING_H;
                    if (sb > contentTop && st < contentBottom) {
                        ctx.fill(x + 9, st, x + PANEL_W - 9, sb, 0xD014151D);
                        int barW = (int)((PANEL_W - 18) * setting.normalized());
                        ctx.fill(x + 9, sb - 2, x + 9 + barW, sb, ACCENT);
                        String label = setting.name + "  " + fmt(setting);
                        ctx.drawTextWithShadow(textRenderer, label, x + 13, st + 4, 0xFFD0CDDA);
                        regions.add(new Region(x + 9, Math.max(st, contentTop), x + PANEL_W - 9, Math.min(sb, contentBottom), null, setting, null));
                    }
                    cy += SETTING_H + 1;
                }
                for (TextSetting setting : m.getTextSettings()) {
                    int st = cy, sb = cy + SETTING_H;
                    if (sb > contentTop && st < contentBottom) {
                        ctx.fill(x + 9, st, x + PANEL_W - 9, sb, 0xD014151D);
                        String shown = setting.name + ": " + setting.get();
                        if (shown.length() > 24) shown = shown.substring(0, 21) + "...";
                        if (editingText == setting && textField != null) textField.render(ctx, mx, my, delta);
                        else ctx.drawTextWithShadow(textRenderer, shown, x + 13, st + 4, 0xFFD0CDDA);
                        regions.add(new Region(x + 9, Math.max(st, contentTop), x + PANEL_W - 9, Math.min(sb, contentBottom), null, null, setting));
                    }
                    cy += SETTING_H + 1;
                }
            }
        }
        ctx.disableScissor();
        if (maxScroll > 0) {
            int trackX = x + PANEL_W - 3;
            int trackTop = contentTop + 1;
            int trackBottom = contentBottom - 1;
            int thumbH = Math.max(14, viewHeight * viewHeight / contentHeight);
            int thumbY = trackTop + (trackBottom - trackTop - thumbH) * scroll / maxScroll;
            ctx.fill(trackX, trackTop, trackX + 2, trackBottom, 0xFF343644);
            ctx.fill(trackX, thumbY, trackX + 2, thumbY + thumbH, ACCENT);
        }
    }

    @Override public void render(DrawContext ctx, int mx, int my, float delta) {
        regions.clear();
        ctx.drawTexture(Identifier.of("mycheat", "textures/gui/vetka_bg.png"),
                0, 0, 0, 0, width, height, 220, 391);
        ctx.fill(0, 0, width, height, 0x88080910);
        int gridW = PANEL_W * 2 + GAP;
        int gridH = PANEL_H * 2 + GAP;
        int left = (width - gridW) / 2;
        int top = Math.max(30, (height - gridH) / 2 + 8);
        ctx.fill(left - 8, top - 30, left + gridW + 8, top + gridH + 8, 0xD90D0E15);
        ctx.fill(left - 8, top - 30, left + gridW + 8, top - 28, ACCENT);
        ctx.drawTextWithShadow(textRenderer, "VETKA", left, top - 20, 0xFFF4F0FF);
        ctx.drawTextWithShadow(textRenderer, "1.21.1  •  RIGHT SHIFT", left + gridW - textRenderer.getWidth("1.21.1  •  RIGHT SHIFT"), top - 20, 0xFF85879A);
        Category[] c = Category.values();
        for (int i = 0; i < c.length; i++) {
            int x = left + (i % 2) * (PANEL_W + GAP);
            int y = top + (i / 2) * (PANEL_H + GAP);
            panel(ctx, x, y, c[i], mx, my, delta);
        }
        ctx.drawCenteredTextWithShadow(textRenderer, "ЛКМ — toggle     ПКМ — settings     СКМ — bind", width / 2, height - 12, 0xFF898B9B);
        super.render(ctx, mx, my, delta);
    }

    @Override public boolean mouseScrolled(double mx, double my, double horizontalAmount, double verticalAmount) {
        int gridW = PANEL_W * 2 + GAP;
        int gridH = PANEL_H * 2 + GAP;
        int left = (width - gridW) / 2;
        int top = Math.max(30, (height - gridH) / 2 + 8);
        Category[] categories = Category.values();
        for (int i = 0; i < categories.length; i++) {
            int x = left + (i % 2) * (PANEL_W + GAP);
            int y = top + (i / 2) * (PANEL_H + GAP);
            if (mx >= x && mx < x + PANEL_W && my >= y + HEADER_H && my < y + PANEL_H) {
                Category category = categories[i];
                int current = scrollOffsets.getOrDefault(category, 0);
                scrollOffsets.put(category, Math.max(0, current - (int)Math.round(verticalAmount * 24)));
                return true;
            }
        }
        return super.mouseScrolled(mx, my, horizontalAmount, verticalAmount);
    }

    private void slide(Region r, double mx) {
        NumberSetting s = r.setting();
        double t = Math.max(0, Math.min(1, (mx - r.x1()) / (r.x2() - r.x1())));
        s.set(s.min + t * (s.max - s.min));
    }
    @Override public boolean charTyped(char chr, int modifiers) {
        if (textField != null && textField.charTyped(chr, modifiers)) return true;
        return super.charTyped(chr, modifiers);
    }
    @Override public boolean mouseClicked(double mx, double my, int button) {
        if (textField != null && textField.mouseClicked(mx, my, button)) return true;
        if (editingText != null) { editingText = null; textField = null; Config.save(); }
        binding = null;
        for (Region r : regions) {
            if (!r.has(mx, my)) continue;
            if (r.textSetting() != null) {
                editingText = r.textSetting();
                textField = new TextFieldWidget(textRenderer, r.x1() + 3, r.y1() + 1, r.x2() - r.x1() - 6, SETTING_H - 2, Text.literal(editingText.name));
                textField.setMaxLength(128); textField.setText(editingText.get());
                textField.setChangedListener(editingText::set); textField.setFocused(true); return true;
            } else if (r.setting() != null) {
                if (button == 0) { dragging = r; slide(r, mx); }
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
    @Override public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (dragging != null) { slide(dragging, mx); return true; }
        return super.mouseDragged(mx, my, button, dx, dy);
    }
    @Override public boolean mouseReleased(double mx, double my, int button) {
        dragging = null; return super.mouseReleased(mx, my, button);
    }
    @Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (editingText != null && textField != null) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER || keyCode == GLFW.GLFW_KEY_ESCAPE) {
                editingText = null; textField = null; Config.save(); return true;
            }
            if (textField.keyPressed(keyCode, scanCode, modifiers)) return true;
        }
        if (binding != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) binding.setKey(-1);
            else binding.setKey(keyCode);
            binding = null; return true;
        }
        if (keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) { close(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
