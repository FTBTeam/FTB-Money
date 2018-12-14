package com.feed_the_beast.mods.money.command;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.math.MathUtils;
import com.feed_the_beast.mods.money.shop.Shop;
import com.feed_the_beast.mods.money.shop.ShopEntry;
import com.feed_the_beast.mods.money.shop.ShopTab;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class CommandImportItemsFromChest extends CommandBase
{
	@Override
	public String getName()
	{
		return "import_shop_items_from_chest";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.ftbmoney.import_shop_items_from_chest.usage";
	}

	@Override
	public int getRequiredPermissionLevel()
	{
		return 2;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if (args.length == 3)
		{
			return getListOfStringsMatchingLastWord(args, "true", "false");
		}
		else if (args.length == 1)
		{
			List<String> list = new ArrayList<>(Shop.SERVER.tabs.size());

			for (ShopTab tab : Shop.SERVER.tabs)
			{
				list.add(tab.title.replace(" ", "").toLowerCase());
			}

			return getListOfStringsMatchingLastWord(args, list);
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);

		if (args.length < 1)
		{
			throw new WrongUsageException(getUsage(sender));
		}

		ShopTab tab = Shop.SERVER.getTab(args[0]);

		if (tab == null)
		{
			throw FTBLib.error(sender, "commands.ftbmoney.import_shop_items_from_chest.invalid_id", args[0]);
		}

		long price = args.length >= 2 ? parseLong(args[1], 0L, Long.MAX_VALUE) : 1L;
		boolean replace = args.length >= 3 && parseBoolean(args[2]);

		RayTraceResult ray = MathUtils.rayTrace(player, false);

		if (ray.typeOfHit == RayTraceResult.Type.BLOCK)
		{
			TileEntity tileEntity = player.world.getTileEntity(ray.getBlockPos());

			if (tileEntity != null)
			{
				IItemHandler handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, ray.sideHit);

				if (handler != null)
				{
					if (replace)
					{
						tab.entries.clear();
					}

					int r = 0;

					for (int i = 0; i < handler.getSlots(); i++)
					{
						ItemStack stack = handler.getStackInSlot(i);

						if (!stack.isEmpty())
						{
							ShopEntry entry = new ShopEntry(tab);
							entry.stack = stack.copy();
							entry.buy = price;
							tab.entries.add(entry);
							r++;
						}
					}

					Shop.SERVER.markDirty();
					sender.sendMessage(new TextComponentTranslation("commands.ftbmoney.import_shop_items_from_chest.text", r, tab.title));
				}
			}
		}
	}
}