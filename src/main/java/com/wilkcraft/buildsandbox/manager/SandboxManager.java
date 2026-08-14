package com.wilkcraft.buildsandbox.manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;

public class SandboxManager {

  private static final String PLAYER_DATA_KEY = "BuildSandbox";
  private static final String IN_SANDBOX_KEY = "InSandbox";

  private static final Map<UUID, PlayerData> PLAYER_DATA = new HashMap<>();
  private static final Set<UUID> IN_SANDBOX = new HashSet<>();
  private static final Set<UUID> ALLOWED_TRAVEL = new HashSet<>();

  public static PlayerData getData(ServerPlayer player) {
    UUID uuid = player.getUUID();
    return PLAYER_DATA.computeIfAbsent(uuid, id -> load(player));
  }

  public static boolean isInSandbox(UUID uuid) {
    return IN_SANDBOX.contains(uuid);
  }

  public static void setInSandbox(ServerPlayer player, boolean value) {
    UUID uuid = player.getUUID();
    if (value) {
      IN_SANDBOX.add(uuid);
    } else {
      IN_SANDBOX.remove(uuid);
    }
    save(player);
  }

  public static void save(ServerPlayer player) {
    PlayerData data = getData(player);
    CompoundTag tag = data.save(player.registryAccess());
    tag.putBoolean(IN_SANDBOX_KEY, IN_SANDBOX.contains(player.getUUID()));
    player.getPersistentData().put(PLAYER_DATA_KEY, tag);
  }

  public static void remove(UUID uuid) {
    PLAYER_DATA.remove(uuid);
    IN_SANDBOX.remove(uuid);
    ALLOWED_TRAVEL.remove(uuid);
  }

  public static void copyPersistentData(ServerPlayer original, ServerPlayer player) {
    CompoundTag originalData = original.getPersistentData();
    if (originalData.contains(PLAYER_DATA_KEY, Tag.TAG_COMPOUND)) {
      player.getPersistentData().put(
          PLAYER_DATA_KEY,
          originalData.getCompound(PLAYER_DATA_KEY).copy());
    }
  }

  public static void allowTravel(UUID uuid) {
    ALLOWED_TRAVEL.add(uuid);
  }

  public static boolean consumeTravel(UUID uuid) {
    return ALLOWED_TRAVEL.remove(uuid);
  }

  private static PlayerData load(ServerPlayer player) {
    CompoundTag persistentData = player.getPersistentData();
    if (!persistentData.contains(PLAYER_DATA_KEY, Tag.TAG_COMPOUND)) {
      return new PlayerData();
    }

    CompoundTag tag = persistentData.getCompound(PLAYER_DATA_KEY);
    if (tag.getBoolean(IN_SANDBOX_KEY)) {
      IN_SANDBOX.add(player.getUUID());
    }
    return PlayerData.load(tag, player.registryAccess());
  }
}
