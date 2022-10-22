
package halloweenMod.cards;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.ApotheosisAction;
import com.megacrit.cardcrawl.actions.unique.ArmamentsAction;
import halloweenMod.mymod.HalloweenMod;
import testmod.utils.Festival;

public class Candy extends AbstractCard implements Festival {
	public static final String ID = HalloweenMod.MOD_PREFIX + "Candy";
	public static final String IMG = "blue/power/creativeAi";
	private static CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
	private static final int COST = 1;
	private static final int BASE_MGC = 4;
	
	public Candy() {
		super(ID, NAME, IMG, IMG, COST, DESCRIPTION, CardType.SKILL, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.SELF);
		this.baseMagicNumber = BASE_MGC;
		this.magicNumber = this.baseMagicNumber;
	}

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.addToBot(new HealAction(p, p, this.magicNumber));
		if (this.upgraded) {
			this.addToBot(new ApotheosisAction());
		} else {
			this.addToBot(new ArmamentsAction(true));
		}
	}

	public AbstractCard makeCopy() {
		return new Candy();
	}

	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();//改名，其实就是多个+
			this.rawDescription = UPGRADE_DESCRIPTION;
			this.initializeDescription();
		}
	}
}