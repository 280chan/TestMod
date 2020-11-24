package christmasMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

public class FamineAction extends AbstractGameAction {
	private DamageInfo info;
	private static final float DURATION = 0.1F;
	private int ratio;

	public FamineAction(AbstractCreature target, DamageInfo info, int amount, int ratio) {
		this.info = info;
		setValues(target, info);
		this.actionType = ActionType.DAMAGE;
		this.duration = DURATION;
		this.amount = amount;
		this.ratio = ratio;
	}

	@Override
	public void update() {
		if ((this.duration == DURATION) && (this.target != null)) {
			AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY,
					AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
			if (!(this.target.isDead || this.target.isDying || this.target.isEscaping)) {
				int pre = this.target.currentHealth;
				this.target.damage(this.info);
				int heal = (pre - this.target.currentHealth) / ratio;
				if (heal > 0)
					this.info.owner.heal(heal);
				if ((this.target.isDying || this.target.currentHealth <= 0) && (!this.target.halfDead)
						&& (!this.target.hasPower("Minion")))
					this.info.owner.increaseMaxHp(amount, true);
			}
		} else {
			System.out.println("目标为null???");
		}
		this.isDone = true;
	}

}
