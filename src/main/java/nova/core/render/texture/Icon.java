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
 */package nova.core.render.texture;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * An Icon defines a sub-area of a {@link Texture}.
 * 
 * @author Vic Nightfall
 */
public class Icon {

	protected Texture texture;
	protected Vector2D minUV;
	protected Vector2D maxUV;

	/** Package private constructor for Texture **/
	Icon() {
	}

	public Icon(Texture texture, Vector2D minUV, Vector2D maxUV) {
		this.texture = texture;
		this.minUV = minUV;
		this.maxUV = maxUV;
	}

	public Texture texture() {
		return texture;
	}

	public Vector2D minUV() {
		return minUV;
	}

	public Vector2D maxUV() {
		return maxUV;
	}
}
