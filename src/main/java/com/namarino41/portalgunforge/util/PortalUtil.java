package com.namarino41.portalgunforge.util;

import com.namarino41.portalgunforge.entities.Portal;
import com.namarino41.portalgunforge.entities.PortalContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Tuple3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class PortalUtil {
    private static final Logger LOGGER = LogManager.getLogger();

    private final PortalCommands portalCommands;

    private static final List<Direction> HORIZONTAL_DIRECTIONS = new ArrayList<>();
    private static final List<Direction> VERTICAL_DIRECTIONS = new ArrayList<>();

    static {
        VERTICAL_DIRECTIONS.add(Direction.UP);
        VERTICAL_DIRECTIONS.add(Direction.DOWN);

        HORIZONTAL_DIRECTIONS.add(Direction.NORTH);
        HORIZONTAL_DIRECTIONS.add(Direction.EAST);
        HORIZONTAL_DIRECTIONS.add(Direction.SOUTH);
        HORIZONTAL_DIRECTIONS.add(Direction.WEST);
    }

    public static String PORTAL_1_ID;
    public static String PORTAL_2_ID;

    public PortalUtil(World worldIn, Entity playerIn) {
        this.portalCommands = new PortalCommands(
                worldIn.getServer().getCommandSource()
                                    .withPermissionLevel(2)
                                    .withEntity(playerIn)
                                    .withFeedbackDisabled(),
                worldIn.getServer().getCommandManager());

        PORTAL_1_ID = "portal_1_" + playerIn.getUniqueID();
        PORTAL_2_ID = "portal_2_" + playerIn.getUniqueID();
    }

    public Portal makePortal(PortalContext portalContext, String tag) {
        Tuple3d adjustedPosition = getPortalPosFromRayTrace(portalContext);
        Portal portal = new Portal(tag, adjustedPosition, portalContext);

        portalCommands.makePortal();
        portalCommands.tagPortal(adjustedPosition, tag);
        portalCommands.makePortalRound(portal);
        portalCommands.changePortalTeleportable(portal, false);
        portalCommands.adjustPositionAfterTeleport(portal);

        positionPortal(portal);

        return portal;
    }

    public void linkPortals(Portal portal1, Portal portal2) {
        portalCommands.linkPortalToPortal(portal1, portal2);
        portalCommands.linkPortalToPortal(portal2, portal1);

        portalCommands.changePortalTeleportable(portal1, true);
        portalCommands.changePortalTeleportable(portal2, true);
    }

    private void positionPortal(Portal portal) {
        Direction portal2BlockFace = portal.getBlockFace();
        Direction portal2PlayerFacing = portal.getPlayerFacing();
        PortalAdjustments portalAdjustments = PortalAdjustments.valueOf(portal2BlockFace, portal2PlayerFacing);

        portalCommands.movePortal(portal, portalAdjustments);
        portalCommands.rotatePortalBody(portal, portalAdjustments);
    }

    public void adjustPortalRotation(Portal portal1, Portal portal2) {
        Direction portal1PortalFacing = portal1.getPortalFacing();
        Direction portal2PortalFacing = portal2.getPortalFacing();

        Direction portal1PlayerFacing = portal1.getPlayerFacing();
        Direction portal2PlayerFacing = portal2.getPlayerFacing();

        if (portal1.isVertical() && portal2.isVertical()) {
            portalCommands.rotatePortalRotation(portal1, "y", getRotation(portal1PortalFacing, portal2PortalFacing));
            portalCommands.rotatePortalRotation(portal2, "y", getRotation(portal2PortalFacing, portal1PortalFacing));
        } else if (portal1.isVertical() && !portal2.isVertical()) {
            portalCommands.rotatePortalRotation(portal1,
                                                portal1PortalFacing == Direction.NORTH ||
                                                        portal1PortalFacing == Direction.SOUTH ? "x" : "z",
                                                getRotation(portal1PortalFacing, portal2PortalFacing));
            if (portal1PortalFacing != portal2PlayerFacing) {
                portalCommands.rotatePortalRotation(portal1,
                                                    portal1PortalFacing == Direction.NORTH ||
                                                            portal1PortalFacing == Direction.SOUTH ? "z" : "x",
                                                    getRotation(portal1PlayerFacing, portal2PlayerFacing));
            }

        } else if (!portal1.isVertical() && portal2.isVertical()) {


        } else if (!portal1.isVertical() && !portal2.isVertical()) {


        }
    }

    private int getRotation(Direction from, Direction to) {
        if (VERTICAL_DIRECTIONS.contains(from) && VERTICAL_DIRECTIONS.contains(to)) {
            if (from == Direction.DOWN && to == Direction.UP) {
                return -180;
            }
            if (from == Direction.UP && to == Direction.DOWN) {
                return 180;
            }
            if (from == to) {
                return 0;
            }
        }

        if (HORIZONTAL_DIRECTIONS.contains(from) && VERTICAL_DIRECTIONS.contains(to)) {
            if (to == Direction.UP) {
                if (from == Direction.NORTH || from == Direction.EAST) {
                    return -90;
                } else {
                    return 90;
                }
            } else {
                if (from == Direction.NORTH || from == Direction.EAST) {
                    return 90;
                } else {
                    return -90;
                }
            }
        }

        if (VERTICAL_DIRECTIONS.contains(from) && HORIZONTAL_DIRECTIONS.contains(to)) {
            if (from == Direction.UP) {
                if (to == Direction.NORTH || to == Direction.EAST) {
                    return 90;
                } else {
                    return -90;
                }
            } else {
                if (to == Direction.NORTH || to == Direction.EAST) {
                    return -90;
                } else {
                    return 90;
                }
            }
        }

        if (HORIZONTAL_DIRECTIONS.contains(from) && HORIZONTAL_DIRECTIONS.contains(to)) {
            int fromIndex = HORIZONTAL_DIRECTIONS.indexOf(from);
            int toIndex = HORIZONTAL_DIRECTIONS.indexOf(to);

            return 90 * (toIndex - fromIndex);
        }
        return 0;
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
     * immersive portal's internal logic positions it a certain way based on what
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
