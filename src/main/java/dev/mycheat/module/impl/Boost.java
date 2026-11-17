package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.ParticlesMode;

/** Снижает нагрузку на рендер: частицы, облака, тени сущностей, смешивание биомов, графика Fast. */
public class Boost extends Module {
    private ParticlesMode oldParticles;
    private CloudRenderMode oldClouds;
    private boolean oldShadows;
    private int oldBlend;
    private GraphicsMode oldGraphics;

    public Boost() {
        super("Boost", Category.MISC, "Прибавляет FPS: выключает облака, тени сущностей, смешивание биомов, снижает частицы и графику до Fast. Не меняет дальность прорисовки.");
    }

    @Override protected void onEnable() {
        var o = mc.options;
        oldParticles = o.getParticles().getValue();
        oldClouds = o.getCloudRenderMode().getValue();
        oldShadows = o.getEntityShadows().getValue();
        oldBlend = o.getBiomeBlendRadius().getValue();
        oldGraphics = o.getGraphicsMode().getValue();

        o.getParticles().setValue(ParticlesMode.MINIMAL);
        o.getCloudRenderMode().setValue(CloudRenderMode.OFF);
        o.getEntityShadows().setValue(false);
        o.getBiomeBlendRadius().setValue(0);
        o.getGraphicsMode().setValue(GraphicsMode.FAST);
    }

    @Override protected void onDisable() {
        var o = mc.options;
        if (oldParticles == null) return;
        o.getParticles().setValue(oldParticles);
        o.getCloudRenderMode().setValue(oldClouds);
        o.getEntityShadows().setValue(oldShadows);
        o.getBiomeBlendRadius().setValue(oldBlend);
        o.getGraphicsMode().setValue(oldGraphics);
    }
}
