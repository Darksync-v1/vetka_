package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import net.minecraft.text.Text;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Detects simple arithmetic challenges in incoming chat and replies with the result. */
public final class PhotoMath extends Module {
    private static final Pattern EXPRESSION = Pattern.compile(
            "(?<![\\d.])(-?\\d+(?:[.,]\\d+)?)\\s*([+\\-*/×÷])\\s*(-?\\d+(?:[.,]\\d+)?)(?![\\d.])"
    );
    private long lastReplyAt;

    public PhotoMath() {
        super("PhotoMath", Category.MISC, "Автоматически решает простые математические примеры из чата.");
    }

    public void onIncomingMessage(Text message) {
        if (!isEnabled() || mc().player == null || mc().getNetworkHandler() == null) return;
        String raw = message.getString();
        String lower = raw.toLowerCase(java.util.Locale.ROOT);

        // Only respond to messages that look like a challenge, not ordinary conversation.
        if (!(lower.contains("?") || lower.contains("сколько") || lower.contains("solve")
                || lower.contains("ответ") || lower.contains("вычисл") || lower.contains("="))) return;

        Matcher matcher = EXPRESSION.matcher(raw);
        if (!matcher.find()) return;
        if (System.currentTimeMillis() - lastReplyAt < 1500L) return;

        try {
            BigDecimal a = new BigDecimal(matcher.group(1).replace(',', '.'));
            BigDecimal b = new BigDecimal(matcher.group(3).replace(',', '.'));
            BigDecimal result;
            switch (matcher.group(2)) {
                case "+" -> result = a.add(b);
                case "-" -> result = a.subtract(b);
                case "*", "×" -> result = a.multiply(b);
                case "/", "÷" -> {
                    if (b.compareTo(BigDecimal.ZERO) == 0) return;
                    result = a.divide(b, 8, RoundingMode.HALF_UP).stripTrailingZeros();
                }
                default -> { return; }
            }
            String answer = result.stripTrailingZeros().toPlainString();
            if (answer.length() > 32) return;
            mc().getNetworkHandler().sendChatMessage(answer);
            lastReplyAt = System.currentTimeMillis();
        } catch (NumberFormatException ignored) {
            // Ignore malformed challenge text.
        }
    }
}
