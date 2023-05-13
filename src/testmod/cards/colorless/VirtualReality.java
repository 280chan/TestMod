package testmod.cards.colorless;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.utility.LoseBlockAction;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import testmod.cards.AbstractTestCard;

public class VirtualReality extends AbstractTestCard {
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
	
	public void applyPowers() {
		super.applyPowers();
		this.upDesc(this.exDesc()[0] + blockGainLastTurn + this.exDesc()[1]);
	}
	
	public void calculateCardDamage(AbstractMonster m) {
		super.calculateCardDamage(m);
		this.upDesc(this.exDesc()[0] + blockGainLastTurn + this.exDesc()[1]);
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