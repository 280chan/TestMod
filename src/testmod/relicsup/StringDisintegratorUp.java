package testmod.relicsup;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class StringDisintegratorUp extends AbstractUpgradedRelic {
	public static ArrayList<AbstractCard> CARDS = new ArrayList<AbstractCard>();
	
	public StringDisintegratorUp() {
		super(RelicTier.BOSS, LandingSound.HEAVY);
		this.counter = 0;
	}
	
	public void onEquip() {
		this.addEnergy();
		if (!this.isActive && this.inCombat()) {
			CARDS.clear();
		}
    }
	
	public void onUnequip() {
		this.reduceEnergy();
    }
	
	public void atPreBattle() {
		CARDS.clear();
	}
	
	public void onUseCard(AbstractCard c, UseCardAction a) {
		if (CARDS.contains(c)) {
			this.counter++;
			p().gainGold(1);
			if (this.counter % 50 == 0)
				this.addRandomKey();
		} else {
			CARDS.add(c);
		}
	}
	
}