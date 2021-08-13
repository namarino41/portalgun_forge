package com.namarino41.portalgunforge.util;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Tuple3d;


public class PortalCommands {
    private final CommandSource commandSource;
    private final Commands commandManager;

    private static final String MAKE_PORTAL = "portal make_portal 1 2 minecraft:overworld %f %f %f";
    private static final String DELETE_PORTAL = "execute as @e[type=immersive_portals:portal,name=\"%s\"] " +
            "run portal delete_portal";
    private static final String TAG_PORTAL = "execute as @e[type=immersive_portals:portal,nbt={Pos:[%fd,%fd,%fd]}] " +
            "run portal set_portal_custom_name \"%s\"";
    private static final String MAKE_PORTAL_ROUND = "execute as @e[type=immersive_portals:portal,name=\"%s\"] " +
            "run portal make_portal_round";
    private static final String COMPLETE_BI_WAY_PORTAL = "execute as @e[type=immersive_portals:portal,name=\"%s\"] " +
            "run portal complete_bi_way_portal";
    private static final String SET_PORTAL_POSITION = "execute as @e[type=immersive_portals:portal,name=\"%s\"] " +
            "run portal set_portal_position minecraft:overworld %f %f %f";
    private static final String ROTATE_PORTAL_BODY = "execute as @e[type=immersive_portals:portal,name=\"%s\"] " +
            "run portal rotate_portal_body_along %s %f";
    private static final String ROTATE_PORTAL_ROTATION = "execute as @e[type=immersive_portals:portal,name=\"%s\"] " +
            "run portal rotate_portal_rotation_along y 180";

    public PortalCommands(CommandSource commandSource, Commands commandManager) {
        this.commandSource = commandSource;
        this.commandManager = commandManager;
    }

    public void makePortal(Tuple3d destination) {
        commandManager.handleCommand(commandSource,
                String.format(MAKE_PORTAL, destination.x, destination.y, destination.z));
    }

    public void deletePortal(String tag) {
        commandManager.handleCommand(commandSource, String.format(DELETE_PORTAL, tag));
    }

    public void tagPortal(Tuple3d portalPosition, String tag) {
        commandManager.handleCommand(commandSource,
                String.format(TAG_PORTAL, portalPosition.x, portalPosition.y, portalPosition.z, tag));
    }

    public void completeBiWayPortal(String tag) {
        commandManager.handleCommand(commandSource, String.format(COMPLETE_BI_WAY_PORTAL, tag));
    }

    public void makePortalRound(String tag) {
        commandManager.handleCommand(commandSource, String.format(MAKE_PORTAL_ROUND, tag));
    }

    public void setPortalPosition(String tag, Tuple3d portalPosition, PortalAdjustments portalRotation) {
        commandManager.handleCommand(commandSource, String.format(SET_PORTAL_POSITION, tag,
                portalPosition.x + portalRotation.getAdjustment().x,
                portalPosition.y + portalRotation.getAdjustment().y,
                portalPosition.z + portalRotation.getAdjustment().z));
    }

    public void rotatePortalBody(String tag, PortalAdjustments portalRotation) {
        commandManager.handleCommand(commandSource,
                String.format(ROTATE_PORTAL_BODY,
                        tag,
                        portalRotation.getBodyRotationAxis1(),
                        portalRotation.getBodyRotationAngle1()));
        if (!portalRotation.getBodyRotationAxis2().isEmpty()) {
            commandManager.handleCommand(commandSource,
                    String.format(ROTATE_PORTAL_BODY,
                            tag,
                            portalRotation.getBodyRotationAxis2(),
                            portalRotation.getBodyRotationAngle2()));
        }
    }
}
