package testmod.relicsup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class RestrainedUp extends AbstractUpgradedRelic {
	private boolean played = false;

	public void onEquip() {
		this.addEnergy();
	}
	
	public void onUnequip() {
		this.reduceEnergy();
	}
	
	public void onUseCard(final AbstractCard c, final UseCardAction action) {
		if (c.type == CardType.POWER) {
			ArrayList<AbstractCard> l = this.combatCards().filter(a -> a.type == CardType.POWER).collect(toArrayList());
			if (!l.isEmpty()) {
				Collections.shuffle(l, new Random(AbstractDungeon.cardRandomRng.randomLong()));
				l.get(0).type = CardType.SKILL;
			}
		}
	}
	
	public void onPlayCard(final AbstractCard c, final AbstractMonster m) {
		this.played |= c.type == CardType.POWER;
	}

	public void atTurnStart() {
		this.counter++;
	}
	
	public void atPreBattle() {
		this.counter = 0;
		this.played = false;
	}

	public void onVictory() {
		this.counter = -1;
		this.played = false;
	}
	
	public boolean canPlay(AbstractCard c) {
		return this.counter > 1 || !this.played || c.type == CardType.POWER;
	}
}