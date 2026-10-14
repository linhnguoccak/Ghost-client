package com.ghost.client.module.impl;

import com.ghost.client.module.Module;
import com.ghost.client.module.config.Setting;

public class HitboxModule extends Module {

    public final Setting<Double> expandSize = new Setting<>(
        "Expand Size", "How much to expand entity hitboxes", 0.3, 0.0, 1.5);

    public HitboxModule() {
        super("Hitbox", "Expands entity hitboxes to make them easier to hit", Category.COMBAT);
        settings.add(expandSize);
    }
}
