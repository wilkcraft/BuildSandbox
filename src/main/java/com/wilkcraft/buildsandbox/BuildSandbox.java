package com.wilkcraft.buildsandbox;

import com.mojang.logging.LogUtils;
import com.wilkcraft.buildsandbox.command.SandboxCommand;
import com.wilkcraft.buildsandbox.command.SandboxItemCommand;

import org.slf4j.Logger;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(BuildSandbox.MODID)
public class BuildSandbox {

    public static final String MODID = "buildsandbox";
    public static final Logger LOGGER = LogUtils.getLogger();

    public BuildSandbox(IEventBus modEventBus, ModContainer modContainer) {

        LOGGER.info("Build Sandbox loaded successfully");

        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {

        SandboxCommand.register(
                event.getDispatcher());

        SandboxItemCommand.register(
                event.getDispatcher());
    }
}