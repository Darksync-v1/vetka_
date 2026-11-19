package dev.mycheat.module.impl;

import dev.mycheat.MyCheat;
import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;

/**
 * Ручная подстройка скорости модулей движения (Speed, GroundBoost) под лаги сервера: если сервер
 * тикает медленнее 20 раз в секунду, слишком сильное ускорение быстрее палится. Настоящего
 * автоматического определения TPS сервера клиент не видит, поэтому это ручной множитель,
 * а не автоматика.
 */
public class TpsSync extends Module {
    private final NumberSetting scale = add(new NumberSetting("Scale", 1.0, 0.3, 1.5, 0.05));

    public TpsSync() {
        super("TpsSync", Category.MISC, "Ручной множитель скорости для Speed и GroundBoost (0.3-1.5). Если сервер лагает/тикает медленно, поставь ниже 1.0, чтобы не так палиться.");
    }

    public static double scale() {
        TpsSync m = MyCheat.MODULES.get(TpsSync.class);
        return (m != null && m.isEnabled()) ? m.scale.get() : 1.0;
    }
}
