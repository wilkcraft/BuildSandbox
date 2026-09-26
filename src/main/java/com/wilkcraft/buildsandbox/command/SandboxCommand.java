package com.wilkcraft.buildsandbox.command;

import com.mojang.brigadier.CommandDispatcher;
import com.wilkcraft.buildsandbox.BuildSandbox;
import com.wilkcraft.buildsandbox.compat.CuriosCompat;
import com.wilkcraft.buildsandbox.manager.InventoryManager;
import com.wilkcraft.buildsandbox.manager.PlayerData;
import com.wilkcraft.buildsandbox.manager.SandboxManager;
import com.wilkcraft.buildsandbox.world.BuildSandboxDimension;
import com.wilkcraft.buildsandbox.storage.PlayerPersistentData;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class SandboxCommand {
        private static void hotbar(
                        ServerPlayer player,
                        Component message) {

                player.displayClientMessage(
                                message,
                                true);
        }

        public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

                dispatcher.register(
                                Commands.literal("buildsandbox")
                                                .executes(context -> execute(context.getSource())));

                dispatcher.register(
                                Commands.literal("bs")
                                                .executes(context -> execute(context.getSource())));
        }

        private static int execute(CommandSourceStack source) {

                try {

                        ServerPlayer player = source.getPlayerOrException();

                        toggleSandbox(player);

                        return 1;

                } catch (Exception e) {

                        BuildSandbox.LOGGER.error(
                                        "Error executing sandbox command",
                                        e);

                        return 0;
                }
        }

        public static void toggleSandbox(ServerPlayer player) {

                var uuid = player.getUUID();

                PlayerData data = SandboxManager.getData(uuid);

                boolean currentlyInSandbox = player.level()
                                .dimension()
                                .location()
                                .equals(BuildSandboxDimension.SANDBOX_ID);

                boolean entering = !currentlyInSandbox;

                if (entering) {
                        if (data.getSandboxPosition() == null) {
                                data.setSandboxPosition(
                                                PlayerPersistentData.loadPosition(
                                                                player,
                                                                "sandboxPos"));
                        }

                        if (data.getSandboxInventory() == null) {
                                data.setSandboxInventory(
                                                PlayerPersistentData.loadInventory(
                                                                player,
                                                                "sandboxInventory"));
                        }

                        data.setSurvivalDimension(player.level().dimension());
                        data.setSurvivalPosition(player.blockPosition());
                        data.setSurvivalInventory(
                                        InventoryManager.copyInventory(player));

                        PlayerPersistentData.saveDimension(
                                        player,
                                        player.level()
                                                        .dimension()
                                                        .location());

                        PlayerPersistentData.savePosition(
                                        player,
                                        "survivalPos",
                                        player.blockPosition());

                        PlayerPersistentData.saveInventory(
                                        player,
                                        "survivalInventory",
                                        InventoryManager.copyInventory(player));

                        if (SandboxManager.isCuriosLoaded()) {

                                if (data.getSandboxCurios() == null) {
                                        data.setSandboxCurios(
                                                        PlayerPersistentData.loadCurios(player, "sandboxCurios"));
                                }

                                ListTag currentCurios = CuriosCompat.saveCurios(player);

                                data.setSurvivalCurios(currentCurios);

                                PlayerPersistentData.saveCurios(player, "survivalCurios", currentCurios);

                                CuriosCompat.loadCurios(player, data.getSandboxCurios());
                        }

                        InventoryManager.loadInventory(
                                        player,
                                        data.getSandboxInventory());

                        player.saveWithoutId(player.getPersistentData());

                        player.setGameMode(GameType.CREATIVE);

                        ServerLevel sandboxLevel = player.server.getLevel(
                                        ResourceKey.create(
                                                        Registries.DIMENSION,
                                                        BuildSandboxDimension.SANDBOX_ID));

                        if (sandboxLevel != null) {

                                SandboxManager.setInSandbox(uuid, true);
                                SandboxManager.allowTravel(uuid);

                                BlockPos pos = data.getSandboxPosition();

                                if (pos == null) {

                                        player.teleportTo(
                                                        sandboxLevel,
                                                        0.5,
                                                        -52,
                                                        0.5,
                                                        0,
                                                        0);

                                } else {

                                        player.teleportTo(
                                                        sandboxLevel,
                                                        pos.getX() + 0.5,
                                                        pos.getY(),
                                                        pos.getZ() + 0.5,
                                                        player.getYRot(),
                                                        player.getXRot());
                                }
                        }

                        if (sandboxLevel == null) {
                                BuildSandbox.LOGGER.error(
                                                "Sandbox dimension not found: {}",
                                                BuildSandboxDimension.SANDBOX_ID);

                                player.sendSystemMessage(
                                                Component.literal("Sandbox dimension not found"));
                                return;
                        }

                        hotbar(
                                        player,
                                        Component.literal("Entered ")
                                                        .withStyle(ChatFormatting.GREEN)
                                                        .append(
                                                                        Component.literal("Build Sandbox")
                                                                                        .withStyle(ChatFormatting.AQUA)));

                } else {
                        if (data.getSurvivalDimension() == null) {

                                String dim = PlayerPersistentData.loadDimension(
                                                player);

                                if (dim != null) {

                                        data.setSurvivalDimension(
                                                        ResourceKey.create(
                                                                        Registries.DIMENSION,
                                                                        ResourceLocation.parse(dim)));
                                }
                        }

                        if (data.getSurvivalPosition() == null) {

                                data.setSurvivalPosition(
                                                PlayerPersistentData.loadPosition(
                                                                player,
                                                                "survivalPos"));
                        }

                        if (data.getSurvivalInventory() == null) {

                                data.setSurvivalInventory(
                                                PlayerPersistentData.loadInventory(
                                                                player,
                                                                "survivalInventory"));
                        }

                        if (data.getSurvivalDimension() == null
                                        || data.getSurvivalPosition() == null
                                        || data.getSurvivalInventory() == null) {

                                ServerLevel overworld = player.server.overworld();

                                InventoryManager.clearInventory(player);

                                player.setGameMode(GameType.SURVIVAL);

                                SandboxManager.allowTravel(uuid);

                                player.teleportTo(
                                                overworld,
                                                overworld.getSharedSpawnPos().getX() + 0.5,
                                                overworld.getSharedSpawnPos().getY(),
                                                overworld.getSharedSpawnPos().getZ() + 0.5,
                                                player.getYRot(),
                                                player.getXRot());

                                return;
                        }

                        data.setSandboxPosition(
                                        player.blockPosition());

                        data.setSandboxInventory(
                                        InventoryManager.copyInventory(player));

                        PlayerPersistentData.savePosition(
                                        player,
                                        "sandboxPos",
                                        player.blockPosition());

                        PlayerPersistentData.saveInventory(
                                        player,
                                        "sandboxInventory",
                                        InventoryManager.copyInventory(player));

                        if (SandboxManager.isCuriosLoaded()) {

                                if (data.getSurvivalCurios() == null) {
                                        data.setSurvivalCurios(
                                                        PlayerPersistentData.loadCurios(player, "survivalCurios"));
                                }

                                ListTag currentCurios = CuriosCompat.saveCurios(player);

                                data.setSandboxCurios(currentCurios);

                                PlayerPersistentData.saveCurios(player, "sandboxCurios", currentCurios);

                                CuriosCompat.loadCurios(player, data.getSurvivalCurios());
                        }

                        InventoryManager.loadInventory(
                                        player,
                                        data.getSurvivalInventory());

                        player.saveWithoutId(player.getPersistentData());

                        player.setGameMode(GameType.SURVIVAL);

                        ServerLevel oldLevel = player.server.getLevel(
                                        data.getSurvivalDimension());

                        if (oldLevel != null) {

                                SandboxManager.setInSandbox(uuid, false);
                                SandboxManager.allowTravel(uuid);

                                BlockPos pos = data.getSurvivalPosition();

                                player.teleportTo(
                                                oldLevel,
                                                pos.getX() + 0.5,
                                                pos.getY(),
                                                pos.getZ() + 0.5,
                                                player.getYRot(),
                                                player.getXRot());
                        }

                        hotbar(
                                        player,
                                        Component.literal("Returned to ")
                                                        .withStyle(ChatFormatting.YELLOW)
                                                        .append(
                                                                        Component.literal("Survival")
                                                                                        .withStyle(ChatFormatting.GOLD)));
                }
        }

}