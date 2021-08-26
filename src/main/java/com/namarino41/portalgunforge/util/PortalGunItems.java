package com.namarino41.portalgunforge.util;

import com.namarino41.portalgunforge.PortalGunForge;
import com.namarino41.portalgunforge.entities.Portal;
import com.namarino41.portalgunforge.items.PortalGunItem;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class PortalGunItems {
    public static final PortalGunItem PORTAL_GUN_ITEM = new PortalGunItem();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(
            ForgeRegistries.ITEMS, PortalGunForge.MOD_ID);

    static {
        ITEMS.register("portalgun", () -> PORTAL_GUN_ITEM);

    }
}
