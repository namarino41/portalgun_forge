package com.namarino41.portalgunforge.entities;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;

public class PortalContext {
    private final Direction playerFacing;
    private final Direction horizontalPlayerFacing;
    private final BlockRayTraceResult blockRayTraceResult;

    public PortalContext(Direction playerFacing, Direction horizontalPlayerFacing, BlockRayTraceResult blockRayTraceResult) {
        this.playerFacing = playerFacing;
        this.horizontalPlayerFacing = horizontalPlayerFacing;
        this.blockRayTraceResult = blockRayTraceResult;
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
}
