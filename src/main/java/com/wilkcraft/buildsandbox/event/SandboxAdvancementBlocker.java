package com.wilkcraft.buildsandbox.event;

import com.wilkcraft.buildsandbox.world.BuildSandboxDimension;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber
public class SandboxAdvancementBlocker {

  @SubscribeEvent
  public static void onTick(PlayerTickEvent.Post event) {

    if (!(event.getEntity() instanceof ServerPlayer player))
      return;

    boolean inSandbox = player.level().dimension().location()
        .equals(BuildSandboxDimension.SANDBOX_ID);

    // SOLO bloqueo lógico (NO romper sistema global)
    if (inSandbox) {
      player.getAdvancements().setSelectedTab(null);
    }
  }
}