package com.wilkcraft.buildsandbox.manager;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InventoryManager {

  public static void saveInventory(ServerPlayer player, List<ItemStack> saved) {

    player.getInventory().clearContent();

    for (int i = 0; i < saved.size(); i++) {
      player.getInventory().setItem(i, saved.get(i));
    }

    player.inventoryMenu.broadcastChanges();
  }

  public static void clearInventory(ServerPlayer player) {

    player.getInventory().clearContent();
    player.inventoryMenu.broadcastChanges();
  }

  public static List<ItemStack> copyInventory(ServerPlayer player) {

    List<ItemStack> inventory = new ArrayList<>();

    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
      inventory.add(
          player.getInventory()
              .getItem(i)
              .copy());
    }

    return inventory;
  }

  public static void loadInventory(
      ServerPlayer player,
      List<ItemStack> inventory) {

    player.getInventory().clearContent();

    if (inventory != null) {

      for (int i = 0; i < inventory.size(); i++) {

        player.getInventory()
            .setItem(i, inventory.get(i));
      }
    }

    player.inventoryMenu.broadcastChanges();
  }
}
