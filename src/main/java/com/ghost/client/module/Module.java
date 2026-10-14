package com.ghost.client.module;

import com.ghost.client.module.config.Setting;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {

    protected final MinecraftClient mc = MinecraftClient.getInstance();

    private final String name;
    private final String description;
    private final Category category;
    private boolean enabled = false;
    private int keyBind = -1;

    protected final List<Setting<?>> settings = new ArrayList<>();

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public void onTick() {}
    public void onEnable() {}
    public void onDisable() {}

    public final void toggle() {
        enabled = !enabled;
        if (enabled) onEnable();
        else onDisable();
    }

    public final void setEnabled(boolean v) {
        if (v != enabled) toggle();
    }

    public String getName()          { return name; }
    public String getDescription()   { return description; }
    public Category getCategory()    { return category; }
    public boolean isEnabled()       { return enabled; }
    public int getKeyBind()          { return keyBind; }
    public void setKeyBind(int k)    { keyBind = k; }
    public List<Setting<?>> getSettings() { return settings; }

    public enum Category {
        COMBAT, PLAYER, RENDER, MOVEMENT, MISC
    }
}
