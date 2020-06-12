
package cards.colorless;

import cards.AbstractTestCard;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.EnergizedPower;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.actions.common.*;

public class BackupPower extends AbstractTestCard {
	public static final String ID = "BackupPower";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
	private static final String[] E = { " [R]", " [G]", " [B]", " [W]" };
	private static final int COST = 1;
	private static final int BASE_MGC = 2;

	public BackupPower() {
		super(ID, NAME, COST, getDescription(BASE_MGC), CardType.SKILL, CardRarity.RARE, CardTarget.NONE);
		this.magicNumber = this.baseMagicNumber = BASE_MGC;
	}

	public static String getDescription(int mgc) {
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
		if (mgc < 4 && mgc > 0) {
			for (int i = 0; i < mgc; i++)
				temp += e;
		} else
			temp += mgc + e;
		return temp + EXTENDED_DESCRIPTION[1] + e + EXTENDED_DESCRIPTION[2];
	}

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.addToBot(new GainEnergyAction(this.magicNumber));
	}

	public void triggerOnEndOfPlayerTurn() {
		AbstractPlayer p = AbstractDungeon.player;
		this.addToBot(new ApplyPowerAction(p, p, new EnergizedPower(p, 1), 1));
		super.triggerOnEndOfPlayerTurn();
	}

	public void upgradeMagicNumber(int amount) {
		super.upgradeMagicNumber(amount);
		this.rawDescription = getDescription(this.magicNumber);
		this.initializeDescription();
	}
	
	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upgradeMagicNumber(1);
		}
	}
}