package com.ghost.client.module.impl;

import com.ghost.client.module.Module;
import com.ghost.client.module.config.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class AutoCrystalModule extends Module {

    public final Setting<Double> placeRange = new Setting<>("Place Range", "Distance to place", 4.0, 1.0, 6.0);
    public final Setting<Double> breakRange = new Setting<>("Break Range", "Distance to break", 5.0, 1.0, 7.0);
    public final Setting<Double> minDamage  = new Setting<>("Min Damage", "Min damage to target", 6.0, 1.0, 20.0);
    public final Setting<Double> maxSelf    = new Setting<>("Max Self Dmg", "Max self damage", 8.0, 1.0, 20.0);
    public final Setting<Integer> delay     = new Setting<>("Delay (ticks)", "Ticks between actions", 0, 0, 10);
    public final Setting<Boolean> antiSuicide = new Setting<>("Anti Suicide", "Stop if fatal to self", true);

    private int tick = 0;

    public AutoCrystalModule() {
        super("AutoCrystal", "Places and breaks end crystals", Category.COMBAT);
        settings.add(placeRange); settings.add(breakRange);
        settings.add(minDamage);  settings.add(maxSelf);
        settings.add(delay);      settings.add(antiSuicide);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        if (tick++ < delay.getValue()) return;
        tick = 0;

        if (!mc.player.getMainHandStack().isOf(Items.END_CRYSTAL)) {
            for (int i = 0; i < 9; i++) {
                if (mc.player.getInventory().getStack(i).isOf(Items.END_CRYSTAL)) {
                    mc.player.getInventory().selectedSlot = i; return;
                }
            }
            return;
        }

        PlayerEntity target = findTarget();
        if (target == null) return;
        breakCrystal(); placeCrystal(target);
    }

    private void breakCrystal() {
        for (Entity e : mc.world.getEntities()) {
            if (e instanceof EndCrystalEntity && mc.player.distanceTo(e) <= breakRange.getValue()) {
                mc.interactionManager.attackEntity(mc.player, e);
                mc.player.swingHand(Hand.MAIN_HAND); return;
            }
        }
    }

    private void placeCrystal(PlayerEntity target) {
        Vec3d tp = target.getPos();
        BlockPos best = null; double bestScore = -1;
        for (int x = (int)tp.x - 3; x <= tp.x + 3; x++) {
            for (int z = (int)tp.z - 3; z <= tp.z + 3; z++) {
                BlockPos pos = new BlockPos(x, (int)tp.y - 1, z);
                if (!isValid(pos)) continue;
                if (mc.player.getPos().distanceTo(new Vec3d(x+.5, pos.getY()+1.5, z+.5)) > placeRange.getValue()) continue;
                double dmgT = dmg(target, pos.up()), dmgS = dmg(mc.player, pos.up());
                if (dmgT < minDamage.getValue() || dmgS > maxSelf.getValue()) continue;
                if (antiSuicide.getValue() && dmgS >= mc.player.getHealth()) continue;
                double score = dmgT - dmgS;
                if (score > bestScore) { bestScore = score; best = pos; }
            }
        }
        if (best == null) return;
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND,
            new BlockHitResult(new Vec3d(best.getX()+.5, best.getY()+1.0, best.getZ()+.5), Direction.UP, best, false));
    }

    private boolean isValid(BlockPos pos) {
        if (mc.world == null) return false;
        return !mc.world.getBlockState(pos).isAir()
            && mc.world.getBlockState(pos.up()).isAir()
            && mc.world.getBlockState(pos.up().up()).isAir()
            && mc.world.getEntitiesByClass(EndCrystalEntity.class, new Box(pos.up()), e->true).isEmpty();
    }

    private double dmg(Entity e, BlockPos origin) {
        double dist = e.getPos().add(0,e.getHeight()/2,0)
            .distanceTo(new Vec3d(origin.getX()+.5, origin.getY()+.5, origin.getZ()+.5));
        if (dist > 6) return 0;
        return (1 - dist/6.0) * 12 * 0.5;
    }

    private PlayerEntity findTarget() {
        double r = Math.max(placeRange.getValue(), breakRange.getValue());
        PlayerEntity best = null; double bd = Double.MAX_VALUE;
        for (PlayerEntity p : mc.world.getPlayers()) {
            if (p == mc.player) continue;
            double d = mc.player.distanceTo(p);
            if (d < r && d < bd) { bd = d; best = p; }
        }
        return best;
    }
    }
