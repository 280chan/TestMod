package powers;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ReverberationPower extends AbstractTestPower {
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
		return m.isDead || m.halfDead || m.escaped || m.isEscaping || m.isDying;
	}
	
	private void next(AbstractCard c, int size) {
		this.addToBot(new AbstractGameAction() {
			@Override
			public void update() {
				this.isDone = true;
				ArrayList<AbstractMonster> list = new ArrayList<AbstractMonster>();
				for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters)
					if (!checkMonster(m))
						list.add(m);
				if (list.isEmpty())
					return;
				AbstractMonster m = list.get(AbstractDungeon.cardRandomRng.random(0, list.size() - 1));
				c.calculateCardDamage(m);
				c.use(AbstractDungeon.player, m);
				if (size > 1)
					next(c, size - 1);
			}
		});
	}

}
