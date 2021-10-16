package dev.ftb.mods.ftbmoney.net;

import dev.ftb.mods.ftbmoney.shop.Shop;
import dev.ftb.mods.ftbmoney.shop.ShopEntry;
import dev.ftb.mods.ftbmoney.shop.ShopTab;
import dev.ftb.mods.ftbquests.util.NetUtils;
import me.shedaniel.architectury.networking.NetworkManager;
import me.shedaniel.architectury.networking.simple.BaseC2SMessage;
import me.shedaniel.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

/**
 * @author LatvianModder
 */
public class EditShopEntryMessage extends BaseC2SMessage {
	private final int tab;
	private final int id;
	private final CompoundTag nbt;

	public EditShopEntryMessage(ShopEntry e, boolean delete) {
		tab = e.tab.getIndex();
		id = e.getIndex();
		nbt = delete ? null : e.serializeNBT();
	}

	public EditShopEntryMessage(FriendlyByteBuf buf) {
		tab = buf.readVarInt();
		id = buf.readVarInt();
		nbt = buf.readAnySizeNbt();
	}

	@Override
	public MessageType getType() {
		return FTBMoneyNetHandler.EDIT_SHOP_ENTRY;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(tab);
		buf.writeVarInt(id);
		buf.writeNbt(nbt);
	}

	@Override
	public void handle(NetworkManager.PacketContext context) {
		if (NetUtils.canEdit(context)) {
			ShopTab t = Shop.SERVER.tabs.get(tab);

			if (nbt == null) {
				t.entries.remove(id);
			} else {
				t.entries.get(id).deserializeNBT(nbt);
			}

			t.shop.markDirty();
			new SyncShopMessage(t.shop.serializeNBT()).sendToAll(context.getPlayer().getServer());
		}
	}
}