package com.wilkcraft.buildsandbox.event;

import com.wilkcraft.buildsandbox.compat.CuriosCompat;
import com.wilkcraft.buildsandbox.manager.InventoryManager;
import com.wilkcraft.buildsandbox.manager.SandboxManager;
import com.wilkcraft.buildsandbox.storage.PlayerPersistentData;
import com.wilkcraft.buildsandbox.world.BuildSandboxDimension;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber
public class SandboxPlayerLogoutHandler {

  @SubscribeEvent
  public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {

    if (!(event.getEntity() instanceof ServerPlayer player))
      return;

    boolean inSandbox = player.level()
        .dimension()
        .location()
        .equals(BuildSandboxDimension.SANDBOX_ID);

    if (inSandbox) {

      PlayerPersistentData.savePosition(
          player,
          "sandboxPos",
          player.blockPosition());

      PlayerPersistentData.saveInventory(
          player,
          "sandboxInventory",
          InventoryManager.copyInventory(player));

    } else {

      PlayerPersistentData.saveDimension(
          player,
          player.level()
              .dimension()
              .location());

      PlayerPersistentData.savePosition(
          player,
          "survivalPos",
          player.blockPosition());

      PlayerPersistentData.saveInventory(
          player,
          "survivalInventory",
          InventoryManager.copyInventory(player));
    }

    if (SandboxManager.isCuriosLoaded()) {

      String key = inSandbox ? "sandboxCurios" : "survivalCurios";

      PlayerPersistentData.saveCurios(
          player,
          key,
          CuriosCompat.saveCurios(player));
    }
  }
}