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

package nova.core.wrapper.mc.forge.v17.network.netty;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import nova.core.wrapper.mc.forge.v17.launcher.NovaMinecraft;
import nova.core.wrapper.mc.forge.v17.network.discriminator.PacketAbstract;

/**
 * @author tgame14
 * @since 31/05/14
 */
@ChannelHandler.Sharable
public class MCPacketHandler extends SimpleChannelInboundHandler<PacketAbstract> {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, PacketAbstract packet) throws Exception {
		INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();

		switch (FMLCommonHandler.instance().getEffectiveSide()) {
			case CLIENT:
				packet.handleClientSide(NovaMinecraft.proxy.getClientPlayer());
				break;
			case SERVER:
				packet.handleServerSide(((NetHandlerPlayServer) netHandler).playerEntity);
				break;
			default:
				break;
		}

	}

}
