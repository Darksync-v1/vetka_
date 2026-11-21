package dev.mycheat.util;

import dev.mycheat.MyCheat;
import dev.mycheat.module.impl.ArmorStatus;
import dev.mycheat.module.impl.Coordinates;
import dev.mycheat.module.impl.FPS;
import dev.mycheat.module.impl.Keystrokes;
import dev.mycheat.module.impl.PingHUD;
import dev.mycheat.module.impl.PotionHUD;
import dev.mycheat.module.impl.TargetHUD;
import net.minecraft.client.MinecraftClient;

/** Right-mouse drag controller for HUD widgets while the chat screen is open. */
public final class HudDragHandler {
    private record Widget(String id, int defaultX, int defaultY, int width, int height, boolean enabled) {}
    private static String dragging;
    private static int offsetX, offsetY;

    private HudDragHandler() {}

    public static boolean begin(double mouseX, double mouseY) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null || mc.currentScreen == null
                || !mc.currentScreen.getClass().getName().endsWith("ChatScreen")) return false;

        int sw = mc.getWindow().getScaledWidth();
        int sh = mc.getWindow().getScaledHeight();
        Widget[] widgets = {
                new Widget("coordinates",5,5,150,25,enabled(Coordinates.class)),
                new Widget("fps",5,33,90,18,enabled(FPS.class)),
                new Widget("ping",5,53,110,18,enabled(PingHUD.class)),
                new Widget("keystrokes",8,sh-92,116,92,enabled(Keystrokes.class)),
                new Widget("potions",sw-126,8,118,112,enabled(PotionHUD.class)),
                new Widget("armor",sw/2-74,sh-58,90,24,enabled(ArmorStatus.class)),
                new Widget("target",sw/2-78,sh/2+34,156,42,enabled(TargetHUD.class))
        };
        for (int i=widgets.length-1;i>=0;i--) {
            Widget w=widgets[i];
            if (!w.enabled) continue;
            int x=HudPositions.x(w.id,w.defaultX), y=HudPositions.y(w.id,w.defaultY);
            if (mouseX>=x && mouseX<=x+w.width && mouseY>=y && mouseY<=y+w.height) {
                dragging=w.id;
                offsetX=(int)mouseX-x;
                offsetY=(int)mouseY-y;
                return true;
            }
        }
        return false;
    }

    public static boolean drag(double mouseX, double mouseY, int button) {
        if (dragging == null || button != 1) return false;
        MinecraftClient mc=MinecraftClient.getInstance();
        int sw=mc.getWindow().getScaledWidth(), sh=mc.getWindow().getScaledHeight();
        int[] size=size(dragging);
        HudPositions.set(dragging,
                Math.max(0,Math.min((int)mouseX-offsetX,sw-size[0])),
                Math.max(0,Math.min((int)mouseY-offsetY,sh-size[1])));
        return true;
    }

    public static boolean release(int button) {
        if (dragging == null || button != 1) return false;
        dragging=null;
        Config.save();
        return true;
    }

    public static boolean isDragging() { return dragging != null; }

    private static int[] size(String id) {
        return switch(id) {
            case "coordinates" -> new int[]{150,25};
            case "fps" -> new int[]{90,18};
            case "ping" -> new int[]{110,18};
            case "keystrokes" -> new int[]{116,92};
            case "potions" -> new int[]{118,112};
            case "armor" -> new int[]{90,24};
            case "target" -> new int[]{156,42};
            default -> new int[]{1,1};
        };
    }

    private static <T extends dev.mycheat.module.Module> boolean enabled(Class<T> type) {
        T module = MyCheat.MODULES.get(type);
        return module != null && module.isEnabled();
    }
}
