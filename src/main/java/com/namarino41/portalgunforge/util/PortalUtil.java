package com.namarino41.portalgunforge.util;

import com.namarino41.portalgunforge.entities.Portal;
import com.namarino41.portalgunforge.entities.PortalContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Tuple3d;
import net.minecraft.world.World;

public class PortalUtil {
    private final PortalCommands portalCommands;

    private final String PORTAL_1_ID;
    private final String PORTAL_2_ID;

    public PortalUtil(World worldIn, Entity playerIn) {
        this.portalCommands = new PortalCommands(
                worldIn.getServer().getCommandSource()
                                    .withPermissionLevel(2)
                                    .withEntity(playerIn)
                                    .withFeedbackDisabled(),
                worldIn.getServer().getCommandManager());

        this.PORTAL_1_ID = "portal_1_" + playerIn.getUniqueID();
        this.PORTAL_2_ID = "portal_2_" + playerIn.getUniqueID();
    }

    /**
     * Makes the portals. Because we can't create a portal to nowhere, we store the first portal's
     * position and then make the second portal with a destination of that initial position.
     * So basically we have to make them backwards; second portal, then first.
     *
     * @param from The ray trace result of first portal spot.
     * @param to The ray trace result of the second portal spot.
     * @return
     */
    public Tuple<Portal, Portal> makePortals(PortalContext from, PortalContext to) {
        Portal portal2 = makePortal2(from, to);
        Portal portal1 = linkPortals(portal2, from);

        System.out.println("Portal1 context:" + portal1.getPortalContext().getBlockRayTraceResult().getFace() + " " + portal1.getPortalContext().getPlayerFacing());
        System.out.println("Portal2 context:" + portal2.getPortalContext().getBlockRayTraceResult().getFace() + " " + portal2.getPortalContext().getPlayerFacing());

        rotateAndPositionPortal(portal1, portal2);
//        rotateAndPositionPortal(portal1);

        return new Tuple<>(portal1, portal2);
    }

    /**
     * Makes the second portal with a destination of the first given the two portal contexts.
     *
     * @param from The ray trace result of first portal spot.
     * @param to The ray trace result of the second portal spot.
     *
     * @return the second portal.
     */
    private Portal makePortal2(PortalContext from, PortalContext to) {
        // Get the actual positions the portals will be in.
        Tuple3d adjustedFrom = getPortalPosFromRayTrace(from);
        Tuple3d adjustedTo = getPortalPosFromRayTrace(to);

        // Make the second portal with a destination of the first. Then, tag the portal with
        // a custom ID. Remember, "from" is the first portal target; "to" is the second portal
        // target. When we create a portal though, it doesn't create a corresponding linked portal
        // automatically. That's done in "linkPortals()". Tagging requires the portal's position,
        // so we use "to" to tag the second portal.
        portalCommands.makePortal(adjustedFrom);
        portalCommands.tagPortal(adjustedTo, PORTAL_2_ID);

        return new Portal(PORTAL_2_ID, adjustedTo, adjustedFrom, to);
    }

    private Portal linkPortals(Portal portal, PortalContext portalContext) {
        portalCommands.completeBiWayPortal(PORTAL_2_ID);
        portalCommands.tagPortal(portal.getDestination(), PORTAL_1_ID);
        portalCommands.makePortalRound(PORTAL_1_ID);
        portalCommands.makePortalRound(PORTAL_2_ID);

        return new Portal(PORTAL_1_ID,
                          new Tuple3d(portal.getDestination().x, portal.getDestination().y, portal.getDestination().z),
                          new Tuple3d(portal.getPosition().x, portal.getPosition().y, portal.getPosition().z),
                          portalContext);
    }

    private void rotateAndPositionPortal(Portal portal1, Portal portal2) {
        Direction portal2BlockFace = portal2.getPortalContext().getBlockRayTraceResult().getFace();
        Direction portal2PlayerFacing = portal2.getPortalContext().getPlayerFacing();
        PortalAdjustments portalAdjustments = PortalAdjustments.valueOf(portal2BlockFace, portal2PlayerFacing);

        portalCommands.setPortalPosition(portal2.getId(), portal2.getPosition(), portalAdjustments);
        portalCommands.rotatePortalBody(portal2.getId(), portalAdjustments);
    }

    private Tuple3d getPortalPosFromRayTrace(PortalContext portalContext) {
        Direction blockFace = portalContext.getBlockRayTraceResult().getFace();
        BlockPos blockPos = portalContext.getBlockRayTraceResult().getPos();
        PortalPosition portalPosition = PortalPosition.valueOf(blockFace.name());

        return new Tuple3d(blockPos.getX() + portalPosition.xAdjustment,
                           blockPos.getY() + portalPosition.yAdjustment,
                           blockPos.getZ() + portalPosition.zAdjustment);
    }

    public void playSound(World worldIn, Entity playerIn, SoundEvent sound) {
        worldIn.playSound(null,
                playerIn.getPosX(),
                playerIn.getPosY(),
                playerIn.getPosZ(),
                sound,
                SoundCategory.NEUTRAL,
                1.0F,
                1F);
    }

    /**
     * Enums for the initial position of a portal. When we execute the "make_portal" command,
     * immersive portal's  internal logic positions it a certain way based on what
     * face of a block is being targeted. These enums help find the portal once it's created.
     */
    private enum PortalPosition {
        UP(.5, 2, .5),
        DOWN(.5, -1, .5),
        NORTH(.5, .5, -1),
        SOUTH(.5, .5, 2),
        EAST(2, .5, .5),
        WEST(-1, .5, .5);

        private final double xAdjustment;
        private final double yAdjustment;
        private final double zAdjustment;

        PortalPosition(double xAdjustment, double yAdjustment, double zAdjustment) {
            this.xAdjustment = xAdjustment;
            this.yAdjustment = yAdjustment;
            this.zAdjustment = zAdjustment;
        }
    }
}
