package com.wilkcraft.buildsandbox.manager;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PlayerData {

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
}