/*
 * Copyright (c) 2015 NOVA, All rights reserved.
 * This library is free software, licensed under GNU Lesser General Public License version 3
 *
 * This file is part of NOVA.
 *
 * NOVA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NOVA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NOVA.  If not, see <http://www.gnu.org/licenses/>.
 */

package nova.core.wrapper.mc.forge.v18.launcher;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import nova.core.entity.Entity;
import nova.core.entity.EntityFactory;
import nova.core.wrapper.mc.forge.v18.render.RenderUtility;
import nova.core.wrapper.mc.forge.v18.wrapper.block.forward.FWBlock;
import nova.core.wrapper.mc.forge.v18.wrapper.block.forward.FWTile;
import nova.core.wrapper.mc.forge.v18.wrapper.block.forward.FWTileRenderer;
import nova.core.wrapper.mc.forge.v18.wrapper.entity.backward.BWEntityFX;
import nova.core.wrapper.mc.forge.v18.wrapper.entity.forward.FWEntity;
import nova.core.wrapper.mc.forge.v18.wrapper.entity.forward.FWEntityFX;
import nova.core.wrapper.mc.forge.v18.wrapper.entity.forward.FWEntityRenderer;
import nova.core.wrapper.mc.forge.v18.wrapper.item.FWItem;
import nova.internal.core.Game;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * @author Calclavia
 */
public class ClientProxy extends CommonProxy {
	@Override
	public void preInit() {
		super.preInit();
		MinecraftForge.EVENT_BUS.register(RenderUtility.instance);
		ClientRegistry.bindTileEntitySpecialRenderer(FWTile.class, FWTileRenderer.instance);
		RenderUtility.instance.preInit();
	}

	@Override
	public void init() {
		super.init();
		RenderingRegistry.registerEntityRenderingHandler(FWEntity.class, FWEntityRenderer.instance);
	}

	@Override
	public void registerItem(FWItem item) {
		super.registerItem(item);

		//Hacks to inject custom item definition
		ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition() {
				@Override
				public ModelResourceLocation getModelLocation(ItemStack stack) {
					ResourceLocation itemRL = (ResourceLocation) Item.itemRegistry.getNameForObject(item);
					return new ModelResourceLocation(itemRL, "inventory");
				}
			}
		);
	}

	@Override
	public void postRegisterBlock(FWBlock block) {
		super.postRegisterBlock(block);

		//Hack to inject custom itemblock definition
		Item itemFromBlock = Item.getItemFromBlock(block);

		ModelLoader.setCustomMeshDefinition(itemFromBlock, new ItemMeshDefinition() {
				@Override
				public ModelResourceLocation getModelLocation(ItemStack stack) {
					ResourceLocation itemRL = (ResourceLocation) Item.itemRegistry.getNameForObject(itemFromBlock);
					return new ModelResourceLocation(itemRL, "inventory");
				}
			}
		);
	}

	@Override
	public boolean isPaused() {
		if (FMLClientHandler.instance().getClient().isSingleplayer() && !FMLClientHandler.instance().getClient().getIntegratedServer().getPublic()) {
			GuiScreen screen = FMLClientHandler.instance().getClient().currentScreen;
			if (screen != null) {
				if (screen.doesGuiPauseGame()) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Entity spawnParticle(net.minecraft.world.World world, EntityFactory factory) {
		//Backward entity particle unwrapper
		Entity build = factory.build();
		if (build instanceof BWEntityFX) {
			EntityFX entityFX = ((BWEntityFX) build).createEntityFX(world);
			FMLClientHandler.instance().getClient().effectRenderer.addEffect(entityFX);
			return Game.natives().toNova(entityFX);
		} else {
			FWEntityFX bwEntityFX = new FWEntityFX(world, factory);
			FMLClientHandler.instance().getClient().effectRenderer.addEffect(bwEntityFX);
			return bwEntityFX.wrapped;
		}
	}

	@Override
	public Entity spawnParticle(net.minecraft.world.World world, Entity entity) {
		//Backward entity particle unwrapper
		if (entity instanceof BWEntityFX) {
			EntityFX entityFX = ((BWEntityFX) entity).createEntityFX(world);
			Vector3D position = entity.position();
			entityFX.posX = position.getX();
			entityFX.posY = position.getY();
			entityFX.posZ = position.getZ();
			FMLClientHandler.instance().getClient().effectRenderer.addEffect(entityFX);
			return Game.natives().toNova(entityFX);
		} else {
			FWEntityFX bwEntityFX = new FWEntityFX(world, entity);
			FMLClientHandler.instance().getClient().effectRenderer.addEffect(bwEntityFX);
			return bwEntityFX.wrapped;
		}
	}

	@Override
	public EntityPlayer getClientPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}
}
