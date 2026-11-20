package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import net.minecraft.text.Text;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Reads arithmetic challenges from normal chat and system/action-bar messages. */
public final class PhotoMath extends Module {
    private static final Pattern EXPRESSION = Pattern.compile(
            "(?<![\\p{L}\\d.])(-?\\d+(?:[.,]\\d+)?)\\s*([+\\-*/×÷xX])\\s*(-?\\d+(?:[.,]\\d+)?)(?![\\d.])"
    );
    private long lastReplyAt;
    private String lastMessage = "";

    public PhotoMath() {
        super("PhotoMath", Category.MISC,
                "Решает арифметические примеры из чата. Поддерживает +, -, *, /, ×, ÷ и x.");
    }

    public void onIncomingMessage(Text message) {
        if (!isEnabled() || message == null || mc().player == null || mc().getNetworkHandler() == null) return;

        String raw = message.getString();
        if (raw == null || raw.isBlank()) return;
        String normalized = raw.toLowerCase(Locale.ROOT).replace('\u2212', '-');
        Matcher matcher = EXPRESSION.matcher(normalized);
        if (!matcher.find()) return;

        long now = System.currentTimeMillis();
        // CHAT and GAME callbacks can occasionally deliver the same server message.
        if (raw.equals(lastMessage) && now - lastReplyAt < 3000L) return;
        if (now - lastReplyAt < 900L) return;

        try {
            BigDecimal a = new BigDecimal(matcher.group(1).replace(',', '.'));
            BigDecimal b = new BigDecimal(matcher.group(3).replace(',', '.'));
            BigDecimal result;
            switch (matcher.group(2).toLowerCase(Locale.ROOT)) {
                case "+" -> result = a.add(b);
                case "-" -> result = a.subtract(b);
                case "*", "×", "x" -> result = a.multiply(b);
                case "/", "÷" -> {
                    if (b.compareTo(BigDecimal.ZERO) == 0) return;
                    result = a.divide(b, 8, RoundingMode.HALF_UP);
                }
                default -> { return; }
            }
            result = result.stripTrailingZeros();
            String answer = result.toPlainString();
            if (answer.length() > 32) return;

            // Send through the play network handler so it is sent as a normal chat message.
            mc().getNetworkHandler().sendChatMessage(answer);
            lastReplyAt = now;
            lastMessage = raw;
        } catch (NumberFormatException ignored) {
            // Ignore text that only resembles an arithmetic expression.
        }
    }
}
