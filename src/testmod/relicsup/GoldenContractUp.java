package testmod.relicsup;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import testmod.relics.GoldenContract.GoldenContractPower;
import testmod.utils.CounterKeeper;

public class GoldenContractUp extends AbstractUpgradedRelic implements CounterKeeper {
	
	public GoldenContractUp() {
		this.counter = 0;
	}
	
	public String getUpdatedDescription() {
		if (!this.isObtained || this.counter < 0)
			return DESCRIPTIONS[0];
		return DESCRIPTIONS[0] + DESCRIPTIONS[1] + Math.pow(1.001, counter) * 100 + DESCRIPTIONS[2];
	}
	
	private int updateCounter(int delta) {
		int tmp = this.counter;
		this.counter++;
		this.updateDescription();
		return tmp;
	}
	
	public void onUseCard(final AbstractCard c, final UseCardAction a) {
		if (c.type == CardType.ATTACK) {
			if (p().gold > 0)
				this.stupidDevToBot(() -> p().loseGold(p().gold < 100 ? 1 : p().gold / 100));
			this.show();
		}
	}
	
	public void atPreBattle() {
		if (this.isActive && p().powers.stream().noneMatch(p -> p instanceof GoldenContractPower))
			p().powers.add(new GoldenContractPower());
    }
	
	public double gainGold(double amount) {
		return amount * Math.pow(1.001, this.updateCounter(1));
	}

}