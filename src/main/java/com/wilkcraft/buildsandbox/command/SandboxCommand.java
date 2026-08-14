package com.wilkcraft.buildsandbox.command;

import com.mojang.brigadier.CommandDispatcher;
import com.wilkcraft.buildsandbox.manager.InventoryManager;
import com.wilkcraft.buildsandbox.manager.PlayerData;
import com.wilkcraft.buildsandbox.manager.SandboxManager;
import com.wilkcraft.buildsandbox.world.BuildSandboxDimension;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
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

                        return 0;
                }
        }

        public static void toggleSandbox(ServerPlayer player) {

                var uuid = player.getUUID();

                PlayerData data = SandboxManager.getData(player);

                boolean physicallyInSandbox = player.level()
                                .dimension()
                                .location()
                                .equals(BuildSandboxDimension.SANDBOX_ID);
                boolean entering = !SandboxManager.isInSandbox(uuid) && !physicallyInSandbox;

                if (entering) {

                        ServerLevel sandboxLevel = player.server.getLevel(
                                        ResourceKey.create(
                                                        Registries.DIMENSION,
                                                        BuildSandboxDimension.SANDBOX_ID));

                        if (sandboxLevel == null) {
                                hotbar(
                                                player,
                                                Component.literal("Sandbox dimension is unavailable")
                                                                .withStyle(ChatFormatting.RED));
                                return;
                        }

                        data.setSurvivalDimension(player.level().dimension());
                        data.setSurvivalPosition(player.blockPosition());
                        data.setSurvivalInventory(
                                        InventoryManager.copyInventory(player));

                        InventoryManager.loadInventory(
                                        player,
                                        data.getSandboxInventory());

                        player.setGameMode(GameType.CREATIVE);

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

                        SandboxManager.setInSandbox(player, true);

                        hotbar(
                                        player,
                                        Component.literal("Entered ")
                                                        .withStyle(ChatFormatting.GREEN)
                                                        .append(
                                                                        Component.literal("Build Sandbox")
                                                                                        .withStyle(ChatFormatting.AQUA)));

                } else {

                        if (data.getSurvivalDimension() == null
                                        || data.getSurvivalPosition() == null
                                        || data.getSurvivalInventory() == null) {
                                recoverOrphanedPlayer(player);
                                return;
                        }

                        ServerLevel oldLevel = player.server.getLevel(
                                        data.getSurvivalDimension());

                        if (oldLevel == null) {
                                hotbar(
                                                player,
                                                Component.literal("Saved survival dimension is unavailable")
                                                                .withStyle(ChatFormatting.RED));
                                return;
                        }

                        data.setSandboxPosition(
                                        player.blockPosition());

                        data.setSandboxInventory(
                                        InventoryManager.copyInventory(player));

                        InventoryManager.loadInventory(
                                        player,
                                        data.getSurvivalInventory());

                        player.setGameMode(GameType.SURVIVAL);

                        SandboxManager.allowTravel(uuid);

                        BlockPos pos = data.getSurvivalPosition();

                        player.teleportTo(
                                        oldLevel,
                                        pos.getX() + 0.5,
                                        pos.getY(),
                                        pos.getZ() + 0.5,
                                        player.getYRot(),
                                        player.getXRot());

                        SandboxManager.setInSandbox(player, false);

                        hotbar(
                                        player,
                                        Component.literal("Returned to ")
                                                        .withStyle(ChatFormatting.YELLOW)
                                                        .append(
                                                                        Component.literal("Survival")
                                                                                        .withStyle(ChatFormatting.GOLD)));
                }
        }

        private static void recoverOrphanedPlayer(ServerPlayer player) {
                ServerLevel overworld = player.server.overworld();
                BlockPos pos = overworld.getSharedSpawnPos();

                // Older versions never wrote the survival snapshot to disk. The original
                // inventory cannot be reconstructed, so do not leak the creative inventory
                // out of the sandbox while allowing the player to leave safely.
                InventoryManager.clearInventory(player);
                player.setGameMode(GameType.SURVIVAL);
                SandboxManager.allowTravel(player.getUUID());
                player.teleportTo(
                                overworld,
                                pos.getX() + 0.5,
                                pos.getY(),
                                pos.getZ() + 0.5,
                                player.getYRot(),
                                player.getXRot());
                SandboxManager.setInSandbox(player, false);

                hotbar(
                                player,
                                Component.literal("Recovered from sandbox; no saved survival inventory existed")
                                                .withStyle(ChatFormatting.YELLOW));
        }
}
