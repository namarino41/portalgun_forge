package com.namarino41.portalgunforge.items;

import com.namarino41.portalgunforge.entities.PortalContext;
import com.namarino41.portalgunforge.util.PortalGunSounds;
import com.namarino41.portalgunforge.util.PortalManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class PortalGunItem extends Item {
    private final Map<PlayerEntity, PortalManager> playerEntityPortalManagerMap = new HashMap<>();

    private final int MAX_RANGE = 500;

    public PortalGunItem() {
        super(new Item.Properties().group(ItemGroup.TOOLS));
        MinecraftForge.EVENT_BUS.register(new LoginLogoutHandler());
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (!worldIn.isRemote) {
            PortalManager portalManager = playerEntityPortalManagerMap.get(playerIn);

            RayTraceContext rayTraceContext = new RayTraceContext(
                    playerIn.getEyePosition(1.0f),
                    playerIn.getEyePosition(1.0f).add(playerIn.getLookVec().scale(100.0)),
                    RayTraceContext.BlockMode.OUTLINE,
                    RayTraceContext.FluidMode.NONE,
                    playerIn);

            BlockRayTraceResult blockRayTraceResult = worldIn.rayTraceBlocks(rayTraceContext);
            if (blockRayTraceResult.getType() != RayTraceResult.Type.BLOCK) {
                return ActionResult.resultFail(playerIn.getHeldItem(handIn));
            }

            if (!blockRayTraceResult.getPos().withinDistance(new Vector3i(
                    playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ()), MAX_RANGE)) {
                return ActionResult.resultFail(playerIn.getHeldItem(handIn));
            }

            Direction lookingDirection = Arrays.stream(Direction.values())
                    .filter(d -> d.getAxis() != blockRayTraceResult.getFace().getAxis())
                    .max(Comparator.comparingDouble(
                            dir -> playerIn.getLookVec().x * dir.getXOffset() +
                                   playerIn.getLookVec().y * dir.getYOffset() +
                                   playerIn.getLookVec().z * dir.getZOffset()))
                    .orElse(null);

            if (!portalManager.isValidPosition(worldIn, blockRayTraceResult, lookingDirection)) {
                return ActionResult.resultFail(playerIn.getHeldItem(handIn));
            }

            if (portalManager.getPortal1() != null && portalManager.getPortal2() != null) {
                portalManager.deletePortal1();
                portalManager.deletePortal2();
            }

            PortalContext portalContext = new PortalContext(lookingDirection,
                                                            playerIn.getHorizontalFacing(),
                                                            blockRayTraceResult,
                                                            worldIn.func_234923_W_().func_240901_a_().getPath());
            if (portalManager.getPortal1() == null) {
                portalManager.playSound(worldIn, playerIn, PortalGunSounds.PORTAL_1_SHOOT_EVENT);
                portalManager.makePortal1(portalContext);
            } else {
                portalManager.playSound(worldIn, playerIn, PortalGunSounds.PORTAL_2_SHOOT_EVENT);
                portalManager.makePortal2(portalContext);
                portalManager.linkPortals();
                portalManager.adjustPortalRotation();
                portalManager.playSound(worldIn, playerIn, PortalGunSounds.PORTAL_OPEN_EVENT);
            }
        }

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    class LoginLogoutHandler {
        @SubscribeEvent
        public void onPlayerLoginEvent(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getPlayer() instanceof ServerPlayerEntity) {
                PlayerEntity playerEntity = event.getPlayer();
                playerEntityPortalManagerMap.put(playerEntity, new PortalManager(playerEntity));
            }
        }

        @SubscribeEvent
        public void onPlayerLogoutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
            if (event.getPlayer() instanceof ServerPlayerEntity) {
                PlayerEntity playerEntity = event.getPlayer();
                PortalManager portalManager = playerEntityPortalManagerMap.remove(playerEntity);
                if (portalManager.getPortal1() != null) {
                    portalManager.deletePortal1();
                }
                if (portalManager.getPortal2() != null) {
                    portalManager.deletePortal2();
                }
            }
        }
    }
}
