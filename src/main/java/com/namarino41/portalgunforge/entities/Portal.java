package com.namarino41.portalgunforge.entities;

import net.minecraft.util.math.Tuple3d;

public class Portal {
    private final String id;
    private final Tuple3d position;
    private final Tuple3d destination;
    private final PortalContext portalContext;

    public Portal(String id, Tuple3d position, Tuple3d destination, PortalContext portalContext) {
        this.id = id;
        this.position = position;
        this.destination = destination;
        this.portalContext = portalContext;
    }

    public Tuple3d getPosition() {
        return position;
    }

    public Tuple3d getDestination() {
        return destination;
    }

    public String getId() {
        return id;
    }

    public PortalContext getPortalContext() {
        return portalContext;
    }
}
