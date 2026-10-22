package com.ghost.client.module.impl;

import com.ghost.client.module.Module;
import com.ghost.client.module.config.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class AntiKickModule extends Module {

    private final Setting<Double> interval = new Setting<>("Interval", 20.0, 5.0, 100.0);
    private int ticker = 0;

    public AntiKickModule() {
        super("AntiKick", "Prevents AFK kick", "Player");
        addSetting(interval);
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;
        ticker++;
        if (ticker >= interval.getValue().intValue()) {
            ticker = 0;
            mc.player.networkHandler.sendPacket(
                new PlayerMoveC2SPacket.OnGroundOnly(mc.player.isOnGround())
            );
            mc.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
        }
    }
}
