
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;
import powers.PulseDistributorPower;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.*;

public class PulseDistributor extends CustomCard {
	public static final String ID = "PulseDistributor";
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(TestMod.makeID(ID));
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
	public static final String IMG = TestMod.cardIMGPath("relic1");
	private static final int COST = 3;
	private static final int INITIAL_MAGIC = 1;

	public PulseDistributor() {
		super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.POWER, CardColor.COLORLESS, CardRarity.RARE, CardTarget.SELF);
		this.magicNumber = this.baseMagicNumber = INITIAL_MAGIC;
		this.updatedDescription();
	}

	private void updatedDescription() {
		String temp = EXTENDED_DESCRIPTION[0];
		if (this.magicNumber != 0) {
			if (this.magicNumber > 0) {
				temp += "+";
			}
			temp += this.magicNumber;
		}
		this.rawDescription = temp + EXTENDED_DESCRIPTION[1];
		this.initializeDescription();
	}

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		if (p.hasPower(PulseDistributorPower.POWER_ID)) {
			PulseDistributorPower power = (PulseDistributorPower)p.getPower(PulseDistributorPower.POWER_ID);
			if (power.magic > this.magicNumber) {
				AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(p, p, PulseDistributorPower.POWER_ID));
				AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new PulseDistributorPower(p, this.magicNumber, power.DAMAGES)));
			}
		} else {
			AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new PulseDistributorPower(p, this.magicNumber)));
		}
	}

	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upgradeMagicNumber(-1);
			this.updatedDescription();
		}
	}
}