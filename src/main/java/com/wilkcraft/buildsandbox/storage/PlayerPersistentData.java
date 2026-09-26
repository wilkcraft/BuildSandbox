package com.wilkcraft.buildsandbox.storage;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class PlayerPersistentData {

  private static final String ROOT = "BuildSandbox";

  private static CompoundTag root(ServerPlayer player) {

    CompoundTag persistent = player.getPersistentData();

    if (!persistent.contains(ROOT)) {
      persistent.put(ROOT, new CompoundTag());
    }

    return persistent.getCompound(ROOT);
  }

  public static void savePosition(
      ServerPlayer player,
      String key,
      BlockPos pos) {

    CompoundTag tag = root(player);

    CompoundTag posTag = new CompoundTag();

    posTag.putInt("x", pos.getX());
    posTag.putInt("y", pos.getY());
    posTag.putInt("z", pos.getZ());

    tag.put(key, posTag);
  }

  public static BlockPos loadPosition(
      ServerPlayer player,
      String key) {

    CompoundTag tag = root(player);

    if (!tag.contains(key))
      return null;

    CompoundTag posTag = tag.getCompound(key);

    return new BlockPos(
        posTag.getInt("x"),
        posTag.getInt("y"),
        posTag.getInt("z"));
  }

  public static void saveDimension(
      ServerPlayer player,
      ResourceLocation dimension) {

    root(player).putString(
        "survivalDimension",
        dimension.toString());
  }

  public static String loadDimension(
      ServerPlayer player) {

    CompoundTag tag = root(player);

    if (!tag.contains("survivalDimension"))
      return null;

    return tag.getString(
        "survivalDimension");
  }

  public static void saveInventory(
      ServerPlayer player,
      String key,
      List<ItemStack> inventory) {

    ListTag list = new ListTag();

    for (int i = 0; i < inventory.size(); i++) {

      ItemStack stack = inventory.get(i);

      if (stack.isEmpty()) {
        continue;
      }

      CompoundTag slot = new CompoundTag();

      slot.putInt("slot", i);

      stack.save(
          player.registryAccess(),
          slot);

      list.add(slot);
    }

    root(player).put(
        key,
        list);
  }

  public static List<ItemStack> loadInventory(
      ServerPlayer player,
      String key) {

    CompoundTag root = root(player);

    if (!root.contains(key))
      return null;

    ListTag list = root.getList(
        key,
        CompoundTag.TAG_COMPOUND);

    List<ItemStack> inventory = new ArrayList<>();

    for (int i = 0; i < 41; i++) {
      inventory.add(ItemStack.EMPTY);
    }

    for (int i = 0; i < list.size(); i++) {

      CompoundTag slotTag = list.getCompound(i);

      int slot = slotTag.getInt("slot");

      ItemStack stack = ItemStack.parseOptional(
          player.registryAccess(),
          slotTag);

      inventory.set(
          slot,
          stack);
    }

    return inventory;
  }

  public static void clear(ServerPlayer player) {

    player.getPersistentData()
        .remove(ROOT);
  }

  public static void saveCurios(
      ServerPlayer player,
      String key,
      ListTag curiosTag) {

    if (curiosTag == null)
      return;

    root(player).put(key, curiosTag);
  }

  public static ListTag loadCurios(
      ServerPlayer player,
      String key) {

    CompoundTag tag = root(player);

    if (!tag.contains(key))
      return null;

    return tag.getList(key, CompoundTag.TAG_COMPOUND);
  }
}