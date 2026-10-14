package com.ghost.client.gui;

import net.minecraft.client.MinecraftClient;

public class ClickGUI {
    private boolean open = false;

    public void toggle() {
        open = !open;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (open) mc.setScreen(new GhostScreen());
        else mc.setScreen(null);
    }

    public boolean isOpen() { return open; }
    public void setOpen(boolean v) { open = v; }
}
