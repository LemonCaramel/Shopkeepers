package com.nisovin.shopkeepers.internals;

import org.bukkit.inventory.ItemStack;

import com.nisovin.shopkeepers.api.internal.ApiInternals;
import com.nisovin.shopkeepers.api.shopkeeper.offers.BookOffer;
import com.nisovin.shopkeepers.api.shopkeeper.offers.PriceOffer;
import com.nisovin.shopkeepers.api.shopkeeper.offers.TradeOffer;
import com.nisovin.shopkeepers.api.util.UnmodifiableItemStack;
import com.nisovin.shopkeepers.shopkeeper.offers.SKBookOffer;
import com.nisovin.shopkeepers.shopkeeper.offers.SKPriceOffer;
import com.nisovin.shopkeepers.shopkeeper.offers.SKTradeOffer;
import com.nisovin.shopkeepers.util.inventory.SKUnmodifiableItemStack;

/**
 * Implementation of {@link ApiInternals}.
 */
public class SKApiInternals implements ApiInternals {

	public SKApiInternals() {
	}

	// FACTORIES

	@Override
	public UnmodifiableItemStack createUnmodifiableItemStack(ItemStack itemStack) {
		return SKUnmodifiableItemStack.of(itemStack);
	}

	@Override
	public PriceOffer createPriceOffer(ItemStack item, int price) {
		return new SKPriceOffer(item, price);
	}

	@Override
	public PriceOffer createPriceOffer(UnmodifiableItemStack item, int price) {
		return new SKPriceOffer(item, price);
	}

	@Override
	public TradeOffer createTradeOffer(ItemStack resultItem, ItemStack item1, ItemStack item2) {
		return new SKTradeOffer(resultItem, item1, item2);
	}

	@Override
	public TradeOffer createTradeOffer(UnmodifiableItemStack resultItem, UnmodifiableItemStack item1, UnmodifiableItemStack item2) {
		return new SKTradeOffer(resultItem, item1, item2);
	}

	@Override
	public BookOffer createBookOffer(String bookTitle, int price) {
		return new SKBookOffer(bookTitle, price);
	}
}
