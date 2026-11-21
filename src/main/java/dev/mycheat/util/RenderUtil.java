package dev.mycheat.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import org.joml.Matrix4f;

/** Общие функции отрисовки для ESP и похожих визуальных модулей. */
public final class RenderUtil {
    public static void box(MatrixStack stack, Box b, float r, float g, float bl, float a) {
        Matrix4f m = stack.peek().getPositionMatrix();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.lineWidth(2.0f);

        float x1 = (float) b.minX, y1 = (float) b.minY, z1 = (float) b.minZ;
        float x2 = (float) b.maxX, y2 = (float) b.maxY, z2 = (float) b.maxZ;
        int rr = (int) (r * 255), gg = (int) (g * 255), bb = (int) (bl * 255), aa = (int) (a * 255);

        BufferBuilder buf = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR);
        float[][] e = {
                {x1,y1,z1,x2,y1,z1},{x2,y1,z1,x2,y1,z2},{x2,y1,z2,x1,y1,z2},{x1,y1,z2,x1,y1,z1},
                {x1,y2,z1,x2,y2,z1},{x2,y2,z1,x2,y2,z2},{x2,y2,z2,x1,y2,z2},{x1,y2,z2,x1,y2,z1},
                {x1,y1,z1,x1,y2,z1},{x2,y1,z1,x2,y2,z1},{x2,y1,z2,x2,y2,z2},{x1,y1,z2,x1,y2,z2}};
        for (float[] s : e) {
            buf.vertex(m, s[0], s[1], s[2]).color(rr, gg, bb, aa);
            buf.vertex(m, s[3], s[4], s[5]).color(rr, gg, bb, aa);
        }
        BufferRenderer.drawWithGlobalProgram(buf.end());
        RenderSystem.lineWidth(1.0f);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    /** Цвет радуги по времени: t в секундах, offset сдвигает фазу (0..1). */
    public static int rainbow(double t, double offset) {
        float hue = (float) ((t * 0.15 + offset) % 1.0);
        return 0xFF000000 | (java.awt.Color.HSBtoRGB(hue, 0.65f, 1.0f) & 0xFFFFFF);
    }
}
