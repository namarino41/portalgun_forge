package com.namarino41.portalgunforge.util;

import com.namarino41.portalgunforge.PortalGunForge;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class PortalGunSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(
            ForgeRegistries.SOUND_EVENTS, PortalGunForge.MOD_ID);

    public static SoundEvent PORTAL_1_SHOOT_EVENT = registerSound("portal1_shoot");
    public static SoundEvent PORTAL_2_SHOOT_EVENT = registerSound("portal2_shoot");
    public static SoundEvent PORTAL_OPEN_EVENT = registerSound("portal_open");
    public static SoundEvent PORTAL_CLOSE_EVENT = registerSound("portal_close");

    private static SoundEvent registerSound(String eventName) {
        ResourceLocation location = new ResourceLocation(PortalGunForge.MOD_ID, eventName);
        SoundEvent event = new SoundEvent(location);
        SOUNDS.register(eventName, () -> event);
        return event;
    }
}
