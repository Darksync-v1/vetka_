package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.ParticlesMode;
import dev.mycheat.setting.NumberSetting;

/** Снижает нагрузку на рендер: частицы, облака, тени сущностей, смешивание биомов, графика Fast. */
public class Boost extends Module {
    private final NumberSetting vsync = add(new NumberSetting("VSync off", 1, 0, 1, 1));
    private final NumberSetting mipmap = add(new NumberSetting("Mipmap off", 1, 0, 1, 1));
    private boolean oldVsync;
    private int oldMipmap;
    private ParticlesMode oldParticles;
    private CloudRenderMode oldClouds;
    private boolean oldShadows;
    private int oldBlend;
    private GraphicsMode oldGraphics;

    public Boost() {
        super("Boost", Category.MISC, "Прибавляет FPS: выключает облака, тени сущностей, смешивание биомов, снижает частицы и графику до Fast. Не меняет дальность прорисовки.");
    }

    @Override protected void onEnable() {
        var o = mc().options;
        oldParticles = o.getParticles().getValue();
        oldClouds = o.getCloudRenderMode().getValue();
        oldShadows = o.getEntityShadows().getValue();
        oldBlend = o.getBiomeBlendRadius().getValue();
        oldGraphics = o.getGraphicsMode().getValue();
        oldVsync = o.getEnableVsync().getValue();
        oldMipmap = o.getMipmapLevels().getValue();

        if (vsync.getInt() == 1) o.getEnableVsync().setValue(false);
        if (mipmap.getInt() == 1) o.getMipmapLevels().setValue(0);
        o.getParticles().setValue(ParticlesMode.MINIMAL);
        o.getCloudRenderMode().setValue(CloudRenderMode.OFF);
        o.getEntityShadows().setValue(false);
        o.getBiomeBlendRadius().setValue(0);
        o.getGraphicsMode().setValue(GraphicsMode.FAST);
    }

    @Override protected void onDisable() {
        var o = mc().options;
        if (oldParticles == null) return;
        o.getParticles().setValue(oldParticles);
        o.getCloudRenderMode().setValue(oldClouds);
        o.getEntityShadows().setValue(oldShadows);
        o.getBiomeBlendRadius().setValue(oldBlend);
        o.getGraphicsMode().setValue(oldGraphics);
        o.getEnableVsync().setValue(oldVsync);
        o.getMipmapLevels().setValue(oldMipmap);
    }
}
