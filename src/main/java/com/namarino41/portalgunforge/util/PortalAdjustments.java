package com.namarino41.portalgunforge.util;

import net.minecraft.util.math.Tuple3d;

/**
 * Enums to properly adjust position and rotation of portals. These are based on
 * the the block face the portal was fired at and the direction the player is facing
 * (i.e. UP_NORTH: Portal was fired at the UP face of a block and the player was facing NORTH).
 */
public enum PortalAdjustments {
    UP_NORTH(0, -.95, -.5, "x", -90, "", 0),
    UP_SOUTH(0, -.95, .5, "x", 90, "", 0),
    UP_EAST(.5, -.95, 0, "z", -90, "", 0),
    UP_WEST(-.5, -.95, 0, "z", 90, "", 0),

    DOWN_NORTH(0, .95, -.5, "x", 90, "", 0),
    DOWN_SOUTH(0, .95, .5, "x", -90, "", 0),
    DOWN_EAST(.5, .95, 0, "z", 90, "", 0),
    DOWN_WEST(-.5, .95, 0, "z", -90, "", 0),

    NORTH_UP(0, .5, .95, "x", 90, "", 0),
    NORTH_DOWN(0, .5, .95, "x", -90, "", 0),
    NORTH_EAST(0, .5, .95, "x", -90, "y", -90),
    NORTH_WEST(0, .5, .95, "x", -90, "y", 90),

    SOUTH_UP(0, .5, -.95, "x", -90, "", 0),
    SOUTH_DOWN(0, .5, -.95, "x", 90, "", 0),
    SOUTH_EAST(0, .5, -.95, "x", 90, "y", 90),
    SOUTH_WEST(0, .5, -.95, "x", 90, "y", -90),

    EAST_UP(-.95, .5, 0, "z", 90, "", 0),
    EAST_DOWN(-.95, .5, 0, "z", -90, "", 0),
    EAST_NORTH(-.95, .5, 0, "z", 90, "y", 90),
    EAST_SOUTH(-.95, .5, 0, "z", 90, "y", -90),

    WEST_UP(.95, .5, 0, "z", 90, "", 0),
    WEST_DOWN(.95, .5, 0, "z", 90, "", 0),
    WEST_NORTH(.95, .5, 0, "z", -90, "y", -90),
    WEST_SOUTH(.95, .5, 0, "z", -90, "y", 90);

    private final double xAdjustment;
    private final double yAdjustment;
    private final double zAdjustment;
    private final String bodyRotationAxis1;
    private final double bodyRotationAngle1;
    private final String bodyRotationAxis2;
    private final double bodyRotationAngle2;

    PortalAdjustments(double xAdjustment,
                      double yAdjustment,
                      double zAdjustment,
                      String bodyRotationAxis1,
                      double bodyRotationAngle1,
                      String bodyRotationAxis2,
                      double bodyRotationAngle2) {
        this.xAdjustment = xAdjustment;
        this.yAdjustment = yAdjustment;
        this.zAdjustment = zAdjustment;
        this.bodyRotationAxis1 = bodyRotationAxis1;
        this.bodyRotationAngle1 = bodyRotationAngle1;
        this.bodyRotationAxis2 = bodyRotationAxis2;
        this.bodyRotationAngle2 = bodyRotationAngle2;
    }

    public Tuple3d getAdjustment() {
        return new Tuple3d(xAdjustment, yAdjustment, zAdjustment);
    }

    public String getBodyRotationAxis1() {
        return bodyRotationAxis1;
    }

    public double getBodyRotationAngle1() {
        return bodyRotationAngle1;
    }

    public String getBodyRotationAxis2() {
        return bodyRotationAxis2;
    }

    public double getBodyRotationAngle2() {
        return bodyRotationAngle2;
    }
}
