package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

/** Compact WASD and mouse CPS HUD. */
public class Keystrokes extends Module {
    private long leftWindow, rightWindow;
    private int leftClicks, rightClicks, leftCps, rightCps;
    private boolean oldLeft, oldRight;
    public Keystrokes() { super("Keystrokes", Category.RENDER, "Показывает WASD, прыжок и CPS мыши."); }

    public void render(DrawContext ctx) {
        if (!isEnabled() || mc().player == null) return;
        long win = mc().getWindow().getHandle(), now = System.currentTimeMillis();
        boolean left = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean right = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        if (left && !oldLeft) leftClicks++;
        if (right && !oldRight) rightClicks++;
        oldLeft = left; oldRight = right;
        if (now - leftWindow >= 1000) { leftCps = leftClicks; leftClicks = 0; leftWindow = now; }
        if (now - rightWindow >= 1000) { rightCps = rightClicks; rightClicks = 0; rightWindow = now; }
        int x=8, y=mc().getWindow().getScaledHeight()-92;
        key(ctx,"W",x+25,y,GLFW.glfwGetKey(win,GLFW.GLFW_KEY_W)==1);
        key(ctx,"A",x,y+23,GLFW.glfwGetKey(win,GLFW.GLFW_KEY_A)==1);
        key(ctx,"S",x+25,y+23,GLFW.glfwGetKey(win,GLFW.GLFW_KEY_S)==1);
        key(ctx,"D",x+50,y+23,GLFW.glfwGetKey(win,GLFW.GLFW_KEY_D)==1);
        key(ctx,"SPACE",x,y+46,GLFW.glfwGetKey(win,GLFW.GLFW_KEY_SPACE)==1,75);
        key(ctx,"LMB "+leftCps,x,y+69,left,36);
        key(ctx,"RMB "+rightCps,x+40,y+69,right,36);
    }
    private void key(DrawContext c,String s,int x,int y,boolean down){key(c,s,x,y,down,22);}
    private void key(DrawContext c,String s,int x,int y,boolean down,int w){
        c.fill(x,y,x+w,y+20,down?0xFF7545C8:0xCC171923);
        c.drawCenteredTextWithShadow(mc().textRenderer,s,x+w/2,y+6,0xFFFFFFFF);
    }
}
