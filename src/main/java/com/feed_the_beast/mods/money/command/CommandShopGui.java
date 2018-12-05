package com.feed_the_beast.mods.money.command;

import com.feed_the_beast.mods.money.net.MessageOpenShop;
import com.feed_the_beast.mods.money.shop.Shop;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

/**
 * @author LatvianModder
 */
public class CommandShopGui extends CommandBase
{
	@Override
	public String getName()
	{
		return "gui";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.ftbmoney.shop.gui.usage";
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
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		new MessageOpenShop(Shop.SERVER).sendTo(player);
	}
}