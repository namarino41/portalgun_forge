package com.namarino41.portalgunforge;

import com.namarino41.portalgunforge.util.PortalGunItems;
import com.namarino41.portalgunforge.util.PortalGunSounds;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(PortalGunForge.MOD_ID)
public class PortalGunForge {
    public static final String MOD_ID = "portalgun_forge";
    private static final Logger LOGGER = LogManager.getLogger();

    public PortalGunForge() {
        IEventBus MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        PortalGunItems.ITEMS.register(MOD_BUS);
        PortalGunSounds.SOUNDS.register(MOD_BUS);
    }
}
