package actions;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

import utils.MiscMethods;

public class PerfectComboAction extends AbstractGameAction implements MiscMethods {
	private DamageInfo info;
	private static final float DURATION = 0.1F;
	private static final float POST_ATTACK_WAIT_DUR = 0.1F;
	private static final PerfectComboAction INSTANCE = new PerfectComboAction();
	private boolean skipWait;
	private int magic;
	private static Random rng;
	private static int deadLoopCounter = 0;

	public PerfectComboAction(AbstractCreature target, DamageInfo info, AttackEffect effect, int magic) {
		this(target, info, effect);
		this.magic = magic;
	}
	
	private PerfectComboAction(AbstractCreature target, DamageInfo info, AttackEffect effect) {
		this.skipWait = false;
		this.info = info;
		this.target = target;
		this.actionType = ActionType.DAMAGE;
		this.attackEffect = effect;
		this.duration = DURATION;
	}

	public PerfectComboAction(AbstractCreature target, DamageInfo info, AttackEffect effect, boolean superFast, int timesUpgraded) {
		this(target, info, effect, timesUpgraded);
		this.skipWait = true;
	}
	
	private PerfectComboAction() {
	}

	private AbstractGameAction combo() {
		AbstractCreature c = null;
		ArrayList<AbstractCreature> avalible = new ArrayList<AbstractCreature>();
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters)
			if (!(m == null || m.isDead || m.halfDead || m.isDying || m.isEscaping))
				avalible.add(m);
		if (avalible.isEmpty())
			return new WaitAction(POST_ATTACK_WAIT_DUR);
		c = avalible.get((int) (Math.random() * avalible.size()));
		return new PerfectComboAction(c, info, attackEffect, skipWait, magic);
	}
	
	public static void setRng() {
		rng = INSTANCE.copyRNG(AbstractDungeon.miscRng);
	}
	
	private boolean roll() {
		return rng.random(99) < this.magic;
	}
	
	public void update() {
		if (this.shouldCancelAction()) {
			this.isDone = true;
			deadLoopCounter = 0;
		} else {
			AbstractDungeon.effectList
					.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY, this.attackEffect));
			this.isDone = true;
			this.info.applyPowers(this.info.owner, this.target);
			this.target.damage(this.info);
			
			if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
				AbstractDungeon.actionManager.clearPostCombatActions();
				deadLoopCounter = 0;
			} else if (!(this.target.hasPower("Invincible") && this.target.getPower("Invincible").amount == 0)) {
				if (roll() && deadLoopCounter < 100) {
					this.addToBot(this.combo());
				} else {
					deadLoopCounter = 0;
				}
			} else {
				deadLoopCounter = 0;
			}
			
		}
	}

}