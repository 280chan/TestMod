package testmod.cards.colorless;

import testmod.cards.AbstractTestCard;
import testmod.mymod.TestMod;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.AttackDamageRandomEnemyAction;

public class Mystery extends AbstractTestCard {
	private static final int BASE_MGC = 1;

	private static int countMystery() {
		return (int) CardCrawlGame.metricData.path_taken.stream().filter("?"::equals).count();
	}
	
	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.applyCount();
		TestMod.info("迷：当前卡M = " + this.magicNumber);
		for (int i = 0; i < this.magicNumber; i++) {
			this.addToBot(new AttackDamageRandomEnemyAction(this, AttackEffect.SLASH_HORIZONTAL));
		}
	}
	
	private void applyCount() {
		if (this.magicNumber != countMystery() + BASE_MGC)
			this.upgradeMagicNumber(BASE_MGC - this.magicNumber + countMystery());
	}
	
	public void calculateCardDamage(AbstractMonster m) {
		this.applyCount();
		super.calculateCardDamage(m);
	}
	
	public void applyPowers() {
		this.applyCount();
		super.applyPowers();
	}

	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upgradeDamage(2);
		}
	}
}