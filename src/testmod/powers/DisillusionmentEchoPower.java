package testmod.powers;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

@SuppressWarnings("deprecation")
public class DisillusionmentEchoPower extends AbstractTestPower {
	
	public DisillusionmentEchoPower(AbstractCreature owner, int amount) {
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = desc(0) + this.amount + desc(1);
	}
	
	public void onUseCard(final AbstractCard card, final UseCardAction action) {
		if (!card.purgeOnUse && p().cardsPlayedThisTurn < 2) {
			flash();
			for (int i = 0; i < this.amount; i++) {
				playAgain(card, action);
			}
		}
	}

	private void playAgain(AbstractCard card, UseCardAction action) {
		this.playAgain(card, action.target == null ? null : (AbstractMonster) action.target);
	}
    
}
