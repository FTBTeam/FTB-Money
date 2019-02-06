package com.feed_the_beast.mods.money.gui;

import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.mods.money.shop.ShopEntry;

import java.util.Comparator;

/**
 * @author LatvianModder
 */
public enum EnumSortType
{
	PRICE_H_L("price_h_l", (o1, o2) -> {
		int i = Long.compare(o2.buy, o1.buy);
		return i == 0 ? compareNames(o1, o2) : i;
	}),
	PRICE_L_H("price_l_h", (o1, o2) -> {
		int i = Long.compare(o1.buy, o2.buy);
		return i == 0 ? compareNames(o1, o2) : i;
	}),
	NAME_A_Z("name_a_z", (o1, o2) -> compareNames(o1, o2)),
	NAME_Z_A("name_z_a", (o1, o2) -> compareNames(o2, o1));

	public static int compareNames(ShopEntry o1, ShopEntry o2)
	{
		return StringUtils.unformatted(o1.stack.getDisplayName()).compareToIgnoreCase(StringUtils.unformatted(o2.stack.getDisplayName()));
	}

	public static final EnumSortType[] VALUES = values();

	public final String name;
	public final Comparator<ShopEntry> comparator;

	EnumSortType(String n, Comparator<ShopEntry> c)
	{
		name = n;
		comparator = c;
	}
}