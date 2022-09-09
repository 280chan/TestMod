package christmasMod.relics;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import christmasMod.mymod.ChristmasMod;
import testmod.relics.AbstractTestRelic;

public class ChristmasGift extends AbstractTestRelic {
	
	public void onExhaust(final AbstractCard card) {
		p().heal(1);
		this.flash();
    }
	
	public void atTurnStart() {
		this.atb(new MakeTempCardInHandAction(ChristmasMod.randomGift(false)));
    }
	
}