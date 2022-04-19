package testmod.powers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class ReproducePower extends AbstractTestPower {
	private AbstractCard c;
	
	public ReproducePower(AbstractPlayer owner, AbstractCard card, int amount) {
		this.c = card;
		this.name += "[" + c.name + "]";
		this.ID += c.cardID + c;
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		this.description = desc(0) + c.name + desc(1);
	}
    
	public void stackPower(int stackAmount) {
		this.fontScale = 8.0f;
	}
	
	public void onAfterCardPlayed(AbstractCard c) {
		if (c.equals(this.c)) {
			this.addTmpActionToBot(() -> {
				c.setCostForTurn(c.costForTurn + this.amount);
			});
			c.returnToHand = c.retain = c.selfRetain = true;
    	}
    }
	
	public void onRemove() {
		this.c.retain = this.c.returnToHand = this.c.selfRetain = false;
	}
}
