package christmasMod.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;

import basemod.abstracts.CustomCard;
import christmasMod.powers.GiftDamagedPower;
import testmod.utils.MiscMethods;

public abstract class AbstractChristmasCard extends CustomCard {
	public static final String ID_PREFIX = "christmas-";
    public static final String IMG = "christmasResources/images/relic0.png";
	
	protected static CardStrings Strings(String ID) {
		return CardCrawlGame.languagePack.getCardStrings(ID_PREFIX + ID);
	}
    
	public AbstractChristmasCard(String id, String name, int cost, String description, CardType type,
			CardTarget target) {
		super(ID_PREFIX + id, name, IMG, cost, description, type, CardColor.COLORLESS, CardRarity.SPECIAL, target);
	}

	public AbstractCard makeCopy() {
		AbstractCard tmp = super.makeCopy();
		if (MiscMethods.MISC.inCombat() && GiftDamagedPower.hasThis()) {
			GiftDamagedPower.getThis().onCardDraw(tmp);
		}
		return tmp;
	}
	
}
