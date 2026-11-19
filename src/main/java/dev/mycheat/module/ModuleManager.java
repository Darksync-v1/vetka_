package dev.mycheat.module;

import dev.mycheat.module.impl.*;
import net.minecraft.client.MinecraftClient;
import java.util.List;

public class ModuleManager {
    private final List<Module> modules = List.of(
            new KillAura(), new ElytraAura(), new TPAura(), new Criticals(), new AntiBot(),
            new TargetStrafe(), new AutoTotem(), new Velocity(),
            new Sprint(), new Fly(), new Speed(), new Step(), new Jesus(), new ElytraFly(),
            new Phase(), new ClickTP(), new NoFall(),
            new Fullbright(), new ESP(), new TargetESP(), new TargetHUD(), new Crosshair(), new Coordinates(), new FPS(), new ArmorStatus(), new ChinaHat(), new NameTags(),
            new FakeLag(), new Boost(), new NoSlow(), new NoWeb(), new AutoEZ(), new MessageAppend(), new MessagePrefix(),
            new NoServerRotate(), new NoServerSlot(), new GuiMove(),
            new AntiWeb(), new Reach(), new GroundBoost(), new TpsSync(), new Femboy());

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
