package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffectInstance;

/** Lists active potion effects and remaining duration. */
public class PotionHUD extends Module {
    public PotionHUD(){ super("PotionHUD", Category.RENDER, "Показывает эффекты зелий и оставшееся время."); }
    public void render(DrawContext ctx){
        if(!isEnabled() || mc().player==null) return;
        int x=mc().getWindow().getScaledWidth()-142, y=8, rows=0;
        for(StatusEffectInstance effect:mc().player.getStatusEffects()){
            if(rows>=8) break;
            String name=effect.getEffectType().value().getName().getString();
            int seconds=Math.max(0,effect.getDuration()/20);
            String time=String.format(java.util.Locale.ROOT,"%d:%02d",seconds/60,seconds%60);
            String level=effect.getAmplifier()>0?" "+(effect.getAmplifier()+1):"";
            ctx.fill(x,y,x+134,y+20,0xCC171923);
            ctx.fill(x,y,x+2,y+20,0xFF9B68E8);
            ctx.drawTextWithShadow(mc().textRenderer,name+level,x+7,y+3,0xFFFFFFFF);
            int tw=mc().textRenderer.getWidth(time);
            ctx.drawTextWithShadow(mc().textRenderer,time,x+127-tw,y+3,0xFFD2B7FF);
            y+=22; rows++;
        }
    }
}
