package cards.colorless;

import cards.AbstractTestCard;
import powers.AssimilatedRunePower;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.localization.CardStrings;

public class AssimilatedRune extends AbstractTestCard {
	public static final String ID = "AssimilatedRune";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final String UPGRADED_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
	private static final int COST = 1;
	private static final int BASE_MGC = 1;

	public AssimilatedRune() {
		super(ID, NAME, COST, DESCRIPTION, CardType.SKILL, CardRarity.RARE, CardTarget.SELF);
		this.magicNumber = this.baseMagicNumber = BASE_MGC;
	}

	public void use(final AbstractPlayer player, final AbstractMonster m) {
		this.addTmpActionToBot(() -> {
			AssimilatedRunePower p = new AssimilatedRunePower(player, this.magicNumber, this.upgraded);
			if (player.hasPower(p.ID)) {
				p = (AssimilatedRunePower) player.getPower(p.ID);
				p.stackPower(this.magicNumber);
				p.updateDescription();
			} else {
				player.powers.add(0, p);
			}
		});
	}

	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.rawDescription = UPGRADED_DESCRIPTION;
			this.initializeDescription();
		}
	}
}