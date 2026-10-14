package com.ghost.client.module;

import com.ghost.client.module.impl.*;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {

    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        register(new AutoCrystalModule());
        register(new HitboxModule());
        register(new AimAssistModule());
        register(new AutoTotemModule());
        register(new AntiKickModule());
    }

    private void register(Module m) {
        modules.add(m);
    }

    public void onTick() {
        for (Module m : modules) {
            if (m.isEnabled()) m.onTick();
        }
    }

    public void disableAll() {
        for (Module m : modules) {
            if (m.isEnabled()) m.setEnabled(false);
        }
    }

    public List<Module> getModules() { return modules; }

    public Module getByName(String name) {
        for (Module m : modules) {
            if (m.getName().equalsIgnoreCase(name)) return m;
        }
        return null;
    }
}
