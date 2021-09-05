package com.namarino41.portalgunforge.entities;

import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;

public class Portal {
    private final String id;
    private final Vector3d position;
    private final PortalContext portalContext;

    public Portal(String id, Vector3d position, PortalContext portalContext) {
        this.id = id;
        this.position = position;
        this.portalContext = portalContext;
    }

    public String getId() {
        return id;
    }

    public String getDimension() {
        return portalContext.getDimension();
    }

    public Vector3d getPosition() {
        return position;
    }

    public Direction getPlayerFacing() {
        return portalContext.getPlayerFacing();
    }

    public Direction getHorizontalPlayerFacing() {
        return portalContext.getHorizontalPlayerFacing();
    }

    public Direction getBlockFace() {
        return portalContext.getBlockRayTraceResult().getFace();
    }

    public boolean isVertical() {
        Direction blockFace = portalContext.getBlockRayTraceResult().getFace();
        return blockFace != Direction.UP && blockFace != Direction.DOWN;
    }
}
