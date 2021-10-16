package dev.ftb.mods.ftbmoney.net;

import dev.ftb.mods.ftbmoney.FTBMoney;
import me.shedaniel.architectury.networking.simple.MessageType;
import me.shedaniel.architectury.networking.simple.SimpleNetworkManager;

/**
 * @author LatvianModder
 */
public interface FTBMoneyNetHandler {
	SimpleNetworkManager NET = SimpleNetworkManager.create(FTBMoney.MOD_ID);

	MessageType UPDATE_MONEY = NET.registerS2C("update_money", UpdateMoneyMessage::new);
	MessageType SYNC_SHOP = NET.registerS2C("sync_shop", SyncShopMessage::new);
	MessageType BUY = NET.registerC2S("buy", BuyMessage::new);
	MessageType ADD_SHOP_TAB = NET.registerC2S("add_shop_tab", AddShopTabMessage::new);
	MessageType EDIT_SHOP_TAB = NET.registerC2S("edit_shop_tab", EditShopTabMessage::new);
	MessageType MOVE_SHOP_TAB = NET.registerC2S("move_shop_tab", MoveShopTabMessage::new);
	MessageType ADD_SHOP_ENTRY = NET.registerC2S("add_shop_entry", AddShopEntryMessage::new);
	MessageType EDIT_SHOP_ENTRY = NET.registerC2S("edit_shop_entry", EditShopEntryMessage::new);

	static void init() {
	}
}