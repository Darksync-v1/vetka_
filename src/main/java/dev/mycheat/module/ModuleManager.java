package dev.mycheat.module;

import dev.mycheat.module.impl.*;
import net.minecraft.client.MinecraftClient;
import java.util.List;

public class ModuleManager {
    private final List<Module> modules = List.of(
            new KillAura(), new AimBot(), new ElytraAura(), new Criticals(), new AntiBot(), new FriendSystem(), new TargetManager(), new BackTrack(), new PingSpoof(), new TotemAnimation(), new PVPTools(),
            new TargetStrafe(), new AutoTotem(), new Velocity(),
            new Sprint(), new Fly(), new Speed(), new Step(), new Jesus(), new ElytraFly(),
            new Phase(), new ClickTP(), new NoFall(),
            new Fullbright(), new ESP(), new TargetESP(), new TargetHUD(), new Crosshair(), new Coordinates(), new FPS(), new ArmorStatus(), new Keystrokes(), new PotionHUD(), new PingHUD(), new ChinaHat(), new NameTags(),
            new Boost(), new NoSlow(), new NoWeb(), new AutoEZ(), new PhotoMath(), new MessageAppend(), new MessagePrefix(),
            new NoServerRotate(), new NoServerSlot(), new GuiMove(), new VetkaHudNew(),
            new AntiWeb(), new Reach(), new TpsSync());

    public List<Module> getAll() { return modules; }

    @SuppressWarnings("unchecked")
    public <T extends Module> T get(Class<T> cls) {
        for (Module m : modules) if (cls.isInstance(m)) return (T) m;
        return null;
    }

    public void tick(MinecraftClient mc) {
        for (Module m : modules) if (m.isEnabled()) m.onTick();
    }
}
