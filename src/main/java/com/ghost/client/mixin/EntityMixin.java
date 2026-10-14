package com.ghost.client.mixin;

import com.ghost.client.GhostClient;
import com.ghost.client.module.impl.HitboxModule;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method="getBoundingBox",at=@At("RETURN"),cancellable=true)
    private void ghost_expand(CallbackInfoReturnable<Box> cir) {
        HitboxModule h=(HitboxModule)GhostClient.getModuleManager().getByName("Hitbox");
        if(h==null||!h.isEnabled()) return;
        if((Entity)(Object)this==GhostClient.mc.player) return;
        double e=h.expandSize.getValue();
        cir.setReturnValue(cir.getReturnValue().expand(e,0,e));
    }
}
