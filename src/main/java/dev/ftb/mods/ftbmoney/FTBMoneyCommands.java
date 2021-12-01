package dev.ftb.mods.ftbmoney;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import dev.ftb.mods.ftbteams.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.data.PlayerTeam;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.Collections;

public class FTBMoneyCommands {
	public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("ftbmoney")
				.then(Commands.literal("balance")
						.executes(context -> balance(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException().getGameProfile())))
						.then(Commands.argument("player", GameProfileArgument.gameProfile())
								.requires(source -> source.hasPermission(2))
								.executes(context -> balance(context.getSource(), GameProfileArgument.getGameProfiles(context, "player")))
						)
				)
				.then(Commands.literal("pay")
						.then(Commands.argument("player", EntityArgument.player())
								.then(Commands.argument("money", LongArgumentType.longArg(1L))
										.executes(context -> pay(context.getSource(), context.getSource().getPlayerOrException(), EntityArgument.getPlayer(context, "player"), LongArgumentType.getLong(context, "money")))
								)
						)
				)
				.then(Commands.literal("set")
						.requires(source -> source.hasPermission(2))
						.then(Commands.argument("player", EntityArgument.players())
								.then(Commands.argument("money", LongArgumentType.longArg(0L))
										.executes(context -> set(context.getSource(), EntityArgument.getPlayers(context, "player"), LongArgumentType.getLong(context, "money")))
								)
						)
				)
				.then(Commands.literal("add")
						.requires(source -> source.hasPermission(2))
						.then(Commands.argument("player", EntityArgument.players())
								.then(Commands.argument("money", LongArgumentType.longArg())
										.executes(context -> add(context.getSource(), EntityArgument.getPlayers(context, "player"), LongArgumentType.getLong(context, "money")))
								)
						)
				)
		);
	}

	private static int balance(CommandSourceStack source, Collection<GameProfile> profiles) {
		for (GameProfile profile : profiles) {
			PlayerTeam team = FTBTeamsAPI.getManager().getInternalPlayerTeam(profile.getId());
			source.sendSuccess(new TextComponent(profile.getName() + ": ").append(FTBMoney.moneyComponent(team.getExtraData().getLong("Money"))), false);
		}

		return profiles.size();
	}

	private static int pay(CommandSourceStack source, ServerPlayer from, ServerPlayer to, long money) {
		source.sendFailure(new TextComponent("WIP!"));
		return 1;
	}

	private static int set(CommandSourceStack source, Collection<ServerPlayer> players, long money) {
		for (ServerPlayer player : players) {
			source.sendSuccess(new TextComponent(player.getScoreboardName() + ": ").append(FTBMoney.moneyComponent(money)), false);
			FTBMoney.setMoney(player, money);
		}

		return players.size();
	}

	private static int add(CommandSourceStack source, Collection<ServerPlayer> players, long money) {
		if (money == 0L) {
			return 0;
		}

		for (ServerPlayer player : players) {
			source.sendSuccess(new TextComponent(player.getScoreboardName() + (money > 0L ? ": +" : ": -")).append(FTBMoney.moneyComponent(Math.abs(money))), false);
			FTBMoney.addMoney(player, money);
		}

		return players.size();
	}
}
