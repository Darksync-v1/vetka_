package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import net.minecraft.client.gui.DrawContext;

/** Displays the local player's server latency. */
public class PingHUD extends Module {
    public PingHUD(){ super("Ping", Category.RENDER, "Показывает задержку до сервера в миллисекундах."); }
    public void render(DrawContext ctx){
        if(!isEnabled() || mc().player==null || mc().getNetworkHandler()==null) return;
        var entry=mc().getNetworkHandler().getPlayerListEntry(mc().player.getUuid());
        if(entry==null) return;
        String value="Ping: "+entry.getLatency()+" ms";
        int x=5,y=50,w=mc().textRenderer.getWidth(value)+10;
        ctx.fill(x,y,x+w,y+14,0xCC171923);
        ctx.drawTextWithShadow(mc().textRenderer,value,x+5,y+3,0xFFD2B7FF);
    }
}
