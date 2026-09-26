package com.wilkcraft.buildsandbox.compat;

import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

public class CuriosCompat {

    public static ListTag saveCurios(ServerPlayer player) {
        return CuriosApi.getCuriosInventory(player)
                .map(handler -> handler.saveInventory(false))
                .orElse(null);
    }

    public static void loadCurios(ServerPlayer player, ListTag tag) {
        clearCurios(player);

        if (tag == null)
            return;

        CuriosApi.getCuriosInventory(player)
                .ifPresent(handler -> handler.loadInventory(tag));
    }

    public static void clearCurios(ServerPlayer player) {
        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            for (ICurioStacksHandler stacksHandler : handler.getCurios().values()) {
                IDynamicStackHandler stacks = stacksHandler.getStacks();
                for (int i = 0; i < stacks.getSlots(); i++) {
                    stacks.setStackInSlot(i, ItemStack.EMPTY);
                }
            }
        });
    }
}