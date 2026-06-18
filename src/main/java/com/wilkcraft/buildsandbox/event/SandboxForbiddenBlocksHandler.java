package com.wilkcraft.buildsandbox.event;

import com.wilkcraft.buildsandbox.world.BuildSandboxDimension;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber
public class SandboxForbiddenBlocksHandler {

  @SubscribeEvent
  public static void onPlace(BlockEvent.EntityPlaceEvent event) {

    if (!(event.getLevel() instanceof Level level))
      return;

    if (!level.dimension()
        .location()
        .equals(BuildSandboxDimension.SANDBOX_ID))
      return;

    Block block = event.getPlacedBlock()
        .getBlock();

    if (isForbidden(block)) {
      event.setCanceled(true);
    }
  }

  @SubscribeEvent
  public static void onMultiPlace(
      BlockEvent.EntityMultiPlaceEvent event) {

    if (!(event.getLevel() instanceof Level level))
      return;

    if (!level.dimension()
        .location()
        .equals(BuildSandboxDimension.SANDBOX_ID))
      return;

    Block block = event.getPlacedBlock()
        .getBlock();

    if (isForbidden(block)) {
      event.setCanceled(true);
    }
  }

  private static boolean isForbidden(Block block) {

    return block == Blocks.NETHER_PORTAL
        || block == Blocks.END_PORTAL
        || block == Blocks.END_PORTAL_FRAME;
  }
}