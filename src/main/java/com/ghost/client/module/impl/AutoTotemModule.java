package com.ghost.client.module.impl;

import com.ghost.client.module.Module;
import com.ghost.client.module.config.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class AutoTotemModule extends Module {

    private final Setting<Double> healthThreshold = new Setting<>("Health", 10.0, 1.0, 20.0);

    public AutoTotemModule() {
        super("AutoTotem", "Keeps totem in offhand", "Player");
        addSetting(healthThreshold);
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.interactionManager == null) return;
        if (mc.player.getHealth() > healthThreshold.getValue().floatValue()) return;

        // check offhand already has totem
        if (mc.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) return;

        // find totem in hotbar/inventory
        PlayerInventory inv = mc.player.getInventory();
        for (int i = 0; i < 36; i++) {
            if (inv.getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
                int containerSlot = i < 9 ? i + 36 : i;
                mc.interactionManager.clickSlot(
                    mc.player.currentScreenHandler.syncId,
                    containerSlot, 0, SlotActionType.PICKUP, mc.player);
                mc.interactionManager.clickSlot(
                    mc.player.currentScreenHandler.syncId,
                    45, 0, SlotActionType.PICKUP, mc.player);
                mc.interactionManager.clickSlot(
                    mc.player.currentScreenHandler.syncId,
                    containerSlot, 0, SlotActionType.PICKUP, mc.player);
                break;
            }
        }
    }
}
