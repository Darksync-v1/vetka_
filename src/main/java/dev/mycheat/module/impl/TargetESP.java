package dev.mycheat.module.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.module.Targets;
import dev.mycheat.setting.NumberSetting;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

/** Рисует бегающее по цели кольцо (цель берётся из KillAura / ElytraAura). */
public class TargetESP extends Module {
    private final NumberSetting size = add(new NumberSetting("Size", 0.8, 0.3, 2.0, 0.1));
    private final NumberSetting speed = add(new NumberSetting("Speed", 1.0, 0.2, 3.0, 0.1));

    public TargetESP() { super("TargetESP", Category.RENDER, "Рисует кольцо, бегающее вверх и вниз по текущей цели ауры."); }

    public void render(WorldRenderContext ctx) {
        Entity t = Targets.get();
        if (t == null || ctx.matrixStack() == null) return;

        float delta = ctx.tickCounter().getTickDelta(false);
        Vec3d cam = ctx.camera().getPos();
        double x = MathHelper.lerp(delta, t.prevX, t.getX()) - cam.x;
        double y = MathHelper.lerp(delta, t.prevY, t.getY()) - cam.y;
        double z = MathHelper.lerp(delta, t.prevZ, t.getZ()) - cam.z;

        double time = System.currentTimeMillis() / 1000.0 * speed.get();
        double h = t.getHeight();
        double yy = y + h * (0.5 + 0.5 * Math.sin(time * 2.0));
        double r = t.getWidth() * size.get();

        Matrix4f m = ctx.matrixStack().peek().getPositionMatrix();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.lineWidth(3.0f);

        BufferBuilder buf = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
        int n = 36;
        for (int i = 0; i <= n; i++) {
            double a = i * (Math.PI * 2 / n);
            buf.vertex(m, (float) (x + Math.cos(a) * r), (float) yy, (float) (z + Math.sin(a) * r))
                    .color(255, 60, 60, 255);
        }
        BufferRenderer.drawWithGlobalProgram(buf.end());

        RenderSystem.lineWidth(1.0f);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }
}
