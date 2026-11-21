package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import dev.mycheat.util.Friends;
import dev.mycheat.module.impl.AntiBot;
import dev.mycheat.util.RenderUtil;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/** Player ESP: draws camera-relative 3D boxes and optionally enables the vanilla outline. */
public class ESP extends Module {
    private final NumberSetting glow = add(new NumberSetting("Glow", 0, 0, 1, 1));
    private final NumberSetting box = add(new NumberSetting("Box", 1, 0, 1, 1));

    public ESP() {
        super("ESP", Category.RENDER,
                "Показывает игроков 3D-боксами сквозь стены. Друзья отмечаются зелёным, остальные — красным.");
    }

    @Override
    public void onTick() {
        if (mc().world == null || mc().player == null) return;
        // Vanilla glow is optional; the ESP box below does not depend on entity glowing metadata.
        if (glow.getInt() == 1) {
            for (Entity e : mc().world.getEntities()) {
                if (e instanceof PlayerEntity pl && pl != mc().player && pl.isAlive()
                        && !AntiBot.isBot(pl)) pl.setGlowing(true);
            }
        }
    }

    @Override
    protected void onDisable() {
        if (mc().world == null) return;
        for (Entity e : mc().world.getEntities()) {
            if (e instanceof PlayerEntity pl && pl != mc().player) pl.setGlowing(false);
        }
    }

    public void render(WorldRenderContext ctx) {
        if (box.getInt() == 0 || ctx.matrixStack() == null || mc().world == null || mc().player == null) return;

        float tickDelta = ctx.tickCounter().getTickDelta(false);
        Vec3d camera = ctx.camera().getPos();
        var matrices = ctx.matrixStack();
        matrices.push();
        // WorldRenderContext's matrix stack is in world space; translate once into camera space.
        matrices.translate(-camera.x, -camera.y, -camera.z);

        try {
            for (Entity entity : mc().world.getEntities()) {
                if (!(entity instanceof PlayerEntity player) || player == mc().player || !player.isAlive()
                        || player.isSpectator() || AntiBot.isBot(player)) continue;

                double ix = MathHelper.lerp(tickDelta, entity.prevX, entity.getX());
                double iy = MathHelper.lerp(tickDelta, entity.prevY, entity.getY());
                double iz = MathHelper.lerp(tickDelta, entity.prevZ, entity.getZ());
                double dx = ix - entity.getX();
                double dy = iy - entity.getY();
                double dz = iz - entity.getZ();
                var interpolatedBox = entity.getBoundingBox().offset(dx, dy, dz);

                boolean friend = Friends.isFriend(player);
                RenderUtil.box(matrices, interpolatedBox,
                        friend ? 0.25f : 1.0f,
                        friend ? 1.0f : 0.20f,
                        friend ? 0.35f : 0.20f,
                        0.95f);
            }
        } finally {
            matrices.pop();
        }
    }
}
