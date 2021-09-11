package com.namarino41.portalgunforge.util;

import com.google.common.collect.ImmutableList;
import com.namarino41.portalgunforge.entities.Portal;
import com.namarino41.portalgunforge.entities.PortalContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PortalManager {
    private static final Logger LOGGER = LogManager.getLogger();

    private final PortalCommandClient portalCommandClient;

    private static final ImmutableList<Direction> HORIZONTAL_DIRECTIONS =
            ImmutableList.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
    private static final ImmutableList<Direction> VERTICAL_DIRECTIONS =
            ImmutableList.of(Direction.UP, Direction.DOWN);

    private final String PORTAL_1_ID;
    private final String PORTAL_2_ID;

    private Portal portal1;
    private Portal portal2;

    public PortalManager(Entity playerIn) {
        World worldIn = playerIn.getEntityWorld();

        this.portalCommandClient = new PortalCommandClient(
                worldIn.getServer().getCommandSource()
                        .withPermissionLevel(2)
                        .withEntity(playerIn)
                        .withFeedbackDisabled(),
                worldIn.getServer().getCommandManager());

        PORTAL_1_ID = "portal_1_" + playerIn.getUniqueID();
        PORTAL_2_ID = "portal_2_" + playerIn.getUniqueID();
    }

    public Portal getPortal1() {
        return portal1;
    }

    public Portal getPortal2() {
        return portal2;
    }

    public void makePortal1(PortalContext portalContext) {
        portal1 = makePortal(portalContext, PORTAL_1_ID);
    }

    public void makePortal2(PortalContext portalContext) {
        portal2 = makePortal(portalContext, PORTAL_2_ID);
    }

    private Portal makePortal(PortalContext portalContext, String tag) {
        Vector3d adjustedPosition = getPortalPosFromRayTrace(portalContext);
        Portal portal = new Portal(tag, adjustedPosition, portalContext);

        portalCommandClient.makePortal(portalContext.getDimension());
        portalCommandClient.tagPortal(adjustedPosition, tag);
        portalCommandClient.makePortalRound(portal);
        portalCommandClient.changePortalTeleportable(portal, false);
        portalCommandClient.adjustPositionAfterTeleport(portal);

        positionPortal(portal);

        return portal;
    }

    private void positionPortal(Portal portal) {
        Direction portal2BlockFace = portal.getBlockFace();
        Direction portal2PlayerFacing = portal.getPlayerFacing();
        PortalAdjustments portalAdjustments = PortalAdjustments.valueOf(portal2BlockFace, portal2PlayerFacing);

        portalCommandClient.movePortal(portal, portalAdjustments);
        portalCommandClient.rotatePortalBody(portal, portalAdjustments);
    }

    public void linkPortals() {
        portalCommandClient.linkPortalToPortal(portal1, portal2);
        portalCommandClient.linkPortalToPortal(portal2, portal1);

        portalCommandClient.changePortalTeleportable(portal1, true);
        portalCommandClient.changePortalTeleportable(portal2, true);
    }

    public void adjustPortalRotation() {
        Direction portal1PortalFacing = portal1.getBlockFace();
        Direction portal2PortalFacing = portal2.getBlockFace();

        Direction portal1PlayerFacing = portal1.getHorizontalPlayerFacing();
        Direction portal2PlayerFacing = portal2.getHorizontalPlayerFacing();

        if (portal1.isVertical() && portal2.isVertical()) {
            portalCommandClient.rotatePortalRotation(portal1, "y", getRotation(portal1PortalFacing, portal2PortalFacing));
            portalCommandClient.rotatePortalRotation(portal2, "y", getRotation(portal2PortalFacing, portal1PortalFacing));
        } else if (portal1.isVertical() && !portal2.isVertical()) {
            // Orient the vertical portal
            portalCommandClient.rotatePortalRotation(portal1,
                                                portal1PortalFacing == Direction.NORTH ||
                                                        portal1PortalFacing == Direction.SOUTH ? "x" : "z",
                                                getRotation(portal1PortalFacing, portal2PortalFacing));
            portalCommandClient.rotatePortalRotation(portal1,
                                                portal1PortalFacing == Direction.NORTH ||
                                                        portal1PortalFacing == Direction.SOUTH ? "z" : "x",
                                                portal1PlayerFacing == Direction.NORTH ||
                                                        portal1PlayerFacing == Direction.WEST ?
                                                            -getRotation(portal1PlayerFacing, portal2PlayerFacing) :
                                                                getRotation(portal1PlayerFacing, portal2PlayerFacing));

            // Orient the horizontal portal
            String axis;
            if (portal2PlayerFacing == Direction.NORTH || portal2PlayerFacing == Direction.SOUTH) {
                if (portal2PlayerFacing != portal1PortalFacing && portal2PlayerFacing != portal1PlayerFacing) {
                    axis = "z";
                } else {
                    axis = "x";
                }
            } else {
                if (portal2PlayerFacing != portal1PortalFacing && portal2PlayerFacing != portal1PlayerFacing) {
                    axis = "x";
                } else {
                    axis = "z";
                }
            }

            portalCommandClient.rotatePortalRotation(portal2, axis, getRotation(portal2PortalFacing, portal1PortalFacing));
            portalCommandClient.rotatePortalRotation(portal2, "y", getRotation(portal2PlayerFacing, portal1PlayerFacing));
        } else if (!portal1.isVertical() && portal2.isVertical()) {
            // Orient the vertical portal
            portalCommandClient.rotatePortalRotation(portal2,
                    portal2PortalFacing == Direction.NORTH ||
                            portal2PortalFacing == Direction.SOUTH ? "x" : "z",
                    getRotation(portal2PortalFacing, portal1PortalFacing));
            portalCommandClient.rotatePortalRotation(portal2,
                                                portal2PortalFacing == Direction.NORTH ||
                                                        portal2PortalFacing == Direction.SOUTH ? "z" : "x",
                                                portal2PlayerFacing == Direction.NORTH ||
                                                        portal2PlayerFacing == Direction.WEST ?
                                                            -getRotation(portal2PlayerFacing, portal1PlayerFacing) :
                                                                getRotation(portal2PlayerFacing, portal1PlayerFacing));

            // Orient the horizontal portal
            String axis;
            if (portal1PlayerFacing == Direction.NORTH || portal1PlayerFacing == Direction.SOUTH) {
                if (portal1PlayerFacing != portal2PortalFacing && portal1PlayerFacing != portal2PlayerFacing) {
                    axis = "z";
                } else {
                    axis = "x";
                }
            } else {
                if (portal1PlayerFacing != portal2PortalFacing && portal1PlayerFacing != portal2PlayerFacing) {
                    axis = "x";
                } else {
                    axis = "z";
                }
            }

            portalCommandClient.rotatePortalRotation(portal1, axis, getRotation(portal1PortalFacing, portal2PortalFacing));
            portalCommandClient.rotatePortalRotation(portal1, "y", getRotation(portal1PlayerFacing, portal2PlayerFacing));
        } else if (!portal1.isVertical() && !portal2.isVertical()) {
            portalCommandClient.rotatePortalRotation(portal1,
                                                portal1PlayerFacing == Direction.NORTH ||
                                                    portal1PlayerFacing == Direction.SOUTH ? "z" : "x",
                                                getRotation(portal1PortalFacing, portal2PortalFacing));
            portalCommandClient.rotatePortalRotation(portal2,
                                                portal2PlayerFacing == Direction.NORTH ||
                                                        portal2PlayerFacing == Direction.SOUTH ? "z" : "x",
                                                getRotation(portal2PortalFacing, portal1PortalFacing));

            if (((portal1PlayerFacing == Direction.NORTH && portal2PlayerFacing == Direction.SOUTH) ||
                    (portal1PlayerFacing == Direction.SOUTH && portal2PlayerFacing == Direction.NORTH)) ||
                        ((portal1PlayerFacing == Direction.EAST && portal2PlayerFacing == Direction.WEST) ||
                                (portal1PlayerFacing == Direction.WEST && portal2PlayerFacing == Direction.EAST))) {
                portalCommandClient.rotatePortalRotation(portal1, "y", 180);
                portalCommandClient.rotatePortalRotation(portal2, "y", 180);
            } else if (portal1PlayerFacing == portal2PlayerFacing) {
                portalCommandClient.rotatePortalRotation(portal1, "y", 0);
                portalCommandClient.rotatePortalRotation(portal2, "y", 0);
            } else {
                portalCommandClient.rotatePortalRotation(portal1, "y", getRotation(portal1PlayerFacing, portal2PlayerFacing));
                portalCommandClient.rotatePortalRotation(portal2, "y", getRotation(portal2PlayerFacing, portal1PlayerFacing));
            }
        }
    }

    private int getRotation(Direction from, Direction to) {
        if (VERTICAL_DIRECTIONS.contains(from) && VERTICAL_DIRECTIONS.contains(to)) {
            if ((from == Direction.UP && to == Direction.DOWN) ||
                    (from == Direction.DOWN && to == Direction.UP)) {
                return 0;
            }
            if (from == to) {
                return 180;
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
            if (((from == Direction.NORTH && to == Direction.SOUTH) || (from == Direction.SOUTH && to == Direction.NORTH)) ||
                    ((from == Direction.EAST && to == Direction.WEST) || (from == Direction.WEST && to == Direction.EAST))) {
                return 0;
            }
            if (from == to) {
                return 180;
            }
            int fromIndex = HORIZONTAL_DIRECTIONS.indexOf(from);
            int toIndex = HORIZONTAL_DIRECTIONS.indexOf(to);

            return 90 * (toIndex - fromIndex);
        }
        return 0;
    }

    private Vector3d getPortalPosFromRayTrace(PortalContext portalContext) {
        Direction blockFace = portalContext.getBlockRayTraceResult().getFace();
        BlockPos blockPos = portalContext.getBlockRayTraceResult().getPos();
        PortalPosition portalPosition = PortalPosition.valueOf(blockFace.name());

        return new Vector3d(blockPos.getX() + portalPosition.xAdjustment,
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

    public void deletePortal1() {
        portalCommandClient.deletePortal(portal1);
        portal1 = null;
    }

    public void deletePortal2() {
        portalCommandClient.deletePortal(portal2);
        portal2 = null;
    }

    public boolean isValidPosition(World worldIn,
                                   BlockRayTraceResult blockRayTraceResult,
                                   Direction lookingDirection) {
        BlockState mainBlock = worldIn.getBlockState(blockRayTraceResult.getPos());
        BlockState adjacentBlock;

        BlockPos adjacentBlockPos;
        if (lookingDirection == Direction.NORTH) {
            adjacentBlockPos = blockRayTraceResult.getPos().north();
        } else if (lookingDirection == Direction.EAST) {
            adjacentBlockPos = blockRayTraceResult.getPos().east();
        } else if (lookingDirection == Direction.SOUTH) {
            adjacentBlockPos = blockRayTraceResult.getPos().south();
        } else if (lookingDirection == Direction.WEST){
            adjacentBlockPos = blockRayTraceResult.getPos().west();
        } else {
            adjacentBlockPos = blockRayTraceResult.getPos().up();
        }
        adjacentBlock = worldIn.getBlockState(adjacentBlockPos);

        if (mainBlock.getBlock() == Blocks.SNOW || adjacentBlock.getBlock() == Blocks.SNOW) {
            return false;
        }

        return mainBlock.isSolidSide(worldIn, blockRayTraceResult.getPos(), blockRayTraceResult.getFace()) &&
                adjacentBlock.isSolidSide(worldIn, blockRayTraceResult.getPos(), blockRayTraceResult.getFace());
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
