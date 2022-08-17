package testmod.relics;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;

import testmod.powers.AbstractTestPower;
import testmod.relicsup.GoldenContractUp;

import com.megacrit.cardcrawl.cards.DamageInfo;

public class GoldenContract extends AbstractTestRelic {
	
	public GoldenContract() {
		this.counter = 0;
	}
	
	public String getUpdatedDescription() {
		if (!this.isObtained || this.counter < 0)
			return DESCRIPTIONS[0];
		return DESCRIPTIONS[0] + DESCRIPTIONS[1] + (counter < 2 ? 5 * (10 - counter)
				: (((int) (5000 * Math.pow(0.9, counter))) / 100.0)) + DESCRIPTIONS[2];
	}
	
	private int updateCounter() {
		int tmp = this.counter++;
		this.updateDescription();
		return tmp;
	}
	
	public void onUseCard(final AbstractCard c, final UseCardAction a) {
		if (c.type == CardType.ATTACK && this.relicStream(GoldenContractUp.class).count() == 0) {
			if (p().gold > 0)
				this.stupidDevToBot(() -> p().loseGold(p().gold < 20 ? 1 : p().gold / 20));
			this.show();
		}
	}
	
	public void atPreBattle() {
		if (this.isActive && p().powers.stream().noneMatch(p -> p instanceof GoldenContractPower))
			p().powers.add(new GoldenContractPower());
    }
	
	public double gainGold(double amount) {
		return this.relicStream(GoldenContractUp.class).count() > 0 ? amount
				: 0.5 * (amount > 0 ? amount * Math.pow(0.9, updateCounter()) : amount);
	}
	
	public static class GoldenContractPower extends AbstractTestPower implements InvisiblePower {
		public GoldenContractPower() {
			this.owner = p();
			this.type = PowerType.BUFF;
			this.updateDescription();
			this.addMap(p -> new GoldenContractPower());
		}
		
		public void updateDescription() {
			this.description = "";
		}

		private int count() {
			return (int) (relicStream(GoldenContract.class).count() + relicStream(GoldenContractUp.class).count());
		}
		
		public float atDamageGive(float damage, DamageInfo.DamageType type) {
			return (type == DamageInfo.DamageType.NORMAL && p().gold > 0) ? damage + p().gold * count() : damage;
		}
	}

}