package dev.mycheat.gui;

import dev.mycheat.util.Config;
import dev.mycheat.util.HudPositions;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;

/** Drag-and-drop editor for on-screen Vetka HUD widgets. */
public final class HudEditorScreen extends Screen {
    private record Widget(String id, String label, int defaultX, int defaultY, int width, int height) {}
    private record Rect(Widget widget, int x, int y) {}
    private final List<Widget> widgets = List.of(
            new Widget("coordinates","Coordinates  XYZ",5,5,150,25),
            new Widget("fps","FPS",5,33,90,18),
            new Widget("ping","Ping",5,53,110,18),
            new Widget("keystrokes","Keystrokes + CPS",8, heightSafe()-92,116,92),
            new Widget("potions","Potions",14,130,118,112),
            new Widget("armor","ArmorStatus",widthSafe()/2-74,heightSafe()-58,90,24),
            new Widget("target","Target Info",widthSafe()/2-78,heightSafe()/2+34,156,42)
    );
    private final List<Rect> rects = new ArrayList<>();
    private String dragging;
    private int dragDx, dragDy;

    private static int widthSafe(){ return net.minecraft.client.MinecraftClient.getInstance().getWindow().getScaledWidth(); }
    private static int heightSafe(){ return net.minecraft.client.MinecraftClient.getInstance().getWindow().getScaledHeight(); }

    public HudEditorScreen(){ super(Text.literal("Vetka HUD Editor")); }
    @Override public boolean shouldPause(){ return false; }

    @Override public void render(DrawContext ctx,int mouseX,int mouseY,float delta){
        rects.clear();
        ctx.fill(0,0,width,height,0x55000000);
        ctx.drawCenteredTextWithShadow(textRenderer,"HUD EDITOR — перетаскивай элементы мышью",width/2,12,0xFFD9B8FF);
        for(Widget w:widgets){
            int x=HudPositions.x(w.id,w.defaultX), y=HudPositions.y(w.id,w.defaultY);
            x=Math.max(0,Math.min(x,width-w.width)); y=Math.max(0,Math.min(y,height-w.height));
            Rect r=new Rect(w,x,y); rects.add(r);
            boolean hover=mouseX>=x&&mouseX<=x+w.width&&mouseY>=y&&mouseY<=y+w.height;
            ctx.fill(x,y,x+w.width,y+w.height,hover?0xDD6840A8:0xCC171923);
            ctx.fill(x,y,x+w.width,y+2,0xFFAA78FF);
            ctx.drawTextWithShadow(textRenderer,w.label,x+5,y+6,0xFFFFFFFF);
            ctx.drawBorder(x,y,w.width,w.height,0xFFB88AFF);
        }
        ctx.drawCenteredTextWithShadow(textRenderer,"ЛКМ — двигать · ESC — сохранить и закрыть",width/2,height-18,0xFFE7E0F2);
        super.render(ctx,mouseX,mouseY,delta);
    }

    @Override public boolean mouseClicked(double mouseX,double mouseY,int button){
        if(button==0){
            for(int i=rects.size()-1;i>=0;i--){
                Rect r=rects.get(i);
                if(mouseX>=r.x&&mouseX<=r.x+r.widget.width&&mouseY>=r.y&&mouseY<=r.y+r.widget.height){
                    dragging=r.widget.id; dragDx=(int)mouseX-r.x; dragDy=(int)mouseY-r.y; return true;
                }
            }
        }
        return super.mouseClicked(mouseX,mouseY,button);
    }
    @Override public boolean mouseDragged(double mouseX,double mouseY,int button,double deltaX,double deltaY){
        if(dragging!=null&&button==0){
            Widget w=widgets.stream().filter(item->item.id.equals(dragging)).findFirst().orElse(null);
            if(w!=null) HudPositions.set(dragging,
                    Math.max(0,Math.min((int)mouseX-dragDx,width-w.width)),
                    Math.max(0,Math.min((int)mouseY-dragDy,height-w.height)));
            return true;
        }
        return super.mouseDragged(mouseX,mouseY,button,deltaX,deltaY);
    }
    @Override public boolean mouseReleased(double mouseX,double mouseY,int button){
        if(button==0&&dragging!=null){ dragging=null; Config.save(); return true; }
        return super.mouseReleased(mouseX,mouseY,button);
    }
    @Override public boolean keyPressed(int key,int scan,int modifiers){
        if(key==256){ Config.save(); close(); return true; }
        return super.keyPressed(key,scan,modifiers);
    }
    @Override public void close(){ Config.save(); super.close(); }
}
