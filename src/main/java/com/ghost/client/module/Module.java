package com.ghost.client.module;

import com.ghost.client.module.config.Setting;
import java.util.ArrayList;
import java.util.List;

public abstract class Module {

    private final String name;
    private final String description;
    private final String category;
    private boolean enabled = false;
    private int keyBind = -1;
    private final List<Setting<?>> settings = new ArrayList<>();

    public Module(String name, String description, String category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    protected void addSetting(Setting<?> setting) {
        settings.add(setting);
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) onEnable();
        else onDisable();
    }

    public void onTick() {}
    public void onEnable() {}
    public void onDisable() {}

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    public int getKeyBind() { return keyBind; }
    public List<Setting<?>> getSettings() { return settings; }
}
