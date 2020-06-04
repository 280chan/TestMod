package cards;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;

import basemod.abstracts.CustomCard;
import mymod.TestMod;

public abstract class AbstractTestCard extends CustomCard {
	
	/**
	 * 无色牌用
	 * @param shortID
	 * @param NAME
	 * @param IMG
	 * @param COST
	 * @param DESCRIPTION
	 * @param type
	 * @param rarity
	 * @param target
	 */
	public AbstractTestCard(String shortID, String NAME, int COST, String DESCRIPTION, CardType type, CardRarity rarity, CardTarget target) {
        super(TestMod.makeID(shortID), NAME, TestMod.cardIMGPath(shortID), COST, DESCRIPTION, type, CardColor.COLORLESS, rarity, target);
    }
	
	/**
	 * 诅咒用
	 * @param shortID
	 * @param NAME
	 * @param IMG
	 * @param COST
	 * @param DESCRIPTION
	 */
	public AbstractTestCard(String shortID, String NAME, String DESCRIPTION) {
        super(TestMod.makeID(shortID), NAME, TestMod.cardIMGPath(shortID), -2, DESCRIPTION, CardType.CURSE, CardColor.CURSE, CardRarity.SPECIAL, CardTarget.NONE);
    }
	
	public static CardStrings Strings(String ID) {
		return CardCrawlGame.languagePack.getCardStrings(TestMod.makeID(ID));
	}
	
}
