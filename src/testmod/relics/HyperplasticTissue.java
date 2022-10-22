package testmod.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import testmod.mymod.TestMod;
import testmod.utils.HandSizeCounterUpdater;

public class HyperplasticTissue extends AbstractTestRelic implements HandSizeCounterUpdater {
	private int delta = 0;
	
	public void onCardDraw(AbstractCard c) {
		if (c.type == CardType.STATUS || c.type == CardType.CURSE) {
			this.updateHandSize(1);
			this.delta++;
		}
	}
	
	public void onEquip() {
		this.updateHandSize(1);
		this.delta = 0;
		TestMod.setActivity(this);
		if (this.inCombat() && this.isActive) {
			this.atPreBattle();
		}
	}
	
	public void onUnequip() {
		this.updateHandSize(-(this.delta + 1));
	}
	
	public void atPreBattle() {
		this.delta = 0;
		this.updateHandSize(0);
	}
	
	public void onVictory() {
		this.updateHandSize(-this.delta);
		this.delta = 0;
	}

}