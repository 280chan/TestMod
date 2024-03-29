package testmod.powers;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import basemod.Pair;
import basemod.ReflectionHacks;

public class RecapPower extends AbstractTestPower {

	public ArrayList<Pair<AbstractCard, AbstractMonster>> list = new ArrayList<Pair<AbstractCard, AbstractMonster>>();

	public RecapPower(AbstractCreature owner, int amount) {
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		this.description = desc(0) + this.amount + desc(1);
	}
	
	private void play(Pair<AbstractCard, AbstractMonster> p) {
		this.addTmpActionToBot(() -> {
			AbstractMonster m = p.getValue();
			if (m == null || m.isDeadOrEscaped())
				m = AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
			this.playAgain(p.getKey(), m);
		});
	}
	
	public void atStartOfTurn() {
		this.list.forEach(this::play);
		this.list.clear();
	}
	
	private Pair<AbstractCard, AbstractMonster> pair(AbstractCard c, AbstractCreature m) {
		return new Pair<AbstractCard, AbstractMonster>(c, (AbstractMonster)m);
	}
	
	private AbstractCreature tryGetTarget(AbstractCard c) {
		ArrayList<AbstractGameAction> list = AbstractDungeon.actionManager.actions;
		for (int i = list.size() - 1; i >= 0; i--) {
			AbstractGameAction a = list.get(i);
			if (a instanceof UseCardAction) {
				AbstractCard b = ReflectionHacks.getPrivate(a, UseCardAction.class, "targetCard");
				if (c.equals(b)) {
					return a.target.isPlayer ? null : a.target;
				}
			}
		}
		return null;
	}
	
	public void onAfterCardPlayed(AbstractCard c) {
		if (c.isInAutoplay)
			return;
		this.list.add(pair(c, this.tryGetTarget(c)));
		if (this.list.size() > this.amount)
			this.list.remove(0);
	}

}
