package testmod.relicsup;

import java.util.HashMap;
import java.util.function.Consumer;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.actions.common.ShuffleAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardMagicianUp extends AbstractUpgradedRelic {
	private static final HashMap<AbstractCard, Integer> CARD = new HashMap<AbstractCard, Integer>();

	public void onEquip() {
		this.addEnergy();
	}
	
	public void onUnequip() {
		this.reduceEnergy();
	}
	
	public void atPreBattle() {
		if (this.isActive)
			this.combatCards().forEach(c -> CARD.put(c, -1));
	}
	
	public void onVictory() {
		if (this.isActive)
			CARD.clear();
	}
	
	public void onCardDraw(AbstractCard c) {
		if (CARD.containsKey(c) && CARD.get(c) == -1) {
			CARD.compute(c, (a, b) -> GameActionManager.turn);
		}
	}
	
	private void play(AbstractCard c, AbstractMonster m) {
		c.purgeOnUse = true;
		if (m != null)
			c.calculateCardDamage(m);
		att(new NewQueueCardAction(c, m, false, true));
	}
	
	private Consumer<AbstractCard> play(AbstractMonster m) {
		return c -> {
			for (int i = 0; i < CARD.get(c); i++)
				play(c.makeSameInstanceOf(), m);
		};
	}
	
	public void onUseCard(AbstractCard c, UseCardAction action) {
		if (!c.purgeOnUse && CARD.containsKey(c) && CARD.get(c) > 0) {
			this.show();
			AbstractMonster m = null;
			if (action.target != null) {
				m = (AbstractMonster) action.target;
			}
			play(m).accept(c);
		}
	}
	
	public void atTurnStart() {
		if (!p().discardPile.isEmpty())
			this.atb(new EmptyDeckShuffleAction());
		this.atb(new ShuffleAction(p().drawPile));
	}
	
}