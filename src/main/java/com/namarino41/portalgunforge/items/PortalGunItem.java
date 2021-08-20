package com.namarino41.portalgunforge.items;

import com.namarino41.portalgunforge.entities.Portal;
import com.namarino41.portalgunforge.entities.PortalContext;
import com.namarino41.portalgunforge.util.PortalGunSounds;
import com.namarino41.portalgunforge.util.PortalUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.Comparator;


public class PortalGunItem extends Item {
    private PortalUtil portalUtil;

    private Portal portal1;
    private Portal portal2;

    public PortalGunItem() {
        super(new Item.Properties().group(ItemGroup.TOOLS));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (!worldIn.isRemote) {
            if (portalUtil == null) {
                portalUtil = new PortalUtil(worldIn, playerIn);
            }

            RayTraceContext rayTraceContext = new RayTraceContext(
                    playerIn.getEyePosition(1.0f),
                    playerIn.getEyePosition(1.0f).add(playerIn.getLookVec().scale(100.0)),
                    RayTraceContext.BlockMode.OUTLINE,
                    RayTraceContext.FluidMode.NONE,
                    playerIn
            );

            BlockRayTraceResult blockRayTraceResult = worldIn.rayTraceBlocks(rayTraceContext);
            if (blockRayTraceResult.getType() != RayTraceResult.Type.BLOCK) {
                return ActionResult.resultFail(playerIn.getHeldItem(handIn));
            }

            Direction lookingDirection = Arrays.stream(Direction.values())
                    .filter(d -> d.getAxis() != blockRayTraceResult.getFace().getAxis())
                    .max(Comparator.comparingDouble(
                            dir -> playerIn.getLookVec().x * dir.getXOffset() +
                                   playerIn.getLookVec().y * dir.getYOffset() +
                                   playerIn.getLookVec().z * dir.getZOffset()))
                    .orElse(null);

            PortalContext portalContext = new PortalContext(lookingDirection, blockRayTraceResult);

            if (portal1 == null) {
                // Since we can't make a portal to nowhere, for the first portal, we're simply going to
                // store the location. Then for the second portal, we can create it with a destination
                // of the initial position we tagged.
                portalUtil.playSound(worldIn, playerIn, PortalGunSounds.PORTAL_1_SHOOT_EVENT);
//                portal1Context = portalContext;
                portal1 = portalUtil.makePortal(portalContext, PortalUtil.PORTAL_1_ID);
            } else {
                portalUtil.playSound(worldIn, playerIn, PortalGunSounds.PORTAL_2_SHOOT_EVENT);
                portal2 = portalUtil.makePortal(portalContext, PortalUtil.PORTAL_2_ID);
                portalUtil.linkPortals(portal1, portal2);
                portalUtil.adjustPortalRotation(portal1, portal2);
                portalUtil.playSound(worldIn, playerIn, PortalGunSounds.PORTAL_OPEN_EVENT);

                portal1 = null;
                portal2 = null;
            }
        }

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }


}
