package actions;

import java.util.ArrayList;
import java.util.stream.Collectors;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower.PowerType;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

import utils.MiscMethods;

public class AdversityCounterattackAction extends AbstractGameAction implements MiscMethods {
	private DamageInfo info;
	private static final float DURATION = 0.1F;
	private static final float POST_ATTACK_WAIT_DUR = 0.1F;
	private boolean skipWait;
	private AbstractPlayer p;

	private int base;

	public AdversityCounterattackAction(AbstractPlayer p, AbstractMonster m, AttackEffect effect) {
		this.target = m;
		this.source = this.p = p;
		this.actionType = ActionType.SPECIAL;
		this.attackEffect = effect;
		this.duration = DURATION;
	}

	private AdversityCounterattackAction(AbstractMonster target, DamageInfo info, AttackEffect effect, int times) {
		this.skipWait = times > 5;
		this.base = info.base;
		this.target = target;
		this.info = info;
		this.actionType = ActionType.DAMAGE;
		this.attackEffect = effect;
		this.duration = DURATION;
		this.amount = times;
	}

	private AbstractGameAction next(int base, int times) {
		AbstractMonster m = randomTarget();
		return m == null ? new WaitAction(POST_ATTACK_WAIT_DUR)
				: new AdversityCounterattackAction(m, new DamageInfo(this.p != null ? this.p : this.info.owner, base),
						attackEffect, times);
	}

	private static AbstractMonster randomTarget() {
		ArrayList<AbstractMonster> avalible = AbstractDungeon.getCurrRoom().monsters.monsters.stream()
				.filter(m -> !(m == null || m.isDead || m.halfDead || m.isDying || m.isEscaping))
				.collect(Collectors.toCollection(ArrayList::new));
		return avalible.isEmpty() ? null : avalible.get((int) (Math.random() * avalible.size()));
	}

	private static int countAmount(AbstractCreature c, PowerType t) {
		return c.powers.stream().filter(p -> p.type == t).map(p -> Math.abs(p.amount)).reduce(0, Integer::sum);
	}

	public void update() {
		this.isDone = true;
		if (this.shouldCancelAction()) {
			return;
		} else if (this.p != null) {
			int baseDamage = countAmount(this.target, PowerType.BUFF);
			int times = countAmount(this.p, PowerType.DEBUFF);
			this.addToTop(this.next(baseDamage, times));
		} else {
			AbstractDungeon.effectList
					.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY, this.attackEffect));
			if (this.amount > 0) {
				this.info.applyPowers(this.info.owner, this.target);
				this.target.damage(this.info);
			}

			if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
				AbstractDungeon.actionManager.clearPostCombatActions();
			} else if (this.amount > 1) {
				if (!(this.target.hasPower("Invincible") && this.target.getPower("Invincible").amount == 0)) {
					this.addToTop(this.next(this.base, this.amount - 1));
				}
			}
			if (!this.skipWait && !Settings.FAST_MODE && this.amount < 20) {
				this.addToTop(new WaitAction(POST_ATTACK_WAIT_DUR));
			}
		}
	}

}