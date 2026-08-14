package com.wilkcraft.buildsandbox.manager;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PlayerData {

  private static final String SURVIVAL_DIMENSION = "SurvivalDimension";
  private static final String SURVIVAL_POSITION = "SurvivalPosition";
  private static final String SURVIVAL_INVENTORY = "SurvivalInventory";
  private static final String SANDBOX_POSITION = "SandboxPosition";
  private static final String SANDBOX_INVENTORY = "SandboxInventory";
  private static final String ACTIVATION_ITEM = "ActivationItem";

  private ResourceKey<Level> survivalDimension;
  private BlockPos survivalPosition;
  private List<ItemStack> survivalInventory;

  private BlockPos sandboxPosition;
  private List<ItemStack> sandboxInventory;

  private ItemStack activationItem = ItemStack.EMPTY;

  public ResourceKey<Level> getSurvivalDimension() {
    return survivalDimension;
  }

  public void setSurvivalDimension(ResourceKey<Level> survivalDimension) {
    this.survivalDimension = survivalDimension;
  }

  public BlockPos getSurvivalPosition() {
    return survivalPosition;
  }

  public void setSurvivalPosition(BlockPos survivalPosition) {
    this.survivalPosition = survivalPosition;
  }

  public List<ItemStack> getSurvivalInventory() {
    return survivalInventory;
  }

  public void setSurvivalInventory(List<ItemStack> survivalInventory) {
    this.survivalInventory = survivalInventory;
  }

  public BlockPos getSandboxPosition() {
    return sandboxPosition;
  }

  public void setSandboxPosition(BlockPos sandboxPosition) {
    this.sandboxPosition = sandboxPosition;
  }

  public List<ItemStack> getSandboxInventory() {
    return sandboxInventory;
  }

  public void setSandboxInventory(List<ItemStack> sandboxInventory) {
    this.sandboxInventory = sandboxInventory;
  }

  public ItemStack getActivationItem() {
    return activationItem;
  }

  public void setActivationItem(ItemStack activationItem) {
    this.activationItem = activationItem;
  }

  public CompoundTag save(HolderLookup.Provider registries) {
    CompoundTag tag = new CompoundTag();

    if (survivalDimension != null) {
      tag.putString(SURVIVAL_DIMENSION, survivalDimension.location().toString());
    }
    if (survivalPosition != null) {
      tag.put(SURVIVAL_POSITION, NbtUtils.writeBlockPos(survivalPosition));
    }
    if (survivalInventory != null) {
      tag.put(SURVIVAL_INVENTORY, saveInventory(survivalInventory, registries));
    }
    if (sandboxPosition != null) {
      tag.put(SANDBOX_POSITION, NbtUtils.writeBlockPos(sandboxPosition));
    }
    if (sandboxInventory != null) {
      tag.put(SANDBOX_INVENTORY, saveInventory(sandboxInventory, registries));
    }
    if (activationItem != null && !activationItem.isEmpty()) {
      tag.put(ACTIVATION_ITEM, activationItem.saveOptional(registries));
    }

    return tag;
  }

  public static PlayerData load(CompoundTag tag, HolderLookup.Provider registries) {
    PlayerData data = new PlayerData();

    ResourceLocation dimension = ResourceLocation.tryParse(tag.getString(SURVIVAL_DIMENSION));
    if (dimension != null) {
      data.survivalDimension = ResourceKey.create(Registries.DIMENSION, dimension);
    }

    data.survivalPosition = NbtUtils.readBlockPos(tag, SURVIVAL_POSITION).orElse(null);
    data.sandboxPosition = NbtUtils.readBlockPos(tag, SANDBOX_POSITION).orElse(null);

    if (tag.contains(SURVIVAL_INVENTORY, Tag.TAG_LIST)) {
      data.survivalInventory = loadInventory(
          tag.getList(SURVIVAL_INVENTORY, Tag.TAG_COMPOUND), registries);
    }
    if (tag.contains(SANDBOX_INVENTORY, Tag.TAG_LIST)) {
      data.sandboxInventory = loadInventory(
          tag.getList(SANDBOX_INVENTORY, Tag.TAG_COMPOUND), registries);
    }
    if (tag.contains(ACTIVATION_ITEM, Tag.TAG_COMPOUND)) {
      data.activationItem = ItemStack.parseOptional(registries, tag.getCompound(ACTIVATION_ITEM));
    }

    return data;
  }

  private static ListTag saveInventory(
      List<ItemStack> inventory,
      HolderLookup.Provider registries) {
    ListTag tag = new ListTag();
    for (ItemStack stack : inventory) {
      tag.add(stack.saveOptional(registries));
    }
    return tag;
  }

  private static List<ItemStack> loadInventory(
      ListTag tag,
      HolderLookup.Provider registries) {
    List<ItemStack> inventory = new ArrayList<>(tag.size());
    for (int i = 0; i < tag.size(); i++) {
      inventory.add(ItemStack.parseOptional(registries, tag.getCompound(i)));
    }
    return inventory;
  }
}
