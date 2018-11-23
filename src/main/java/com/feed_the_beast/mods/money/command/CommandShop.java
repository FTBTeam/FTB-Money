package com.feed_the_beast.mods.money.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

/**
 * @author LatvianModder
 */
public class CommandShop extends CommandBase
{
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

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
	{
		sender.sendMessage(new TextComponentString("WIP!"));
	}
}