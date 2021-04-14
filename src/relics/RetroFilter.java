package relics;

import com.megacrit.cardcrawl.actions.common.GainGoldAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardColor;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.ShopRoom;

public class RetroFilter extends AbstractTestRelic {
	public static final String ID = "RetroFilter";
	public static final int ORIGINAL_RATE = 120;
	public static final int DEFAULT_RATE = 60;
	
	public static AbstractRelic getThis() {
		for (AbstractRelic r : AbstractDungeon.player.relics)
			if (r instanceof RetroFilter)
				return r;
		return null;
	}
	
	public RetroFilter() {
		super(ID, RelicTier.COMMON, LandingSound.CLINK);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	private static boolean check(AbstractCard c) {
		return c.color == CardColor.COLORLESS;
	}
	
	public void onUseCard(AbstractCard c, UseCardAction action) {
		if (check(c)) {
			this.addToBot(new GainGoldAction(1));
		}
	}
	
	public void onPreviewObtainCard(AbstractCard c) {
		if (check(c)) {
			int rate = DEFAULT_RATE;
			for (AbstractCard t : AbstractDungeon.player.masterDeck.group)
				if (!check(t))
					rate--;
			if (rate < 0)
				rate = 0;
			c.price = c.price * rate / ORIGINAL_RATE;
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