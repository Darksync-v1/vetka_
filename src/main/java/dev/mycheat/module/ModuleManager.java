package dev.mycheat.module;

import dev.mycheat.module.impl.*;
import net.minecraft.client.MinecraftClient;
import java.util.List;

public class ModuleManager {
    private final List<Module> modules = List.of(
            new Sprint(), new Fly(), new Speed(), new Fullbright(), new NoFall(),
            new Step(), new AutoTotem(), new ESP(), new KillAura(), new Velocity(), new Jesus(),
            new ElytraFly(), new ElytraAura());

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
