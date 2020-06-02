
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;

import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.*;

import actions.SubstituteBySubterfugeAction;

import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.localization.CardStrings;

public class SubstituteBySubterfuge extends CustomCard {
	public static final String ID = "SubstituteBySubterfuge";
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(TestMod.makeID(ID));
	private static final String NAME = cardStrings.NAME;
	private static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
	public static final String IMG = TestMod.cardIMGPath("relic1");
	private static final String[] E = {" [R] ", " [G] ", " [B] ", " [P] "};
	private static final int COST = 0;

	public SubstituteBySubterfuge() {
		super(TestMod.makeID(ID), NAME, IMG, COST, getDescription(false), CardType.SKILL, CardColor.COLORLESS, CardRarity.RARE, CardTarget.NONE);
		this.exhaust = true;
	}

	public static String getDescription(boolean upgraded) {
		String temp = EXTENDED_DESCRIPTION[0];
		String e = E[0];
		if (AbstractDungeon.player != null) {
			switch (AbstractDungeon.player.chosenClass) {
			case WATCHER:
				e = E[3];
				break;
			case DEFECT:
				e = E[2];
				break;
			case THE_SILENT:
				e = E[1];
			default:
			}
		}
		temp += e + EXTENDED_DESCRIPTION[1];
		if (!upgraded)
			temp += EXTENDED_DESCRIPTION[2];
		return temp;
	}
	
	public void use(final AbstractPlayer p, final AbstractMonster m) {
		AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(1));
		AbstractDungeon.actionManager.addToBottom(new SubstituteBySubterfugeAction(p));
	}

	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.rawDescription = getDescription(true);
			this.initializeDescription();
			this.exhaust = false;
		}
	}
}