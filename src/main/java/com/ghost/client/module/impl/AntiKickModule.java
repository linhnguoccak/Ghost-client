package com.ghost.client.module.impl;

import com.ghost.client.module.Module;
import com.ghost.client.module.config.Setting;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class AntiKickModule extends Module {

    public final Setting<Integer> interval = new Setting<>(
        "Interval (ticks)", "Ticks between packets", 30, 5, 100);
    public final Setting<Boolean> swingPacket = new Setting<>(
        "Swing Packet", "Send swing arm packet", true);
    public final Setting<Boolean> movePacket = new Setting<>(
        "Move Packet", "Send tiny movement packet", true);

    private int tickTimer = 0;

    public AntiKickModule() {
        super("AntiKick", "Prevents AFK kick / anti-band", Category.PLAYER);
        settings.add(interval);
        settings.add(swingPacket);
        settings.add(movePacket);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.getNetworkHandler() == null) return;
        tickTimer++;
        if (tickTimer < interval.getValue()) return;
        tickTimer = 0;
        if (movePacket.getValue()) {
            mc.getNetworkHandler().sendPacket(
                new PlayerMoveC2SPacket.OnGroundOnly(mc.player.isOnGround(), mc.player.horizontalCollision));
        }
        if (swingPacket.getValue()) {
            mc.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
        }
    }
}
