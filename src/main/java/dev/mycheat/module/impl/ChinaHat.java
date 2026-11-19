package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

/** Animated cosmetic cone above the local player's head. */
public final class ChinaHat extends Module {
    private final NumberSetting radius = add(new NumberSetting("Radius", 0.65, 0.2, 1.2, 0.05));
    private final NumberSetting height = add(new NumberSetting("Height", 0.28, 0.1, 0.7, 0.02));
    private final NumberSetting opacity = add(new NumberSetting("Opacity", 0.75, 0.1, 1.0, 0.05));

    public ChinaHat() { super("ChinaHat", Category.RENDER, "Радужная коническая шляпа над головой игрока."); }

    public void render(WorldRenderContext ctx) {
        if (mc().player == null || ctx.matrixStack() == null) return;
        float tick = ctx.tickCounter().getTickDelta(false);
        Vec3d camera = ctx.camera().getPos();
        double x = MathHelper.lerp(tick, mc().player.prevX, mc().player.getX()) - camera.x;
        double y = MathHelper.lerp(tick, mc().player.prevY, mc().player.getY()) - camera.y;
        double z = MathHelper.lerp(tick, mc().player.prevZ, mc().player.getZ()) - camera.z;
        float r = (float) radius.get();
        float h = (float) height.get();
        int alpha = (int)(opacity.get() * 255);
        var matrices = ctx.matrixStack();
        matrices.push();
        matrices.translate(x, y + mc().player.getHeight() - 0.05, z);
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc(); RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
        long now = System.currentTimeMillis();
        int segments = 48;
        for (int i = 0; i < segments; i++) {
            double a1 = Math.PI * 2 * i / segments;
            double a2 = Math.PI * 2 * (i + 1) / segments;
            int c1 = dev.mycheat.util.RenderUtil.rainbow(now / 1000.0, i / (double)segments);
            int c2 = dev.mycheat.util.RenderUtil.rainbow(now / 1000.0, (i + 1) / (double)segments);
            float[] p1 = {(float)Math.cos(a1)*r, 0, (float)Math.sin(a1)*r};
            float[] p2 = {(float)Math.cos(a2)*r, 0, (float)Math.sin(a2)*r};
            buffer.vertex(matrix, 0, h, 0).color(255,255,255,alpha);
            buffer.vertex(matrix, p1[0], 0, p1[2]).color((c1>>16)&255,(c1>>8)&255,c1&255,alpha);
            buffer.vertex(matrix, p2[0], 0, p2[2]).color((c2>>16)&255,(c2>>8)&255,c2&255,alpha);
        }
        BufferRenderer.drawWithGlobalProgram(buffer.end());
        RenderSystem.enableCull(); RenderSystem.disableBlend();
        matrices.pop();
    }
}
