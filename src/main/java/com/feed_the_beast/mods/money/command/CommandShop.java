package com.feed_the_beast.mods.money.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.CommandTreeBase;

/**
 * @author LatvianModder
 */
public class CommandShop extends CommandTreeBase
{
	public CommandShop()
	{
		addSubcommand(new CommandShopGui());
		addSubcommand(new CommandShopAdd());
	}

	@Override
	public String getName()
	{
		return "shop";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.ftbmoney.shop.usage";
	}

	@Override
	public int getRequiredPermissionLevel()
	{
		return 0;
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender)
	{
		return true;
	}
}