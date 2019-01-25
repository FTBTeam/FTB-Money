package com.feed_the_beast.mods.money.integration.jei;

import com.feed_the_beast.ftbquests.item.FTBQuestsItems;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;

/**
 * @author LatvianModder
 */
@JEIPlugin
public class FTBMoneyJEIIntegration implements IModPlugin
{
	public static IJeiRuntime RUNTIME;

	@Override
	public void onRuntimeAvailable(IJeiRuntime r)
	{
		RUNTIME = r;
	}

	@Override
	public void register(IModRegistry r)
	{
		r.handleRecipes(ShopWrapper.class, recipe -> recipe, ShopCategory.UID);
		r.addRecipeCatalyst(new ItemStack(FTBQuestsItems.BOOK), ShopCategory.UID);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration r)
	{
		r.addRecipeCategories(new ShopCategory(r.getJeiHelpers().getGuiHelper()));
	}
}