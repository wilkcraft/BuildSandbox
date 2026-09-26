package com.wilkcraft.buildsandbox.event;

import com.wilkcraft.buildsandbox.compat.CuriosCompat;
import com.wilkcraft.buildsandbox.manager.InventoryManager;
import com.wilkcraft.buildsandbox.manager.SandboxManager;
import com.wilkcraft.buildsandbox.storage.PlayerPersistentData;
import com.wilkcraft.buildsandbox.world.BuildSandboxDimension;

import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber
public class SandboxPlayerLoginHandler {

  @SubscribeEvent
  public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {

    if (!(event.getEntity() instanceof ServerPlayer player))
      return;

    boolean inSandbox = player.level()
        .dimension()
        .location()
        .equals(BuildSandboxDimension.SANDBOX_ID);

    if (inSandbox) {

      InventoryManager.loadInventory(
          player,
          PlayerPersistentData.loadInventory(
              player,
              "sandboxInventory"));

    } else {

      InventoryManager.loadInventory(
          player,
          PlayerPersistentData.loadInventory(
              player,
              "survivalInventory"));
    }

    if (SandboxManager.isCuriosLoaded()) {

      String key = inSandbox ? "sandboxCurios" : "survivalCurios";

      ListTag tag = PlayerPersistentData.loadCurios(player, key);

      CuriosCompat.loadCurios(player, tag);
    }
  }
}