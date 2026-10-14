package com.ghost.client;

import com.ghost.client.gui.ClickGUI;
import com.ghost.client.module.ModuleManager;
import com.ghost.client.util.ConfigManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GhostClient implements ClientModInitializer {

    public static final String MOD_NAME = "Ghost";
    public static final String MOD_VERSION = "1.0";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static GhostClient INSTANCE;
    public static MinecraftClient mc;

    private static ModuleManager moduleManager;
    private static ClickGUI clickGUI;
    private static ConfigManager configManager;

    private static KeyBinding guiKey;
    private static KeyBinding panicKey;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        mc = MinecraftClient.getInstance();

        moduleManager = new ModuleManager();
        configManager = new ConfigManager();
        clickGUI = new ClickGUI();

        configManager.load();

        guiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.ghost.gui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_INSERT,
            "category.ghost"
        ));

        panicKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.ghost.panic",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_END,
            "category.ghost"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (guiKey.wasPressed()) {
                clickGUI.toggle();
            }
            while (panicKey.wasPressed()) {
                moduleManager.disableAll();
            }
            moduleManager.onTick();
        });

        LOGGER.info("[Ghost] v{} loaded", MOD_VERSION);
    }

    public static ModuleManager getModuleManager() { return moduleManager; }
    public static ClickGUI getClickGUI()           { return clickGUI; }
    public static ConfigManager getConfigManager() { return configManager; }
}
