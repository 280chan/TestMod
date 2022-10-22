
package testmod.cards;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import testmod.cards.AbstractTestCard;

/**
 * @deprecated
 */
public class 示例卡牌 extends AbstractTestCard {
	private static final int COST = -2;
	private static final int BASE_BLK = 5;
	private static final int BASE_DMG = 6;
	private static final int BASE_MGC = 1;

	public 示例卡牌() {
		super(COST, CardType.ATTACK, CardRarity.RARE, CardTarget.NONE);
		this.baseBlock = BASE_BLK;
		this.baseDamage = BASE_DMG;
		this.magicNumber = this.baseMagicNumber = BASE_MGC;
	}

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		
	}
	
	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upgradeBlock(3);
			this.upgradeDamage(3);
			this.upgradeMagicNumber(1);
			this.upgradeBaseCost(1);
		}
	}
}