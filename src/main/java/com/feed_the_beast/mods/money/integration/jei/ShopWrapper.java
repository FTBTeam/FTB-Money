package com.feed_the_beast.mods.money.integration.jei;

import com.feed_the_beast.mods.money.FTBMoney;
import com.feed_the_beast.mods.money.shop.ShopEntry;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ShopWrapper implements IRecipeWrapper
{
	public final ShopEntry entry;

	public ShopWrapper(ShopEntry e)
	{
		entry = e;
	}

	@Override
	public void getIngredients(IIngredients ingredients)
	{
		ingredients.setOutput(VanillaTypes.ITEM, entry.stack);
	}

	@Override
	public void drawInfo(Minecraft mc, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
	{
		mc.fontRenderer.drawString(I18n.format("ftbmoney.shop.entry.buy") + ":", 20, 1, 0xFF222222);
		mc.fontRenderer.drawString(I18n.format("ftbmoney.shop.entry.sell") + ":", 20, 10, 0xFF222222);

		String bs = FTBMoney.moneyString(entry.buy);
		String ss = FTBMoney.moneyString(entry.sell);

		mc.fontRenderer.drawString(bs, recipeWidth - mc.fontRenderer.getStringWidth(bs), 1, 0xFF222222);
		mc.fontRenderer.drawString(ss, recipeWidth - mc.fontRenderer.getStringWidth(ss), 10, 0xFF222222);
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY)
	{
		if (entry.disabledServer && !Minecraft.getMinecraft().isSingleplayer())
		{
			return Collections.singletonList(TextFormatting.RED + I18n.format("ftbmoney.shop.entry.disabled_server"));
		}

		return Collections.emptyList();
	}
}