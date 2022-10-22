package testmod.cards.colorless;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.utility.LoseBlockAction;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import testmod.cards.AbstractTestCard;

public class VirtualReality extends AbstractTestCard {
	private static final int BASE_BLK = 40;
	private static int blockGainLastTurn = 0, blockGainThisTurn = 0;
	
	public static void reset() {
		blockGainLastTurn = blockGainThisTurn = 0;
	}
	
	public static void gainBlock(int amount) {
		blockGainThisTurn += amount;
	}
	
	public static void turnStarts() {
		blockGainLastTurn = blockGainThisTurn;
		blockGainThisTurn = 0;
	}
	
	public VirtualReality() {
		super(0, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF);
		this.baseBlock = BASE_BLK;
	}

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.atb(new GainBlockAction(p, p, this.block));
		if (blockGainLastTurn > 0)
			this.atb(new LoseBlockAction(p, p, blockGainLastTurn));
	}
	
	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upgradeBlock(10);
		}
	}
}