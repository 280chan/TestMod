package relics;

import java.util.stream.Stream;

import com.megacrit.cardcrawl.actions.common.GainGoldAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardColor;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.ShopRoom;

public class RetroFilter extends AbstractTestRelic {
	public static final int ORIGINAL_RATE = 120;
	public static final int DEFAULT_RATE = 60;
	
	public static Stream<RetroFilter> getThis() {
		return INSTANCE.relicStream(RetroFilter.class);
	}
	
	public RetroFilter() {
		super(RelicTier.COMMON, LandingSound.CLINK);
	}
	
	private boolean check(AbstractCard c) {
		return c.color != CardColor.COLORLESS;
	}
	
	public void onUseCard(AbstractCard c, UseCardAction action) {
		if (!check(c)) {
			this.addToBot(new GainGoldAction(1));
		}
	}
	
	public void onPreviewObtainCard(AbstractCard c) {
		if (!check(c)) {
			c.price = c.price
					* Math.max(DEFAULT_RATE - (int) p().masterDeck.group.stream().filter(this::check).count(), 0)
					/ ORIGINAL_RATE;
		}
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