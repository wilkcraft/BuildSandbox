package com.wilkcraft.buildsandbox.event;

import com.wilkcraft.buildsandbox.world.BuildSandboxDimension;

import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber
public class SandboxSpawnHandler {

  @SubscribeEvent
  public static void onEntitySpawn(
      EntityJoinLevelEvent event) {

    if (!(event.getEntity() instanceof Mob))
      return;

    if (event.getLevel()
        .dimension()
        .location()
        .equals(BuildSandboxDimension.SANDBOX_ID)) {

      event.setCanceled(true);
    }
  }
}