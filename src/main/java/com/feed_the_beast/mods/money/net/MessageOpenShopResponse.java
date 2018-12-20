package com.feed_the_beast.mods.money.net;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbquests.client.ClientQuestFile;
import com.feed_the_beast.mods.money.FTBMoney;
import com.feed_the_beast.mods.money.gui.GuiShop;
import com.feed_the_beast.mods.money.shop.Shop;
import com.feed_the_beast.mods.money.shop.ShopEntry;
import com.feed_the_beast.mods.money.shop.ShopTab;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.server.permission.PermissionAPI;

/**
 * @author LatvianModder
 */
public class MessageOpenShopResponse extends MessageToClient
{
	private NBTTagCompound nbt;
	private boolean canEdit;
	private int[] locked;

	public MessageOpenShopResponse()
	{
	}

	public MessageOpenShopResponse(EntityPlayerMP player)
	{
		nbt = Shop.SERVER.serializeNBT();
		canEdit = PermissionAPI.hasPermission(player, FTBMoney.PERM_EDIT_SHOP);

		IntArrayList list = new IntArrayList();
		int i = 0;

		for (ShopTab tab : Shop.SERVER.tabs)
		{
			for (ShopEntry entry : tab.entries)
			{
				if (!entry.lock.isEmpty() && !entry.isUnlocked(player))
				{
					list.add(i);
				}

				i++;
			}
		}

		locked = list.toIntArray();
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
		data.writeBoolean(canEdit);
		data.writeVarInt(locked.length);

		for (int i : locked)
		{
			data.writeVarInt(i);
		}
	}

	@Override
	public void readData(DataIn data)
	{
		nbt = data.readNBT();
		canEdit = data.readBoolean();
		locked = new int[data.readVarInt()];

		for (int i = 0; i < locked.length; i++)
		{
			locked[i] = data.readVarInt();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onMessage()
	{
		Shop shop = new Shop(ClientQuestFile.INSTANCE);
		shop.deserializeNBT(nbt);
		new GuiShop(shop, canEdit, new IntOpenHashSet(locked)).openGui();
	}
}