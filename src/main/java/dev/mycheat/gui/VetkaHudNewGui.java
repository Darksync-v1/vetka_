package dev.mycheat.gui;

import dev.mycheat.MyCheat;
import dev.mycheat.module.Module;
import dev.mycheat.module.impl.VetkaHudNew;
import dev.mycheat.setting.NumberSetting;
import dev.mycheat.util.Config;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;
import java.util.List;

/** Six-column, translucent layout based on the supplied Vetka screenshot. */
public class VetkaHudNewGui extends Screen {
    private static final String[] TITLES = {"Combat", "Movement", "Render", "Player", "Misc", "Client"};
    private static final int[] HUD_HASHES = {"TargetHUD".hashCode(), "Crosshair".hashCode(), "Coordinates".hashCode(), "FPS".hashCode(), "ArmorStatus".hashCode(), "ChinaHat".hashCode(), "NameTags".hashCode(), "TargetESP".hashCode(), "ESP".hashCode()};
    private final int[] scroll = new int[6];
    private Module binding;
    private Module expanded;
    private record Hit(int x1,int y1,int x2,int y2,Module module,NumberSetting setting) {
        boolean contains(double x,double y){return x>=x1&&x<x2&&y>=y1&&y<y2;}
    }
    private final List<Hit> hits = new ArrayList<>();

    public VetkaHudNewGui(){ super(Text.literal("VetkaHudNew")); }
    @Override public boolean shouldPause(){return false;}
    @Override public void removed(){Config.save();}

