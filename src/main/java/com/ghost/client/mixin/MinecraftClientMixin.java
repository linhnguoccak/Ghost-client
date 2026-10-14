package com.ghost.client.mixin;

import com.ghost.client.GhostClient;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method="stop",at=@At("HEAD"))
    private void ghost_stop(CallbackInfo ci) {
        GhostClient.getConfigManager().save();
    }
}
