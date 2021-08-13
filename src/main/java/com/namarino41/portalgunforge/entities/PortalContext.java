package com.namarino41.portalgunforge.entities;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;

public class PortalContext {
    private final Direction playerFacing;
    private final BlockRayTraceResult blockRayTraceResult;

    public PortalContext(Direction playerFacing, BlockRayTraceResult blockRayTraceResult) {
        this.playerFacing = playerFacing;
        this.blockRayTraceResult = blockRayTraceResult;
    }

    public Direction getPlayerFacing() {
        return playerFacing;
    }

    public BlockRayTraceResult getBlockRayTraceResult() {
        return blockRayTraceResult;
    }
}
