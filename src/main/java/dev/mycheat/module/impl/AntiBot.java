package dev.mycheat.module.impl;

import dev.mycheat.MyCheat;
import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

/** Отсеивает NPC и ботов-приманок, чтобы ауры их не били. Проверки включаются слайдерами. */
public class AntiBot extends Module {
    private final NumberSetting tab = add(new NumberSetting("TabList", 1, 0, 1, 1));
    private final NumberSetting uuid = add(new NumberSetting("UUID", 1, 0, 1, 1));
    private final NumberSetting name = add(new NumberSetting("Name", 1, 0, 1, 1));

    public AntiBot() { super("AntiBot", Category.COMBAT, "Исключает ботов и NPC из целей ауры: проверяет список игроков, UUID и формат ника."); }

    public static boolean isBot(Entity e) {
        AntiBot m = MyCheat.MODULES.get(AntiBot.class);
        if (m == null || !m.isEnabled() || !(e instanceof PlayerEntity p)) return false;
        // настоящие игроки есть в списке игроков (tab)
        if (m.tab.getInt() == 1 && mc.getNetworkHandler() != null
                && mc.getNetworkHandler().getPlayerListEntry(p.getUuid()) == null) return true;
        // у настоящих аккаунтов UUID версии 4, у NPC часто версия 2 и др.
        if (m.uuid.getInt() == 1 && p.getUuid().version() != 4) return true;
        // ник игрока: 3-16 символов, буквы, цифры, _
        if (m.name.getInt() == 1 && !p.getGameProfile().getName().matches("\\w{3,16}")) return true;
        return false;
    }
}
