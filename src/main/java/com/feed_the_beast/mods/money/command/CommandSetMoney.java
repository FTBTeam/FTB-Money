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
public class CommandSetMoney extends CommandBase
{
	@Override
	public String getName()
	{
		return "setmoney";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.ftbmoney.setmoney.usage";
	}

	@Override
	public int getRequiredPermissionLevel()
	{
		return 2;
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

		EntityPlayerMP player = getPlayer(server, sender, args[0]);
		long money = FTBMoney.getMoney(player);

		ITextComponent playerName = sender.getDisplayName();
		playerName.getStyle().setColor(TextFormatting.BLUE);

		if (args[1].startsWith("~"))
		{
			long add = parseLong(args[1].substring(1), -money, Long.MAX_VALUE);

			if (add != 0L)
			{
				FTBMoney.setMoney(player, money + add);
			}

			sender.sendMessage(new TextComponentString("").appendSibling(playerName).appendText(add < 0L ? " - " : " + ").appendSibling(FTBMoney.moneyComponent(Math.abs(add))));

			if (player != sender)
			{
				player.sendStatusMessage(new TextComponentString("").appendSibling(playerName).appendText(add < 0L ? " - " : " + ").appendSibling(FTBMoney.moneyComponent(Math.abs(add))), true);
			}
		}
		else
		{
			long set = parseLong(args[1], 0L, Long.MAX_VALUE);

			if (set != money)
			{
				FTBMoney.setMoney(player, set);
			}

			sender.sendMessage(new TextComponentString("").appendSibling(playerName).appendText(" = ").appendSibling(FTBMoney.moneyComponent(set)));

			if (player != sender)
			{
				player.sendStatusMessage(new TextComponentString("").appendSibling(playerName).appendText(" = ").appendSibling(FTBMoney.moneyComponent(set)), true);
			}
		}
	}
}