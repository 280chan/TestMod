package christmasMod.actions;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

public class DeathAction extends AbstractGameAction {
	private DamageInfo info;
	private static final float DURATION = 0.1F;
	private int increase;

	public DeathAction(AbstractCreature target, DamageInfo info, int amount, int increase) {
		this.info = info;
		this.target = target;
		this.actionType = ActionType.DAMAGE;
		this.duration = DURATION;
		this.amount = amount;
		this.increase = increase;
	}

	private static boolean checkState(AbstractCreature m) {
		return (m.isDead || m.isDying || m.isEscaping || m.currentHealth <= 0);
	}
	
	private static AbstractCreature randomEnemy() {
		ArrayList<AbstractCreature> list = new ArrayList<AbstractCreature>();
		for (AbstractCreature m : AbstractDungeon.getCurrRoom().monsters.monsters) {
			if (!checkState(m)) {
				list.add(m);
			}
		}
		if (list.isEmpty())
			return null;
		return list.get((int) (Math.random() * list.size()));
	}
	
	private DeathAction next() {
		return new DeathAction(randomEnemy(), this.info, this.amount + increase, this.increase);
	}
	
	@Override
	public void update() {
		if ((this.duration == DURATION) && (this.target != null)) {
			AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY,
					AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
			this.info.applyPowers(this.info.owner, this.target);
			if (!checkState(this.target)) {
				for (int i = 0; i < this.amount && !checkState(this.target); i++)
					this.target.damage(this.info);
				if (checkState(this.target) && !AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead())
					AbstractDungeon.actionManager.addToTop(next());
			}
		} else {
			System.out.println("目标为null???");
		}
		this.isDone = true;
	}

}
