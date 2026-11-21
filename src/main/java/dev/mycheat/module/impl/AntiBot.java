package dev.mycheat.module.impl;

import dev.mycheat.MyCheat;
import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Отсеивает NPC и ботов-приманок, чтобы ауры их не били.
 * По умолчанию выключено (0), кроме проверки ника: на пиратских/анархи-серверах (TLauncher, ely.by
 * и т.п.) у игроков оффлайн-UUID версии 3, а не 4, и они не всегда есть в tab-листе — раньше это
 * ошибочно помечало ВСЕХ игроков как ботов и аура переставала бить кого-либо вообще.
 */
public class AntiBot extends Module {
    private final NumberSetting tab = add(new NumberSetting("TabList", 0, 0, 1, 1));
    private final NumberSetting uuid = add(new NumberSetting("UUID", 0, 0, 1, 1));
    private final NumberSetting name = add(new NumberSetting("Name", 1, 0, 1, 1));

    public AntiBot() { super("AntiBot", Category.COMBAT, "Исключает ботов и NPC из целей ауры. TabList и UUID по умолчанию выключены — на пиратских/анархи-серверах они ошибочно помечают ботами реальных игроков."); }

    /**
     * Conservative heuristic used by AimBot and ESP even when the AntiBot module is disabled.
     * A player absent from the server player list is usually a fake-player/NPC entity.
     * Offline UUID v3 is valid and is deliberately accepted.
     */
    public static boolean isLikelyBot(Entity e) {
        if (!(e instanceof PlayerEntity p)) return true;
        if (!p.getGameProfile().getName().matches("[A-Za-z0-9_]{3,16}")) return true;
        int version = p.getUuid().version();
        if (version != 3 && version != 4) return true;

        var client = net.minecraft.client.MinecraftClient.getInstance();
        if (client.getNetworkHandler() != null
                && client.getNetworkHandler().getPlayerListEntry(p.getUuid()) == null) {
            return true;
        }
        return false;
    }

    public static boolean isBot(Entity e) {
        AntiBot m = MyCheat.MODULES.get(AntiBot.class);
        if (isLikelyBot(e)) return true;
        if (m == null || !m.isEnabled() || !(e instanceof PlayerEntity p)) return false;
        // настоящие игроки есть в списке игроков (tab) — ненадёжно на некоторых серверах, поэтому выключено по умолчанию
        if (m.tab.getInt() == 1 && mc().getNetworkHandler() != null
                && mc().getNetworkHandler().getPlayerListEntry(p.getUuid()) == null) return true;
        // у премиум-аккаунтов UUID версии 4, у оффлайн/пиратских — версии 3 (это НОРМАЛЬНО, не бот);
        // ботом считаем только совсем не похожий на Minecraft UUID (не 3 и не 4)
        if (m.uuid.getInt() == 1) {
            int v = p.getUuid().version();
            if (v != 3 && v != 4) return true;
        }
        // ник игрока: 3-16 символов, буквы, цифры, _
        if (m.name.getInt() == 1 && !p.getGameProfile().getName().matches("\\w{3,16}")) return true;
        return false;
    }
}
