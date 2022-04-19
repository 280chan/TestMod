package testmod.powers;

import java.util.ArrayList;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ReverberationPower extends AbstractTestPower {

	public ReverberationPower(AbstractCreature owner, int amount) {
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.BUFF;
	}

	public void updateDescription() {
		this.description = desc(0) + this.amount + desc(1);
	}

	public void onExhaust(final AbstractCard card) {
		if (card.type == CardType.CURSE || card.type == CardType.STATUS) {
			return;
		}
		this.next(card, this.amount);
	}
	
	private boolean checkMonster(AbstractMonster m) {
		return !(m.isDead || m.halfDead || m.escaped || m.isEscaping || m.isDying);
	}
	
	private void next(AbstractCard c, int size) {
		this.addTmpActionToBot(() -> {
			ArrayList<AbstractMonster> list = AbstractDungeon.getMonsters().monsters.stream().filter(this::checkMonster)
					.collect(this.toArrayList());
			if (list.isEmpty())
				return;
			AbstractMonster m = list.get(AbstractDungeon.cardRandomRng.random(0, list.size() - 1));
			c.calculateCardDamage(m);
			c.freeToPlayOnce = c.cost == -1 && !c.freeToPlayOnce;
			c.use(AbstractDungeon.player, m);
			if (size > 1)
				next(c, size - 1);
			else if (c.cost == -1)
				resetXcost(c);
		});
	}
	
	private void resetXcost(AbstractCard c) {
		this.addTmpActionToBot(() -> {
			c.freeToPlayOnce = false;
		});
	}

}
