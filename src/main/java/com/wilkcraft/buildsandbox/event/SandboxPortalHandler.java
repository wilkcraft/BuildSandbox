package com.wilkcraft.buildsandbox.event;

import com.wilkcraft.buildsandbox.world.BuildSandboxDimension;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber
public class SandboxPortalHandler {

  @SubscribeEvent
  public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {

    if (!(event.getLevel() instanceof Level level))
      return;

    if (level.dimension().location()
        .equals(BuildSandboxDimension.SANDBOX_ID)
        &&
        event.getPlacedBlock().getBlock() instanceof BaseFireBlock) {

      event.setCanceled(true);
    }
  }
}