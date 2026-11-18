package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class Fullbright extends Module {
    public Fullbright() { super("Fullbright", Category.RENDER, "Постоянное ночное зрение: в темноте всё хорошо видно."); }
    @Override public void onTick() {
        mc().player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 500, 0, false, false, false));
    }
    @Override protected void onDisable() { mc().player.removeStatusEffect(StatusEffects.NIGHT_VISION); }
}
