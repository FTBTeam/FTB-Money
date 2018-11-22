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

	public int getRequiredPermissionLevel()
	{
		return 0;
	}

	public boolean checkPermission(MinecraftServer server, ICommandSender sender)
	{
		return true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		EntityPlayerMP other = getPlayer(server, sender, args[0]);
		long money = FTBMoney.getMoney(player);
		long pay = parseLong(args[1], 1, money);
		FTBMoney.setMoney(player, money - pay);
		FTBMoney.setMoney(other, FTBMoney.getMoney(other) + pay);

		ITextComponent name = player.getDisplayName();
		name.getStyle().setColor(TextFormatting.BLUE);
		ITextComponent nameOther = other.getDisplayName();
		nameOther.getStyle().setColor(TextFormatting.BLUE);
		player.sendMessage(new TextComponentTranslation("commands.ftbmoney.pay.paid", name, nameOther, FTBMoney.moneyComponent(pay)));
	}
}