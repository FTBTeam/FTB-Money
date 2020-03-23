package com.feed_the_beast.mods.money.net;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbquests.client.ClientQuestFile;
import com.feed_the_beast.mods.money.integration.jei.FTBMoneyJEIHelper;
import com.feed_the_beast.mods.money.shop.Shop;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author LatvianModder
 */
public class MessageSyncShop extends MessageToClient
{
	private NBTTagCompound nbt;

	public MessageSyncShop()
	{
	}

	public MessageSyncShop(Shop s)
	{
		nbt = s.serializeNBT();
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBMoneyNetHandler.NET;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeNBT(nbt);
	}

	@Override
	public void readData(DataIn data)
	{
		nbt = data.readNBT();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onMessage()
	{
		Shop.CLIENT = new Shop(() -> ClientQuestFile.INSTANCE);
		Shop.CLIENT.deserializeNBT(nbt);
		FTBMoneyJEIHelper.refresh();
	}
}