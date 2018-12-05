package com.feed_the_beast.mods.money.shop;

import com.feed_the_beast.ftblib.lib.math.BlockDimPos;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;

/**
 * @author LatvianModder
 */
public class ShopEntry implements INBTSerializable<NBTTagCompound>
{
	public final ShopTab tab;
	public String page = "";
	public ItemStack stack = ItemStack.EMPTY;
	public long buy = 0L;
	public long sell = 0L;
	public BlockDimPos stock = null;
	public UUID createdBy = null;

	public ShopEntry(ShopTab t)
	{
		tab = t;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("item", stack.serializeNBT());

		if (buy > 0L)
		{
			nbt.setLong("buy", buy);
		}

		if (sell > 0L)
		{
			nbt.setLong("sell", sell);
		}

		if (stock != null)
		{
			nbt.setIntArray("stock", new int[] {stock.posX, stock.posY, stock.posZ, stock.dim});
		}

		if (createdBy != null)
		{
			nbt.setString("created_by", StringUtils.fromUUID(createdBy));
		}

		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		page = nbt.getString("page");
		stack = new ItemStack(nbt.getCompoundTag("item"));
		buy = nbt.getLong("buy");
		sell = nbt.getLong("sell");
		int[] p = nbt.getIntArray("stock");
		stock = p.length == 4 ? new BlockDimPos(p[0], p[1], p[2], p[3]) : null;
		createdBy = StringUtils.fromString(nbt.getString("created_by"));
	}

	public int getIndex()
	{
		return tab.entries.indexOf(this);
	}
}