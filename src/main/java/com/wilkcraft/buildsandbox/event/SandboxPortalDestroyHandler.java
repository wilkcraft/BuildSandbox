package com.wilkcraft.buildsandbox.event;

import com.wilkcraft.buildsandbox.world.BuildSandboxDimension;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber
public class SandboxPortalDestroyHandler {

  @SubscribeEvent
  public static void onPortalCreate(BlockEvent.EntityPlaceEvent event) {

    if (!(event.getLevel() instanceof Level level))
      return;

    if (level.dimension().location()
        .equals(BuildSandboxDimension.SANDBOX_ID)
        &&
        event.getPlacedBlock().getBlock() instanceof NetherPortalBlock) {

      event.setCanceled(true);
    }
  }
}