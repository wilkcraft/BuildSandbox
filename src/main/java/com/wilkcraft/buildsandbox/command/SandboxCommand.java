package com.wilkcraft.buildsandbox.command;

import com.mojang.brigadier.CommandDispatcher;
import com.wilkcraft.buildsandbox.manager.InventoryManager;
import com.wilkcraft.buildsandbox.manager.PlayerData;
import com.wilkcraft.buildsandbox.manager.SandboxManager;
import com.wilkcraft.buildsandbox.world.BuildSandboxDimension;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class SandboxCommand {

        public static void register(
                        CommandDispatcher<CommandSourceStack> dispatcher) {

                dispatcher.register(
                                Commands.literal("buildsandbox")
                                                .executes(context -> {

                                                        ServerPlayer player = context.getSource()
                                                                        .getPlayerOrException();

                                                        var uuid = player.getUUID();

                                                        PlayerData data = SandboxManager.getData(uuid);

                                                        boolean entering = player.gameMode
                                                                        .getGameModeForPlayer() != GameType.CREATIVE;

                                                        if (entering) {

                                                                data.setSurvivalDimension(
                                                                                player.level().dimension());

                                                                data.setSurvivalPosition(
                                                                                player.blockPosition());

                                                                data.setSurvivalInventory(
                                                                                InventoryManager.copyInventory(player));

                                                                InventoryManager.loadInventory(
                                                                                player,
                                                                                data.getSandboxInventory());

                                                                player.setGameMode(GameType.CREATIVE);

                                                                ServerLevel sandboxLevel = player.server.getLevel(
                                                                                ResourceKey.create(
                                                                                                net.minecraft.core.registries.Registries.DIMENSION,
                                                                                                BuildSandboxDimension.SANDBOX_ID));

                                                                if (sandboxLevel != null) {

                                                                        BlockPos pos = data.getSandboxPosition();

                                                                        if (pos == null) {

                                                                                SandboxManager.allowTravel(
                                                                                                player.getUUID());

                                                                                player.teleportTo(
                                                                                                sandboxLevel,
                                                                                                0.5,
                                                                                                64,
                                                                                                0.5,
                                                                                                0,
                                                                                                0);

                                                                        } else {

                                                                                SandboxManager.allowTravel(
                                                                                                player.getUUID());

                                                                                player.teleportTo(
                                                                                                sandboxLevel,
                                                                                                pos.getX() + 0.5,
                                                                                                pos.getY(),
                                                                                                pos.getZ() + 0.5,
                                                                                                player.getYRot(),
                                                                                                player.getXRot());
                                                                        }
                                                                }

                                                                player.sendSystemMessage(
                                                                                Component.literal(
                                                                                                "You entered the Build Sandbox"));

                                                        } else {

                                                                data.setSandboxPosition(
                                                                                player.blockPosition());

                                                                data.setSandboxInventory(
                                                                                InventoryManager.copyInventory(player));

                                                                InventoryManager.loadInventory(
                                                                                player,
                                                                                data.getSurvivalInventory());

                                                                player.setGameMode(GameType.SURVIVAL);

                                                                ServerLevel oldLevel = player.server.getLevel(
                                                                                data.getSurvivalDimension());

                                                                if (oldLevel != null) {

                                                                        BlockPos pos = data.getSurvivalPosition();

                                                                        SandboxManager.allowTravel(player.getUUID());

                                                                        player.teleportTo(
                                                                                        oldLevel,
                                                                                        pos.getX() + 0.5,
                                                                                        pos.getY(),
                                                                                        pos.getZ() + 0.5,
                                                                                        player.getYRot(),
                                                                                        player.getXRot());
                                                                }

                                                                player.sendSystemMessage(
                                                                                Component.literal(
                                                                                                "You left the Build Sandbox"));
                                                        }

                                                        return 1;
                                                }));
        }
}