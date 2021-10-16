package dev.ftb.mods.ftbmoney.gui;

import dev.ftb.mods.ftbmoney.shop.ShopEntry;

import java.util.Comparator;

/**
 * @author LatvianModder
 */
public enum SortType {
	PRICE_H_L("price_h_l", (o1, o2) -> {
		int i = Long.compare(o2.buy, o1.buy);
		return i == 0 ? compareNamesAZ(o1, o2) : i;
	}),
	PRICE_L_H("price_l_h", (o1, o2) -> {
		int i = Long.compare(o1.buy, o2.buy);
		return i == 0 ? compareNamesAZ(o1, o2) : i;
	}),
	NAME_A_Z("name_a_z", SortType::compareNamesAZ),
	NAME_Z_A("name_z_a", SortType::compareNamesZA);

	public static SortType sort = SortType.PRICE_H_L;

	public static int compareNamesAZ(ShopEntry o1, ShopEntry o2) {
		return o1.stack.getHoverName().getString().compareToIgnoreCase(o2.stack.getHoverName().getString());
	}

	public static int compareNamesZA(ShopEntry o1, ShopEntry o2) {
		return -compareNamesAZ(o2, o1);
	}

	public static final SortType[] VALUES = values();

	public final String name;
	public final Comparator<ShopEntry> comparator;

	SortType(String n, Comparator<ShopEntry> c) {
		name = n;
		comparator = c;
	}
}