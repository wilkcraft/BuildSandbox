package com.wilkcraft.buildsandbox.event;

import com.wilkcraft.buildsandbox.manager.SandboxManager;
import com.wilkcraft.buildsandbox.world.BuildSandboxDimension;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;

@EventBusSubscriber
public class SandboxTravelHandler {

  @SubscribeEvent
  public static void onTravel(
      EntityTravelToDimensionEvent event) {

    if (!(event.getEntity() instanceof ServerPlayer player))
      return;

    if (!player.level()
        .dimension()
        .location()
        .equals(BuildSandboxDimension.SANDBOX_ID))
      return;

    if (!SandboxManager.consumeTravel(
        player.getUUID())) {

      event.setCanceled(true);
    }
  }
}