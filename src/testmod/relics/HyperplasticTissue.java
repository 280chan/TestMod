package testmod.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import basemod.BaseMod;
import testmod.mymod.TestMod;
import testmod.utils.HandSizeCounterUpdater;

public class HyperplasticTissue extends AbstractTestRelic implements HandSizeCounterUpdater {
	private int delta = 0;
	
	public HyperplasticTissue() {
		super(RelicTier.COMMON, LandingSound.SOLID);
	}
	
	public void onCardDraw(AbstractCard c) {
		if (c.type == CardType.STATUS || c.type == CardType.CURSE) {
			BaseMod.MAX_HAND_SIZE++;
			this.delta++;
			this.updateHandSize();
		}
    }
	
	public void onEquip() {
		BaseMod.MAX_HAND_SIZE++;
		this.updateHandSize();
		this.delta = 0;
		TestMod.setActivity(this);
		if (this.inCombat() && this.isActive) {
			this.atPreBattle();
		}
    }
	
	public void onUnequip() {
		BaseMod.MAX_HAND_SIZE -= this.delta + 1;
		this.updateHandSize();
    }
	
	public void atPreBattle() {
		this.delta = 0;
		this.updateHandSize();
    }
	
	public void onVictory() {
		BaseMod.MAX_HAND_SIZE -= this.delta;
		this.updateHandSize();
		this.delta = 0;
    }

}