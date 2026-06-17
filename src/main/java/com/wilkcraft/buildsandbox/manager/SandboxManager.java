package com.wilkcraft.buildsandbox.manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SandboxManager {

  private static final Map<UUID, PlayerData> PLAYER_DATA = new HashMap<>();

  private static final Set<UUID> ALLOWED_TRAVEL = new HashSet<>();

  public static PlayerData getData(UUID uuid) {

    return PLAYER_DATA.computeIfAbsent(
        uuid,
        id -> new PlayerData());
  }

  public static boolean isInSandbox(UUID uuid) {

    PlayerData data = PLAYER_DATA.get(uuid);

    return data != null
        && data.getSurvivalPosition() != null;
  }

  public static void remove(UUID uuid) {
    PLAYER_DATA.remove(uuid);
  }

  public static void allowTravel(UUID uuid) {
    ALLOWED_TRAVEL.add(uuid);
  }

  public static boolean consumeTravel(UUID uuid) {
    return ALLOWED_TRAVEL.remove(uuid);
  }
}