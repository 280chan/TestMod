package testmod.relicsup;

import java.util.stream.Stream;

import com.megacrit.cardcrawl.actions.unique.LimitBreakAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class LaevatainUp extends AbstractUpgradedRelic {
	
	public void atPreBattle() {
		this.counter = 0;
		this.show();
		this.addTmpActionToBot(() -> att(apply(p(), new StrengthPower(p(), Math.max(3, count())))));
	}
	
	private int count(Stream<AbstractCard> s) {
		return (int) s.filter(c -> c.type == CardType.CURSE).count();
	}
	
	private int count() {
		return count(p().masterDeck.group.stream()) > 0 ? count(p().masterDeck.group.stream()) : count(combatCards());
	}
	
	private void revert(AbstractPower p) {
		p.amount = -p.amount;
		p.updateDescription();
	}
	
	public void atTurnStart() {
		this.counter++;
		if (this.counter == 3) {
			this.counter = 0;
			atb(new LimitBreakAction());
			this.addTmpActionToBot(() -> p().powers.stream().filter(p -> p instanceof StrengthPower && p.amount < 0)
					.forEach(this::revert));
			this.show();
		}
	}
	
	public void onVictory() {
		this.counter = -1;
	}
	
}