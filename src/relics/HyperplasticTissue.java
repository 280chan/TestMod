package relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.helpers.PowerTip;
import basemod.BaseMod;

public class HyperplasticTissue extends AbstractTestRelic {
	public static final String ID = "HyperplasticTissue";
	private static int max;
	
	public HyperplasticTissue() {
		super(ID, RelicTier.COMMON, LandingSound.SOLID);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	public void onCardDraw(AbstractCard c) {
		if (c.type == CardType.STATUS || c.type == CardType.CURSE) {
			this.counter = ++BaseMod.MAX_HAND_SIZE;
		}
    }
	
	public void onEquip() {
		this.counter = ++BaseMod.MAX_HAND_SIZE;
    }
	
	public void onUnequip() {
		BaseMod.MAX_HAND_SIZE = --max;
    }
	
	public void atPreBattle() {
		max = this.counter = BaseMod.MAX_HAND_SIZE;
    }
	
	public void onVictory() {
		BaseMod.MAX_HAND_SIZE = this.counter = max;
    }

}