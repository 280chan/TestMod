package powers;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import mymod.TestMod;

public class ReverberationPower extends AbstractPower {
	public static final String POWER_ID = "ReverberationPower";
	public static final String NAME = "残响";
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String[] DESCRIPTIONS = { "每当一张非 #y诅咒 、 #y状态 牌 #y消耗 时，触发其打出时的效果 #b", " 次。" };

	public ReverberationPower(AbstractCreature owner, int amount) {
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = amount;
		this.img = ImageMaster.loadImage(IMG);
		updateDescription();
		this.type = PowerType.BUFF;
	}

	public void updateDescription() {
		this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
	}

	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
		this.amount += stackAmount;
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
		AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
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
