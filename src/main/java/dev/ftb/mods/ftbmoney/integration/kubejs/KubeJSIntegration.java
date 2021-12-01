package dev.ftb.mods.ftbmoney.integration.kubejs;

import dev.ftb.mods.ftbmoney.FTBMoney;
import dev.latvian.kubejs.player.ServerPlayerJS;
import dev.latvian.kubejs.KubeJSPlugin;
import dev.latvian.kubejs.script.BindingsEvent;

public class KubeJSIntegration extends KubeJSPlugin
{
	public static class FTBMoneyWrapper {
		public static long getMoney(ServerPlayerJS player) {
			return FTBMoney.getMoney(player.minecraftPlayer);
		}
	
		public static void setMoney(ServerPlayerJS player, Number money) {
			FTBMoney.setMoney(player.minecraftPlayer, money.longValue());
		}
	
		public static void addMoney(ServerPlayerJS player, Number money) {
			FTBMoney.addMoney(player.minecraftPlayer, money.longValue());
		}
	}

	@Override
	public void addBindings(BindingsEvent event) {
		event.add("FTBMoney", FTBMoneyWrapper.class);
	}
}
