package com.namarino41.portalgunforge.util;

import com.namarino41.portalgunforge.entities.Portal;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.math.vector.Vector3d;


public class PortalCommandClient {
    private final CommandSource commandSource;
    private final Commands commandManager;

    private static final String MAKE_PORTAL = "portal make_portal 1 2 minecraft:%s %f %f %f";
    private static final String DELETE_PORTAL = "execute as @e[type=immersive_portals:portal,name=\"%s\"] " +
            "run portal delete_portal";
    private static final String TAG_PORTAL = "execute as @e[type=immersive_portals:portal,nbt={Pos:[%fd,%fd,%fd]}] " +
            "run portal set_portal_custom_name \"%s\"";
    private static final String MAKE_PORTAL_ROUND = "execute as @e[type=immersive_portals:portal,name=\"%s\"] " +
            "run portal make_portal_round";
    private static final String TOGGLE_PORTAL_TELEPORTABLE = "execute as @e[type=immersive_portals:portal,name=\"%s\"] " +
            "run portal set_portal_nbt {teleportable:%s}";
    private static final String ADJUST_POSITION_AFTER_TELEPORT = "execute as @e[type=immersive_portals:portal,name=\"%s\"] " +
            "run portal set_portal_nbt {adjustPositionAfterTeleport:true}";
    private static final String SET_PORTAL_POSITION = "execute as @e[type=immersive_portals:portal,name=\"%s\"] " +
            "run portal set_portal_position minecraft:%s %f %f %f";
    private static final String ROTATE_PORTAL_BODY = "execute as @e[type=immersive_portals:portal,name=\"%s\"] " +
            "run portal rotate_portal_body_along %s %f";
    private static final String LINK_PORTAL_TO_PORTAL = "execute as @e[type=immersive_portals:portal,name=\"%s\"] " +
            "run portal set_portal_destination_to @e[limit=1,type=immersive_portals:portal,name=\"%s\"]";
    private static final String ROTATE_PORTAL_ROTATION = "execute as @e[type=immersive_portals:portal,name=\"%s\"] " +
            "run portal rotate_portal_rotation_along %s %d";

    public PortalCommandClient(CommandSource commandSource, Commands commandManager) {
        this.commandSource = commandSource;
        this.commandManager = commandManager;
    }

    public void makePortal(String dimension) {
        commandManager.handleCommand(commandSource,
                String.format(MAKE_PORTAL, dimension, 0.0, -100.0, 0.0));
    }

    public void deletePortal(Portal portal) {
        commandManager.handleCommand(commandSource, String.format(DELETE_PORTAL, portal.getId()));
    }

    public void tagPortal(Vector3d portalPosition, String tag) {
        commandManager.handleCommand(commandSource,
                String.format(TAG_PORTAL, portalPosition.x, portalPosition.y, portalPosition.z, tag));
    }

    public void makePortalRound(Portal portal) {
        commandManager.handleCommand(commandSource, String.format(MAKE_PORTAL_ROUND, portal.getId()));
    }

    public void movePortal(Portal portal, PortalAdjustments portalAdjustments) {
        commandManager.handleCommand(commandSource, String.format(SET_PORTAL_POSITION, portal.getId(), portal.getDimension(),
                portal.getPosition().x + portalAdjustments.getAdjustment().x,
                portal.getPosition().y + portalAdjustments.getAdjustment().y,
                portal.getPosition().z + portalAdjustments.getAdjustment().z));
    }

    public void rotatePortalBody(Portal portal, PortalAdjustments portalRotation) {
        commandManager.handleCommand(commandSource,
                String.format(ROTATE_PORTAL_BODY,
                        portal.getId(),
                        portalRotation.getBodyRotationAxis1(),
                        portalRotation.getBodyRotationAngle1()));
        if (!portalRotation.getBodyRotationAxis2().isEmpty()) {
            commandManager.handleCommand(commandSource,
                    String.format(ROTATE_PORTAL_BODY,
                            portal.getId(),
                            portalRotation.getBodyRotationAxis2(),
                            portalRotation.getBodyRotationAngle2()));
        }
    }

    public void changePortalTeleportable(Portal portal, boolean teleportable) {
        commandManager.handleCommand(commandSource,
                String.format(TOGGLE_PORTAL_TELEPORTABLE, portal.getId(), teleportable));
    }

    public void linkPortalToPortal(Portal portal1, Portal portal2) {
        commandManager.handleCommand(commandSource,
                String.format(LINK_PORTAL_TO_PORTAL, portal1.getId(), portal2.getId()));
    }

    public void adjustPositionAfterTeleport(Portal portal) {
        commandManager.handleCommand(commandSource,
                String.format(ADJUST_POSITION_AFTER_TELEPORT, portal.getId()));
    }

    public void rotatePortalRotation(Portal portal, String rotationAxis, int rotationAngle) {
        commandManager.handleCommand(commandSource,
                String.format(ROTATE_PORTAL_ROTATION, portal.getId(), rotationAxis, rotationAngle));
    }
}
