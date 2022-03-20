package testmod.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import basemod.BaseMod;
import testmod.mymod.TestMod;

public class HyperplasticTissue extends AbstractTestRelic {
	private int delta = 0;
	
	public HyperplasticTissue() {
		super(RelicTier.COMMON, LandingSound.SOLID);
	}
	
	public void onCardDraw(AbstractCard c) {
		if (c.type == CardType.STATUS || c.type == CardType.CURSE) {
			this.counter = ++BaseMod.MAX_HAND_SIZE;
			this.delta++;
		}
    }
	
	public void onEquip() {
		this.counter = ++BaseMod.MAX_HAND_SIZE;
		this.delta = 0;
		TestMod.setActivity(this);
		if (this.inCombat() && this.isActive) {
			this.atPreBattle();
		}
    }
	
	public void onUnequip() {
		BaseMod.MAX_HAND_SIZE -= this.delta;
    }
	
	public void atPreBattle() {
		this.delta = 0;
		this.counter = BaseMod.MAX_HAND_SIZE;
    }
	
	public void onVictory() {
		this.counter = (BaseMod.MAX_HAND_SIZE -= this.delta);
    }

}