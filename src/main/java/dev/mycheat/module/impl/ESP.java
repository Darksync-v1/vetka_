package dev.mycheat.module.impl;

import dev.mycheat.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class ESP extends Module {
    public ESP() { super("ESP"); }
    @Override public void onTick() {
        for (Entity e : mc.world.getEntities())
            if (e instanceof PlayerEntity && e != mc.player) e.setGlowing(true);
    }
    @Override protected void onDisable() {
        for (Entity e : mc.world.getEntities())
            if (e instanceof PlayerEntity) e.setGlowing(false);
    }
}
