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

package nova.core.wrapper.mc.forge.v18.wrapper.render;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import nova.core.render.model.MeshModel;
import nova.core.render.texture.EntityTexture;
import nova.core.render.texture.Texture;
import nova.core.wrapper.mc.forge.v18.render.RenderUtility;

import java.util.Optional;

/**
 * BWModel for dynamic rendering
 * @author Calclavia
 */
public class BWModel extends MeshModel {

	public void render() {
		render(Optional.empty());
	}

	public void render(Optional<RenderManager> entityRenderManager) {
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldRenderer = tessellator.getWorldRenderer();
		worldRenderer.setColorRGBA_F(1, 1, 1, 1);

		/**
		 * Convert textures and UV into Minecraft equivalent.
		 */
		flatten().forEach(
			model -> {
				if (model instanceof MeshModel) {
					MeshModel meshModel = (MeshModel) model;
					meshModel.faces.forEach(face ->
					{
						// Brightness is defined as: skyLight << 20 | blockLight << 4
						if (face.getBrightness() >= 0) {
							worldRenderer.setBrightness((int) (face.getBrightness() * (15 << 20 | 11 << 4)));
						} else {
							// Determine nearest adjacent block.
							worldRenderer.setBrightness(15 << 20 | 11 << 4);
						}

						worldRenderer.setNormal((int) face.normal.getX(), (int) face.normal.getY(), (int) face.normal.getZ());

						if (face.texture.isPresent()) {
							if (entityRenderManager.isPresent() && face.texture.get() instanceof EntityTexture) {
								//We're not working on an atlas, so just do... this.
								Texture t = face.texture.get();
								entityRenderManager.get().renderEngine.bindTexture(new ResourceLocation(t.domain, "textures/entities/" + t.resource + ".png"));
								face.vertices.forEach(
									v -> {
										worldRenderer.setColorRGBA(v.color.red(), v.color.green(), v.color.blue(), v.color.alpha());
										worldRenderer.addVertexWithUV(v.vec.getX(), v.vec.getY(), v.vec.getZ(), v.uv.getX(), v.uv.getY());
									}
								);
							} else {
								Texture texture = face.texture.get();
								TextureAtlasSprite icon = RenderUtility.instance.getTexture(texture);
								face.vertices.forEach(
									v -> {
										worldRenderer.setColorRGBA(v.color.red(), v.color.green(), v.color.blue(), v.color.alpha());
										if (icon != null) {
											worldRenderer.addVertexWithUV(v.vec.getX(), v.vec.getY(), v.vec.getZ(), icon.getInterpolatedU(16 * v.uv.getX()), icon.getInterpolatedV(16 * v.uv.getY()));
										} else {
											worldRenderer.addVertexWithUV(v.vec.getX(), v.vec.getY(), v.vec.getZ(), v.uv.getX(), v.uv.getY());
										}
									}
								);
							}
						} else {
							face.vertices.forEach(
								v -> {
									worldRenderer.setColorRGBA(v.color.red(), v.color.green(), v.color.blue(), v.color.alpha());
									worldRenderer.addVertex(v.vec.getX(), v.vec.getY(), v.vec.getZ());
								}
							);
						}
					});
				}
				//TODO: Handle BW Rendering
			}
		);
	}
}
