package com.wilkcraft.buildsandbox.event;

import com.wilkcraft.buildsandbox.manager.SandboxManager;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber
public class PlayerDataLifecycleHandler {

  @SubscribeEvent
  public static void onClone(PlayerEvent.Clone event) {
    if (event.getEntity() instanceof ServerPlayer player
        && event.getOriginal() instanceof ServerPlayer original) {
      SandboxManager.copyPersistentData(original, player);
    }
  }

  @SubscribeEvent
  public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
    SandboxManager.remove(event.getEntity().getUUID());
  }
}
