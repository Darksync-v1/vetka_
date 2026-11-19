package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class Fullbright extends Module {
    private final NumberSetting strength = add(new NumberSetting("Strength", 0, 0, 2, 1));

    public Fullbright() { super("Fullbright", Category.RENDER, "Постоянное ночное зрение: в темноте всё хорошо видно. Strength — уровень эффекта."); }

    @Override public void onTick() {
        mc().player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 500, strength.getInt(), false, false, false));
    }
    @Override protected void onDisable() { mc().player.removeStatusEffect(StatusEffects.NIGHT_VISION); }
}
