package com.wilkcraft.buildsandbox.manager;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class PlayerData {

  // Supervivencia

  private ResourceKey<Level> survivalDimension;
  private BlockPos survivalPosition;
  private List<ItemStack> survivalInventory;

  // Sandbox

  private BlockPos sandboxPosition;
  private List<ItemStack> sandboxInventory;

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
}