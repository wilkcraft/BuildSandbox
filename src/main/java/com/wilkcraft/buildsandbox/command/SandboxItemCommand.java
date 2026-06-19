package com.wilkcraft.buildsandbox.command;

import com.mojang.brigadier.CommandDispatcher;
import com.wilkcraft.buildsandbox.manager.PlayerData;
import com.wilkcraft.buildsandbox.manager.SandboxManager;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;

public class SandboxItemCommand {
  private static void hotbar(
      ServerPlayer player,
      Component message) {

    player.displayClientMessage(
        message,
        true);
  }

  public static void register(
      CommandDispatcher<CommandSourceStack> dispatcher) {

    dispatcher.register(
        Commands.literal("bsitem")
            .executes(context -> setCurrentItem(
                context.getSource()))
            .then(
                Commands.literal("reset")
                    .executes(context -> reset(
                        context.getSource()))));
  }

  private static int setCurrentItem(
      CommandSourceStack source) {

    try {

      ServerPlayer player = source.getPlayerOrException();

      ItemStack stack = player.getMainHandItem();

      if (stack.isEmpty()) {

        hotbar(
            player,
            Component.literal(
                "Hold an item in your hand")
                .withStyle(ChatFormatting.RED));

        return 0;
      }

      if (!isAllowedActivationItem(stack)) {

        hotbar(
            player,
            Component.literal(
                "That item cannot be used")
                .withStyle(ChatFormatting.RED));

        return 0;
      }

      PlayerData data = SandboxManager.getData(
          player.getUUID());

      data.setActivationItem(
          stack.copyWithCount(1));

      hotbar(
          player,
          Component.literal(
              "Activation item set to ")
              .withStyle(ChatFormatting.GREEN)
              .append(
                  stack.getHoverName()
                      .copy()
                      .withStyle(ChatFormatting.GOLD)));

      return 1;

    } catch (Exception e) {

      return 0;
    }
  }

  private static boolean isAllowedActivationItem(
      ItemStack stack) {

    Item item = stack.getItem();

    if (item == Items.SHIELD)
      return false;

    if (item == Items.BOW)
      return false;

    if (item == Items.CROSSBOW)
      return false;

    if (item == Items.TRIDENT)
      return false;

    if (item instanceof SpawnEggItem)
      return false;

    if (item instanceof BlockItem)
      return false;

    return true;
  }

  private static int reset(
      CommandSourceStack source) {

    try {

      ServerPlayer player = source.getPlayerOrException();

      PlayerData data = SandboxManager.getData(
          player.getUUID());

      data.setActivationItem(
          ItemStack.EMPTY);

      hotbar(
          player,
          Component.literal(
              "Activation item reset to ")
              .withStyle(ChatFormatting.YELLOW)
              .append(
                  Items.BRUSH.getDefaultInstance()
                      .getHoverName()
                      .copy()
                      .withStyle(ChatFormatting.GOLD)));

      return 1;

    } catch (Exception e) {

      return 0;
    }
  }
}