package testmod.relicsup;

import java.util.stream.Stream;

import com.megacrit.cardcrawl.actions.common.GainGoldAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardColor;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.ShopRoom;

import testmod.relics.RetroFilter;

public class RetroFilterUp extends AbstractUpgradedRelic {
	public static final int ORIGINAL = 100;
	
	public static Stream<RetroFilterUp> getThis() {
		return MISC.relicStream(RetroFilterUp.class);
	}
	
	private boolean check(AbstractCard c) {
		return c.color == CardColor.COLORLESS;
	}
	
	private int amount() {
		return (int) p().masterDeck.group.stream().filter(this::check).count();
	}
	
	public void onUseCard(AbstractCard c, UseCardAction action) {
		if (check(c)) {
			this.atb(new GainGoldAction(amount()));
		}
	}

	public void onPreviewObtainCard(AbstractCard c) {
		c.price = check(c) ? 0 : Math.max(RetroFilter.DEFAULT_RATE - amount(), 0) * c.price / ORIGINAL;
	}
	
	public void onEnterRoom(AbstractRoom room) {
		if ((room instanceof ShopRoom)) {
			this.flash();
			this.pulse = true;
		} else {
			this.pulse = false;
		}
	}

}