package com.feed_the_beast.mods.money.command;

import com.feed_the_beast.mods.money.FTBMoney;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
public class CommandPay extends CommandBase
{
	@Override
	public String getName()
	{
		return "pay";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.ftbmoney.pay.usage";
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
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if (args.length == 0)
		{
			return Collections.emptyList();
		}
		else if (isUsernameIndex(args, args.length - 1))
		{
			return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return index == 0;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length < 2)
		{
			throw new WrongUsageException(getUsage(sender));
		}

		EntityPlayerMP senderPlayer = getCommandSenderAsPlayer(sender);
		long money = FTBMoney.getMoney(senderPlayer);
		long pay = parseLong(args[1], 1L, money);

		EntityPlayerMP player = getPlayer(server, sender, args[0]);

		ITextComponent senderName = sender.getDisplayName();
		senderName.getStyle().setColor(TextFormatting.BLUE);

		ITextComponent playerName = player.getDisplayName();
		playerName.getStyle().setColor(TextFormatting.BLUE);

		FTBMoney.setMoney(senderPlayer, money - pay);
		FTBMoney.setMoney(player, FTBMoney.getMoney(player) + pay);

		ITextComponent component = new TextComponentString("").appendSibling(senderName).appendText(" > ").appendSibling(playerName).appendText(" ").appendSibling(FTBMoney.moneyComponent(pay));
		player.sendMessage(component);
		sender.sendMessage(component);
	}
}