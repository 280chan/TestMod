package actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

import cards.colorless.Bloodthirsty;
import mymod.TestMod;
import utils.MiscMethods;

public class BloodthirstyAction extends AbstractGameAction implements MiscMethods {
	private DamageInfo info;
	private static final float DURATION = 0.1F;

	public BloodthirstyAction(AbstractCreature target, AbstractCreature source, int magic) {
		this.target = target;
		Long max = (long) this.target.maxHealth;
		Long rate = (long) magic;
		Long amount = max * rate / 100L;
		if (amount > 2147483647L)
			amount = 100000000L;
		this.info = new DamageInfo(source, amount.intValue(), DamageType.HP_LOSS);
		setValues(target, info);
		this.actionType = ActionType.SPECIAL;
		this.duration = DURATION;
	}

	@Override
	public void update() {
		if ((this.duration == DURATION) && (this.target != null)) {
			AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY,
					AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
			if (!(this.target.isDead || this.target.isDying || this.target.isEscaping)) {
				int pre = this.target.currentHealth;
				this.target.damage(this.info);
				int heal = pre - this.target.currentHealth;
				if (heal > 0) {
					this.info.owner.heal(heal);
					for (AbstractCard c : this.getAllInBattleInstance(TestMod.makeID(Bloodthirsty.ID))) {
						((Bloodthirsty)c).doublesMagicNumber();
					}
				}
			}
		} else {
			TestMod.info("目标为null???");
		}
		this.isDone = true;
	}

}
