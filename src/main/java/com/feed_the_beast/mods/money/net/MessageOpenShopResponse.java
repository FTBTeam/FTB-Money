package com.feed_the_beast.mods.money.net;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.mods.money.FTBMoney;
import com.feed_the_beast.mods.money.gui.GuiShop;
import com.feed_the_beast.mods.money.shop.Shop;
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

	public MessageOpenShopResponse()
	{
	}

	public MessageOpenShopResponse(EntityPlayerMP player)
	{
		nbt = Shop.SERVER.serializeNBT();
		canEdit = PermissionAPI.hasPermission(player, FTBMoney.PERM_EDIT_SHOP);
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
	}

	@Override
	public void readData(DataIn data)
	{
		nbt = data.readNBT();
		canEdit = data.readBoolean();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onMessage()
	{
		Shop shop = new Shop();
		shop.deserializeNBT(nbt);
		new GuiShop(shop, canEdit).openGui();
	}
}