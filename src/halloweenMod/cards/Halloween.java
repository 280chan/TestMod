package halloweenMod.cards;

import java.util.ArrayList;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.MultiCardPreview;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.localization.CardStrings;
import halloweenMod.mymod.HalloweenMod;

public class Halloween extends AbstractCard {
	public static final String ID = HalloweenMod.MOD_PREFIX + "Halloween";
	public static final String IMG = "green/skill/hide";
	private static CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
	private static final String E = "[E] ";
	private static final int COST = 0;
	private static final int BASE_MGC = 1;
	private static final ArrayList<Halloween> PREVIEW = new ArrayList<Halloween>();
	
	public Halloween() {
		this(false);
		MultiCardPreview.add(this, getPreview(1), getPreview(2));
	}
	
	private Halloween(boolean noPreview) {
		super(ID, NAME, IMG, IMG, COST, getDescription(), CardType.SKILL, CardColor.COLORLESS, CardRarity.SPECIAL,
				CardTarget.SELF);
		this.baseMagicNumber = BASE_MGC;
		this.magicNumber = this.baseMagicNumber;
		this.exhaust = true;
	}
	
	private Halloween getPreview(int n) {
		int old = PREVIEW.size();
		for (int i = old; i < n; i++) {
			Halloween tmp = new Halloween(true);
			for (int j = 0; j < i + 1; j++) {
				tmp.upgrade();
			}
			PREVIEW.add(tmp);
		}
		return PREVIEW.get(n - 1);
	}
	
	private static String getDescription() {
		return getDescription(BASE_MGC);
	}
	
	private static String getDescription(int magic) {
		String temp = EXTENDED_DESCRIPTION[0] + " ";
		if (magic < 4)
			for (int i = 0; i < magic; i++)
				temp += E;
		else
			temp += "!M! " + E;
		if (magic > 1 && EXTENDED_DESCRIPTION.length > 2)
			return temp + EXTENDED_DESCRIPTION[2];
		else
			return temp + EXTENDED_DESCRIPTION[1];
	}

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.addToBot(new ApplyPowerAction(p, p, new DexterityPower(p, this.magicNumber), this.magicNumber));
		this.addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, this.magicNumber), this.magicNumber));
		this.addToBot(new GainEnergyAction(this.magicNumber));
		this.addToBot(new DrawCardAction(p, this.magicNumber));
	}
	
	public AbstractCard makeCopy() {
		return new Halloween();
	}

	public boolean canUpgrade() {
		return true;
	}
	
	public void upgrade() {
		this.timesUpgraded += 1;
		this.upgraded = true;
		this.upgradeMagicNumber(1);
		this.rawDescription = getDescription(this.magicNumber);
		this.name = (NAME + "+" + this.timesUpgraded);
		this.initializeDescription();
		this.initializeTitle();
		ArrayList<AbstractCard> l = MultiCardPreview.multiCardPreview.get(this);
		if (!PREVIEW.contains(this) && l != null && !l.isEmpty())
			l.add(getPreview(l.remove(0).timesUpgraded + 2));
	}
}