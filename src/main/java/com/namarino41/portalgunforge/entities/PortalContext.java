package com.namarino41.portalgunforge.entities;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.DimensionType;

public class PortalContext {
    private final Direction playerFacing;
    private final Direction horizontalPlayerFacing;
    private final BlockRayTraceResult blockRayTraceResult;
    private final String dimension;

    public PortalContext(Direction playerFacing,
                         Direction horizontalPlayerFacing,
                         BlockRayTraceResult blockRayTraceResult,
                         String dimension) {
        this.playerFacing = playerFacing;
        this.horizontalPlayerFacing = horizontalPlayerFacing;
        this.blockRayTraceResult = blockRayTraceResult;
        this.dimension = dimension;
    }

    public Direction getPlayerFacing() {
        return playerFacing;
    }

    public Direction getHorizontalPlayerFacing() {
        return horizontalPlayerFacing;
    }

    public BlockRayTraceResult getBlockRayTraceResult() {
        return blockRayTraceResult;
    }

    public String getDimension() {
        return dimension;
    }
}
