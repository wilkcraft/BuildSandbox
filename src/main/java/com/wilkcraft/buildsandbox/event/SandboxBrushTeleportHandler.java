package com.wilkcraft.buildsandbox.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.wilkcraft.buildsandbox.command.SandboxCommand;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber
public class SandboxBrushTeleportHandler {

  private static final Map<UUID, Integer> COUNTDOWNS = new HashMap<>();
  private static final Set<UUID> ACTIVE = new HashSet<>();

  @SubscribeEvent
  public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {

    if (!(event.getEntity() instanceof ServerPlayer player))
      return;

    if (event.getHand() != InteractionHand.MAIN_HAND)
      return;

    if (!player.isShiftKeyDown())
      return;

    if (player.getMainHandItem().getItem() != Items.BRUSH)
      return;

    UUID uuid = player.getUUID();

    if (!ACTIVE.contains(uuid)) {

      ACTIVE.add(uuid);
      COUNTDOWNS.put(uuid, 60);

      player.displayClientMessage(
          Component.literal("Teleporting in 3..."),
          true);
    }
  }

  @SubscribeEvent
  public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {

    if (!(event.getEntity() instanceof ServerPlayer player))
      return;

    if (event.getHand() != InteractionHand.MAIN_HAND)
      return;

    if (!player.isShiftKeyDown())
      return;

    if (player.getMainHandItem().getItem() != Items.BRUSH)
      return;

    var block = event.getLevel().getBlockState(event.getPos()).getBlock();

    if (block == Blocks.SUSPICIOUS_SAND
        || block == Blocks.SUSPICIOUS_GRAVEL)
      return;

    UUID uuid = player.getUUID();

    if (!ACTIVE.contains(uuid)) {

      ACTIVE.add(uuid);
      COUNTDOWNS.put(uuid, 60);

      player.displayClientMessage(
          Component.literal("Teleporting in 3..."),
          true);
    }
  }

  @SubscribeEvent
  public static void onTick(PlayerTickEvent.Post event) {

    if (!(event.getEntity() instanceof ServerPlayer player))
      return;

    UUID uuid = player.getUUID();

    if (!ACTIVE.contains(uuid))
      return;

    if (!player.isShiftKeyDown()
        || player.getMainHandItem().getItem() != Items.BRUSH) {

      ACTIVE.remove(uuid);
      COUNTDOWNS.remove(uuid);

      player.displayClientMessage(
          Component.literal("Teleport cancelled"),
          true);

      return;
    }

    int ticks = COUNTDOWNS.get(uuid) - 1;

    if (ticks == 40) {
      player.displayClientMessage(
          Component.literal("Teleporting in 2..."),
          true);
    }

    if (ticks == 20) {
      player.displayClientMessage(
          Component.literal("Teleporting in 1..."),
          true);
    }

    if (ticks <= 0) {

      ACTIVE.remove(uuid);
      COUNTDOWNS.remove(uuid);

      SandboxCommand.toggleSandbox(player);
      return;
    }

    COUNTDOWNS.put(uuid, ticks);
  }
}