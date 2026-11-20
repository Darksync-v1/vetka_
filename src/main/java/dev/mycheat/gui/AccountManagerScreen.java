package dev.mycheat.gui;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.session.Session;
import dev.mycheat.mixin.MinecraftClientAccessor;
import dev.mycheat.util.VetkaPaths;
import net.minecraft.text.Text;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Offline-name manager. Microsoft accounts use the official Microsoft launcher sign-in. */
public final class AccountManagerScreen extends Screen {
    private final Screen parent;
    private final List<String> accounts = new ArrayList<>();
    private TextFieldWidget nameField;
    private String status = "Offline names only. Microsoft sign-in is handled by the official launcher.";
    private static final int PANEL_W = 310;

    public AccountManagerScreen(Screen parent) {
        super(Text.literal("Vetka Account Manager"));
        this.parent = parent;
    }

    private Path accountFile() {
        return VetkaPaths.config().resolve("vetka_accounts.txt");
    }

    @Override
    protected void init() {
        accounts.clear();
        try {
            Path file = accountFile();
            if (Files.exists(file)) {
                for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                    String value = line.trim();
                    if (!value.isEmpty() && !accounts.contains(value)) accounts.add(value);
                }
            }
        } catch (IOException ignored) {
            status = "Could not read saved account names.";
        }

        int left = (width - PANEL_W) / 2;
        nameField = new TextFieldWidget(textRenderer, left + 10, 62, PANEL_W - 20, 20, Text.literal("Player name"));
        nameField.setMaxLength(16);
        nameField.setPlaceholder(Text.literal("Enter username (3-16 chars)"));
        addDrawableChild(nameField);
        addDrawableChild(ButtonWidget.builder(Text.literal("Add & switch"), b -> addAndSwitch())
                .dimensions(left + 10, 88, 140, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Back"), b -> client.setScreen(parent))
                .dimensions(left + 160, 88, 140, 20).build());

        int y = 122;
        for (String account : accounts) {
            if (y > height - 34) break;
            final String selected = account;
            addDrawableChild(ButtonWidget.builder(Text.literal("Use: " + selected),
                    b -> switchTo(selected)).dimensions(left + 10, y, PANEL_W - 20, 20).build());
            y += 24;
        }
    }

    private void addAndSwitch() {
        String username = nameField.getText().trim();
        if (!username.matches("[A-Za-z0-9_]{3,16}")) {
            status = "Use 3-16 letters, numbers or underscores.";
            return;
        }
        if (!accounts.contains(username)) accounts.add(0, username);
        saveAccounts();
        switchTo(username);
    }

    private void saveAccounts() {
        try {
            Files.createDirectories(accountFile().getParent());
            Files.write(accountFile(), accounts, StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            status = "Could not save account names.";
        }
    }

    private void switchTo(String username) {
        UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
        Session session = new Session(username, uuid, "", Optional.empty(), Optional.empty(), Session.AccountType.LEGACY);
        ((MinecraftClientAccessor) (Object) MinecraftClient.getInstance()).vetka$setSession(session);
        status = "Switched to " + username + " (offline name).";
        client.setScreen(parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(net.minecraft.util.Identifier.of("mycheat", "textures/gui/vetka_bg.png"),
                0, 0, 0, 0, width, height, 1920, 1080);
        context.fill(0, 0, width, height, 0x99070A12);
        int left = (width - PANEL_W) / 2;
        context.fill(left, 35, left + PANEL_W, height - 24, 0xE0141722);
        context.fill(left, 35, left + PANEL_W, 37, 0xFF9B68E8);
        context.drawCenteredTextWithShadow(textRenderer, "VETKA ACCOUNT MANAGER", width / 2, 43, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer, "Saved offline names", width / 2, 112, 0xFFBDB7CC);
        context.drawCenteredTextWithShadow(textRenderer, status, width / 2, height - 16, 0xFFE0D7F0);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override public boolean shouldPause() { return false; }
}
