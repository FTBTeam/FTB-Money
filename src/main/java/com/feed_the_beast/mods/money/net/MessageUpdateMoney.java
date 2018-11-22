package com.feed_the_beast.mods.money.net;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.mods.money.FTBMoney;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author LatvianModder
 */
public class MessageUpdateMoney extends MessageToClient
{
	private long money;

	public MessageUpdateMoney()
	{
	}

	public MessageUpdateMoney(long m)
	{
		money = m;
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBMoneyNetHandler.NET;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeVarLong(money);
	}

	@Override
	public void readData(DataIn data)
	{
		money = data.readVarLong();
	}

	@SideOnly(Side.CLIENT)
	public void onMessage()
	{
		FTBMoney.setMoney(Minecraft.getMinecraft().player, money);
	}
}