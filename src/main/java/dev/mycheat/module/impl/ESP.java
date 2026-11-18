package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import dev.mycheat.util.Friends;
import dev.mycheat.util.RenderUtil;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/** Подсвечивает игроков сквозь стены: контур (glow) и/или 3D-бокс. */
public class ESP extends Module {
    private final NumberSetting glow = add(new NumberSetting("Glow", 1, 0, 1, 1));
    private final NumberSetting box = add(new NumberSetting("Box", 1, 0, 1, 1));

    public ESP() {
        super("ESP", Category.RENDER, "Подсвечивает игроков сквозь стены контуром и/или объёмным боксом. Друзья отмечаются зелёным, враги — красным.");
    }

    @Override public void onTick() {
        for (Entity e : mc().world.getEntities())
            if (e instanceof PlayerEntity pl && pl != mc().player) pl.setGlowing(glow.getInt() == 1);
    }
    @Override protected void onDisable() {
        for (Entity e : mc().world.getEntities()) if (e instanceof PlayerEntity pl) pl.setGlowing(false);
    }

    public void render(WorldRenderContext ctx) {
        if (box.getInt() == 0 || ctx.matrixStack() == null) return;
        float delta = ctx.tickCounter().getTickDelta(false);
        Vec3d cam = ctx.camera().getPos();
        for (Entity e : mc().world.getEntities()) {
            if (!(e instanceof PlayerEntity pl) || pl == mc().player) continue;
            double x = MathHelper.lerp(delta, e.prevX, e.getX()) - cam.x;
            double y = MathHelper.lerp(delta, e.prevY, e.getY()) - cam.y;
            double z = MathHelper.lerp(delta, e.prevZ, e.getZ()) - cam.z;
            var local = e.getBoundingBox().offset(x - e.getX(), y - e.getY(), z - e.getZ());
            boolean friend = Friends.isFriend(pl);
            ctx.matrixStack().push();
            RenderUtil.box(ctx.matrixStack(), local, friend ? 0.3f : 1.0f, friend ? 1.0f : 0.25f, 0.3f, 0.85f);
            ctx.matrixStack().pop();
        }
    }
}