    private boolean playerHud(Module m){
        for(int h:HUD_HASHES) if(m.getName().hashCode()==h) return true;
        return false;
    }
    private List<Module> modules(int col){
        List<Module> out=new ArrayList<>();
        for(Module m:MyCheat.MODULES.getAll()){
            boolean add=switch(col){
                case 0 -> m.getCategory().name().equals("COMBAT");
                case 1 -> m.getCategory().name().equals("MOVEMENT");
                case 2 -> m.getCategory().name().equals("RENDER") && !playerHud(m);
                case 3 -> playerHud(m);
                case 4 -> m.getCategory().name().equals("MISC") && !(m instanceof VetkaHudNew);
                default -> m instanceof VetkaHudNew;
            };
            if(add) out.add(m);
        }
        return out;
    }
    private static String keyName(int key){
        if(key<0)return "";
        String s=InputUtil.Type.KEYSYM.createFromCode(key).getLocalizedText().getString();
        return s.length()>7?s.substring(0,7):s;
    }
    @Override public void render(DrawContext ctx,int mx,int my,float delta){
        hits.clear();
        int gap=6, margin=8, top=8, bottom=height-22;
        int colW=Math.max(118,(width-margin*2-gap*5)/6);
        ctx.fill(0,0,width,height,0x18000000);
        for(int c=0;c<6;c++){
            int x=margin+c*(colW+gap), y=top;
            ctx.fill(x,y,x+colW,bottom,0xB911141B);
            ctx.fill(x,y,x+colW,y+25,0xE51C202A);
            ctx.fill(x,y,x+colW,y+2,0xFF9B68E8);
            String title=TITLES[c];
            ctx.drawTextWithShadow(textRenderer,title,x+8,y+9,0xFFF2EFFA);
            List<Module> list=modules(c);
            String count=""+list.size();
            ctx.drawTextWithShadow(textRenderer,count,x+colW-8-textRenderer.getWidth(count),y+9,0xFF999BAA);
            int rowY=y+30;
            ctx.enableScissor(x+2,y+27,x+colW-2,bottom-3);
            int offset=scroll[c];
            for(Module m:list){
                int h=20;
                int ry=rowY-offset;
                boolean visible=ry+h>y+27&&ry<bottom-3;
                if(visible){
                    boolean hover=mx>=x+4&&mx<x+colW-4&&my>=Math.max(ry,y+27)&&my<Math.min(ry+h,bottom-3);
                    ctx.fill(x+4,ry,x+colW-4,ry+h,hover?0xFF373B47:0xB9232731);
                    if(m.isEnabled())ctx.fill(x+4,ry,x+7,ry+h,0xFF9B68E8);
                    String name=m.getName();
                    int max=colW-44;
                    while(textRenderer.getWidth(name)>max&&name.length()>4)name=name.substring(0,name.length()-1);
                    ctx.drawTextWithShadow(textRenderer,name,x+11,ry+6,m.isEnabled()?0xFFFFFFFF:0xFFB9BBC5);
                    String right=binding==m?"[...]":keyName(m.getKey());
                    if(expanded==m)right+=" v";
                    if(!right.isEmpty())ctx.drawTextWithShadow(textRenderer,right,x+colW-8-textRenderer.getWidth(right),ry+6,0xFFB5A0E8);
                    hits.add(new Hit(x+4,Math.max(ry,y+27),x+colW-4,Math.min(ry+h,bottom-3),m,null));
                    if(expanded==m){
                        int sy=ry+h;
                        for(NumberSetting st:m.getSettings()){
                            if(sy+17>y+27&&sy<bottom-3){
                                ctx.fill(x+8,sy,x+colW-8,sy+17,0xB512151D);
                                String label=st.name+"  "+(st.get()==Math.rint(st.get())?(long)st.get():String.format("%.1f",st.get()));
                                if(textRenderer.getWidth(label)>colW-20)label=st.name;
                                ctx.drawTextWithShadow(textRenderer,label,x+11,sy+5,0xFFD6D2E0);
                                int bw=(int)((colW-16)*st.normalized());ctx.fill(x+8,sy+15,x+8+bw,sy+17,0xFF9B68E8);
                                hits.add(new Hit(x+8,Math.max(sy,y+27),x+colW-8,Math.min(sy+17,bottom-3),null,st));
                            }
                            sy+=18;
                        }
                        h+=m.getSettings().size()*18;
                    }
                }
                rowY+=h+2;
            }
            ctx.disableScissor();
        }
        String help=binding==null?"ЛКМ — toggle    ПКМ — settings    СКМ — bind    ESC — close":"Нажми клавишу для бинда · ESC — снять бинд";
        ctx.drawCenteredTextWithShadow(textRenderer,help,width/2,height-13,0xFFE0DCEB);
        super.render(ctx,mx,my,delta);
    }
    @Override public boolean mouseScrolled(double mx,double my,double horizontal,double vertical){
        int gap=6,margin=8,colW=Math.max(118,(width-margin*2-gap*5)/6);
        for(int c=0;c<6;c++){int x=margin+c*(colW+gap);if(mx>=x&&mx<x+colW){scroll[c]=Math.max(0,scroll[c]-(int)Math.round(vertical*24));return true;}}
        return super.mouseScrolled(mx,my,horizontal,vertical);
    }
    @Override public boolean mouseClicked(double mx,double my,int button){
        
        for(Hit h:hits)if(h.contains(mx,my)){
            if(h.setting()!=null){if(button==0){double t=Math.max(0,Math.min(1,(mx-h.x1())/(h.x2()-h.x1())));NumberSetting s=h.setting();s.set(s.min+t*(s.max-s.min));}return true;}
            if(h.module()!=null){if(button==0)h.module().toggle();else if(button==1)expanded=expanded==h.module()?null:h.module();else if(button==2)binding=h.module();return true;}
        }
        return super.mouseClicked(mx,my,button);
    }
    @Override public boolean keyPressed(int key,int scan,int modifiers){
        if(binding!=null){if(key==GLFW.GLFW_KEY_ESCAPE)binding.setKey(-1);else binding.setKey(key);binding=null;Config.save();return true;}
        if(key==GLFW.GLFW_KEY_RIGHT_SHIFT||key==GLFW.GLFW_KEY_ESCAPE){close();return true;}
        return super.keyPressed(key,scan,modifiers);
    }
}
