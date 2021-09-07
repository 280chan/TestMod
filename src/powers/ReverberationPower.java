package powers;

import java.util.ArrayList;
import java.util.stream.Collectors;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import utils.MiscMethods;

public class ReverberationPower extends AbstractTestPower implements MiscMethods {
	public static final String POWER_ID = "ReverberationPower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;

	public ReverberationPower(AbstractCreature owner, int amount) {
		super(POWER_ID);
		this.name = NAME;
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.BUFF;
	}

	public void updateDescription() {
		this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
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
			ArrayList<AbstractMonster> list = AbstractDungeon.getCurrRoom().monsters.monsters.stream()
					.filter(this::checkMonster).collect(Collectors.toCollection(ArrayList::new));
			if (list.isEmpty())
				return;
			AbstractMonster m = list.get(AbstractDungeon.cardRandomRng.random(0, list.size() - 1));
			c.calculateCardDamage(m);
			if (c.cost == -1 && !c.freeToPlayOnce)
				c.freeToPlayOnce = true;
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
