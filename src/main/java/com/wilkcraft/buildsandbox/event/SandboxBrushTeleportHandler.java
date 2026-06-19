package com.wilkcraft.buildsandbox.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.wilkcraft.buildsandbox.command.SandboxCommand;
import com.wilkcraft.buildsandbox.manager.PlayerData;
import com.wilkcraft.buildsandbox.manager.SandboxManager;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber
public class SandboxBrushTeleportHandler {

  private static final Map<UUID, Integer> COUNTDOWNS = new HashMap<>();
  private static final Set<UUID> ACTIVE = new HashSet<>();

  private static void hotbar(
      ServerPlayer player,
      Component message) {

    player.displayClientMessage(
        message,
        true);
  }

  private static boolean isActivationItem(ServerPlayer player) {

    PlayerData data = SandboxManager.getData(player.getUUID());

    ItemStack configured = data.getActivationItem();

    if (configured == null || configured.isEmpty()) {

      return player.getMainHandItem().getItem() == Items.BRUSH;
    }

    return ItemStack.isSameItem(
        player.getMainHandItem(),
        configured);
  }

  @SubscribeEvent
  public static void onRightClickItem(
      PlayerInteractEvent.RightClickItem event) {

    if (!(event.getEntity() instanceof ServerPlayer player))
      return;

    if (event.getHand() != InteractionHand.MAIN_HAND)
      return;

    if (!player.isShiftKeyDown())
      return;

    if (!isActivationItem(player))
      return;

    UUID uuid = player.getUUID();

    if (!ACTIVE.contains(uuid)) {

      ACTIVE.add(uuid);
      COUNTDOWNS.put(uuid, 60);

      hotbar(
          player,
          Component.literal("⌛ Teleporting in ")
              .withStyle(ChatFormatting.YELLOW)
              .append(
                  Component.literal("3")
                      .withStyle(ChatFormatting.GOLD))
              .append(
                  Component.literal("...")
                      .withStyle(ChatFormatting.YELLOW)));
    }
  }

  @SubscribeEvent
  public static void onRightClickBlock(
      PlayerInteractEvent.RightClickBlock event) {

    if (!(event.getEntity() instanceof ServerPlayer player))
      return;

    if (event.getHand() != InteractionHand.MAIN_HAND)
      return;

    if (!player.isShiftKeyDown())
      return;

    if (!isActivationItem(player))
      return;

    var block = event.getLevel()
        .getBlockState(event.getPos())
        .getBlock();

    if (block == Blocks.SUSPICIOUS_SAND
        || block == Blocks.SUSPICIOUS_GRAVEL)
      return;

    UUID uuid = player.getUUID();

    if (!ACTIVE.contains(uuid)) {

      ACTIVE.add(uuid);
      COUNTDOWNS.put(uuid, 60);

      hotbar(
          player,
          Component.literal("⌛ Teleporting in ")
              .withStyle(ChatFormatting.YELLOW)
              .append(
                  Component.literal("3")
                      .withStyle(ChatFormatting.GOLD))
              .append(
                  Component.literal("...")
                      .withStyle(ChatFormatting.YELLOW)));
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
        || !isActivationItem(player)) {

      ACTIVE.remove(uuid);
      COUNTDOWNS.remove(uuid);

      hotbar(
          player,
          Component.literal("✖ Teleport cancelled")
              .withStyle(ChatFormatting.RED));

      return;
    }

    int ticks = COUNTDOWNS.get(uuid) - 1;

    if (ticks == 40) {

      hotbar(
          player,
          Component.literal("⌛ Teleporting in ")
              .withStyle(ChatFormatting.YELLOW)
              .append(
                  Component.literal("2")
                      .withStyle(ChatFormatting.GOLD))
              .append(
                  Component.literal("...")
                      .withStyle(ChatFormatting.YELLOW)));
    }

    if (ticks == 20) {

      hotbar(
          player,
          Component.literal("⌛ Teleporting in ")
              .withStyle(ChatFormatting.YELLOW)
              .append(
                  Component.literal("1")
                      .withStyle(ChatFormatting.GOLD))
              .append(
                  Component.literal("...")
                      .withStyle(ChatFormatting.YELLOW)));
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