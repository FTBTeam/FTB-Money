package com.feed_the_beast.mods.money;

import com.feed_the_beast.ftblib.events.FTBLibEvent;
import com.feed_the_beast.mods.money.shop.ShopEntry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * @author LatvianModder
 */
@Cancelable
public class EventShopLock extends FTBLibEvent
{
	private final ShopEntry entry;
	private final EntityPlayerMP player;

	public EventShopLock(ShopEntry e, EntityPlayerMP p)
	{
		entry = e;
		player = p;
	}

	public ShopEntry getEntry()
	{
		return entry;
	}

	public String getLock()
	{
		return entry.lock;
	}

	public EntityPlayerMP getPlayer()
	{
		return player;
	}
}