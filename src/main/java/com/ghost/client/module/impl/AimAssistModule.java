package com.ghost.client.module.impl;

import com.ghost.client.module.Module;
import com.ghost.client.module.config.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class AimAssistModule extends Module {

    public final Setting<Double> fov = new Setting<>("FOV", "Search FOV degrees", 90.0, 10.0, 180.0);
    public final Setting<Double> speed = new Setting<>("Speed", "Rotation speed", 3.0, 0.5, 10.0);
    public final Setting<Double> range = new Setting<>("Range", "Target range", 6.0, 1.0, 10.0);
    public final Setting<Boolean> targetPlayers = new Setting<>("Target Players", "Aim at players", true);

    public AimAssistModule() {
        super("AimAssist", "Smoothly rotates towards nearest enemy", Category.COMBAT);
        settings.add(fov);
        settings.add(speed);
        settings.add(range);
        settings.add(targetPlayers);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        Entity target = findTarget();
        if (target == null) return;

        Vec3d diff = target.getPos().add(0, target.getHeight() * 0.8, 0).subtract(mc.player.getEyePos());
        double dist = diff.horizontalLength();
        double yaw = Math.toDegrees(Math.atan2(-diff.x, diff.z));
        double pitch = Math.toDegrees(-Math.atan2(diff.y, dist));

        float spd = speed.getValue().floatValue();
        float dYaw = clamp((float) wrapDeg(yaw - mc.player.getYaw()), -spd, spd);
        float dPitch = clamp((float) wrapDeg(pitch - mc.player.getPitch()), -spd, spd);

        mc.player.setYaw(mc.player.getYaw() + dYaw);
        mc.player.setPitch(mc.player.getPitch() + dPitch);
    }

    private Entity findTarget() {
        double r = range.getValue();
        Entity best = null; double bestDist = Double.MAX_VALUE;
        for (Entity e : mc.world.getEntities()) {
            if (e == mc.player) continue;
            if (targetPlayers.getValue() && !(e instanceof PlayerEntity)) continue;
            double d = mc.player.distanceTo(e);
            if (d > r || !isInFov(e)) continue;
            if (d < bestDist) { bestDist = d; best = e; }
        }
        return best;
    }

    private boolean isInFov(Entity e) {
        Vec3d diff = e.getPos().subtract(mc.player.getPos()).normalize();
        double angle = Math.toDegrees(Math.acos(Math.max(-1, Math.min(1, diff.dotProduct(mc.player.getRotationVector())))));
        return angle <= fov.getValue() / 2;
    }

    private double wrapDeg(double d) {
        d %= 360; if (d >= 180) d -= 360; if (d < -180) d += 360; return d;
    }

    private float clamp(float v, float lo, float hi) { return Math.max(lo, Math.min(hi, v)); }
}
