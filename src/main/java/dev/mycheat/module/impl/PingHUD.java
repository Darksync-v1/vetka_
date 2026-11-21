package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.util.HudPositions;
import net.minecraft.client.gui.DrawContext;

/** Displays the local player's server latency. */
public class PingHUD extends Module {
    public PingHUD(){ super("Ping", Category.RENDER, "Показывает задержку до сервера в миллисекундах."); }
    public void render(DrawContext ctx){
        if(!isEnabled() || mc().player==null || mc().getNetworkHandler()==null) return;
        var entry=mc().getNetworkHandler().getPlayerListEntry(mc().player.getUuid());
        if(entry==null) return;
        String value="Ping: "+PingSpoof.displayPing(entry.getLatency())+" ms";
        int x=HudPositions.x("ping",5),y=HudPositions.y("ping",53),w=mc().textRenderer.getWidth(value)+10;
        ctx.fill(x,y,x+w,y+16,0xCC171923);
        ctx.drawTextWithShadow(mc().textRenderer,value,x+5,y+4,0xFFD2B7FF);
    }
}
