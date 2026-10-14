package com.ghost.client.module.impl;

import com.ghost.client.module.Module;
import com.ghost.client.module.config.Setting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.item.ItemStack;

public class AutoTotemModule extends Module {

    public final Setting<Double> healthThreshold = new Setting<>(
        "Health Threshold", "Equip totem when HP below this", 16.0, 1.0, 20.0);

    public AutoTotemModule() {
        super("AutoTotem", "Keeps totem in offhand automatically", Category.PLAYER);
        settings.add(healthThreshold);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        ClientPlayerEntity p = mc.player;
        if (p.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) return;
        if (p.getHealth() > healthThreshold.getValue()) return;

        int totemSlot = -1;
        for (int i = 0; i < 36; i++) {
            ItemStack stack = p.getInventory().getStack(i);
            if (stack.isOf(Items.TOTEM_OF_UNDYING)) { totemSlot = i; break; }
        }
        if (totemSlot == -1) return;

        int containerSlot = totemSlot < 9 ? totemSlot + 36 : totemSlot;
        mc.interactionManager.clickSlot(p.currentScreenHandler.syncId, containerSlot, 0, SlotActionType.PICKUP, p);
        mc.interactionManager.clickSlot(p.currentScreenHandler.syncId, 45, 0, SlotActionType.PICKUP, p);
        mc.interactionManager.clickSlot(p.currentScreenHandler.syncId, containerSlot, 0, SlotActionType.PICKUP, p);
    }
}
