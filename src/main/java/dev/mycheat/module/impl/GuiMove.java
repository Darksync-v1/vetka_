package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

/**
 * Позволяет ходить WASD, пока открыт инвентарь, сундук, крафт-стол и т.п.
 * Камера не поворачивается (мышь занята интерфейсом), двигаешься в ту сторону, куда смотрел
 * до открытия экрана.
 */
public class GuiMove extends Module {
    private final NumberSetting speed = add(new NumberSetting("Speed", 0.215, 0.1, 0.3, 0.005));

    public GuiMove() {
        super("GuiMove", Category.MISC, "Даёт ходить WASD, пока открыт инвентарь или другой экран. Камера при этом не крутится.");
    }

    @Override public void onTick() {
        var p = mc().player;
        if (mc().currentScreen == null) return;
        long win = mc().getWindow().getHandle();
        boolean w = InputUtil.isKeyPressed(win, GLFW.GLFW_KEY_W);
        boolean a = InputUtil.isKeyPressed(win, GLFW.GLFW_KEY_A);
        boolean s = InputUtil.isKeyPressed(win, GLFW.GLFW_KEY_S);
        boolean d = InputUtil.isKeyPressed(win, GLFW.GLFW_KEY_D);
        double f = (w ? 1 : 0) - (s ? 1 : 0), side = (a ? 1 : 0) - (d ? 1 : 0);
        if (f == 0 && side == 0) return;
        double yaw = Math.toRadians(p.getYaw());
        double sin = Math.sin(yaw), cos = Math.cos(yaw);
        double x = side * cos - f * sin, z = f * cos + side * sin, len = Math.hypot(x, z);
        Vec3d v = p.getVelocity();
        p.setVelocity(x / len * speed.get(), v.y, z / len * speed.get());
    }
}
