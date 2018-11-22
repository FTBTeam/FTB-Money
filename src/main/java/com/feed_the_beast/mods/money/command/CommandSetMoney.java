package com.feed_the_beast.mods.money.command;

import com.feed_the_beast.mods.money.FTBMoney;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

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

	public int getRequiredPermissionLevel()
	{
		return 2;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP player = getPlayer(server, sender, args[0]);
		long money = FTBMoney.getMoney(player);

		if (args[1].startsWith("~"))
		{
			long add = parseLong(args[1].substring(1), -money, Long.MAX_VALUE);

			if (add != 0L)
			{
				FTBMoney.setMoney(player, money + add);
			}

			ITextComponent name = player.getDisplayName();
			name.getStyle().setColor(TextFormatting.BLUE);
			player.sendMessage(new TextComponentTranslation("commands.ftbmoney.setmoney.add", FTBMoney.moneyComponent(add), name));
		}
		else
		{
			long set = parseLong(args[1], 0L, Long.MAX_VALUE);

			if (set != money)
			{
				FTBMoney.setMoney(player, set);
			}

			ITextComponent name = player.getDisplayName();
			name.getStyle().setColor(TextFormatting.BLUE);
			player.sendMessage(new TextComponentTranslation("commands.ftbmoney.setmoney.set", name, FTBMoney.moneyComponent(set)));
		}
	}
}