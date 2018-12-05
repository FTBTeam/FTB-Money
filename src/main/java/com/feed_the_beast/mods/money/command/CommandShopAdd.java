package com.feed_the_beast.mods.money.command;

import com.feed_the_beast.mods.money.shop.Shop;
import com.feed_the_beast.mods.money.shop.ShopEntry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;

/**
 * @author LatvianModder
 */
public class CommandShopAdd extends CommandBase
{
	@Override
	public String getName()
	{
		return "add";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.ftbmoney.shop.add.usage";
	}

	@Override
	public int getRequiredPermissionLevel()
	{
		return 2;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length < 1)
		{
			throw new WrongUsageException(getUsage(sender));
		}

		EntityPlayerMP player = getCommandSenderAsPlayer(sender);

		ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);

		if (stack.isEmpty())
		{
			throw new CommandException("commands.ftbmoney.shop.add.hand_empty");
		}

		long price = parseLong(args[0], 0L, Long.MAX_VALUE);

		ShopEntry entry = new ShopEntry(Shop.SERVER.tabs.get(Shop.SERVER.tabs.size() - 1));
		entry.netID = ++entry.tab.shop.nextNetID;
		entry.stack = stack.copy();
		entry.buy = price;
		entry.tab.entries.add(entry);
		entry.tab.shop.markDirty();
	}
}