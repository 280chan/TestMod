package actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.CleaveEffect;

public class ComboMasterAction extends AbstractGameAction {
	private int[] multiDamage;
	private AbstractPlayer p;
	private int damage;

	public ComboMasterAction(AbstractPlayer p, int[] multiDamage, int damage, DamageType damageType) {
		this.multiDamage = multiDamage;
		this.damage = damage;
	    this.damageType = damageType;
	    this.p = p;
	    this.duration = Settings.ACTION_DUR_XFAST;
		this.actionType = ActionType.SPECIAL;
		this.attackEffect = AttackEffect.SLASH_HORIZONTAL;
	}
	
	public void update() {
		if (0 < damage && checkContinue()) {
			this.addToTop(this.next());
			this.addToTop(new DamageAllEnemiesAction(this.p, this.multiDamage, this.damageType, AttackEffect.NONE, true));
			this.addToTop(new SFXAction("ATTACK_HEAVY"));
			this.addToTop(new VFXAction(this.p, new CleaveEffect(), 0.0F)); 
		}
		this.isDone = true;
	}
	
	private ComboMasterAction next() {
		return new ComboMasterAction(p, multiDamage, damage - 1, damageType);
	}

	private static boolean checkContinue() {
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters)
			if (!(m == null || m.isDead || m.halfDead || m.isDying || m.isEscaping))
				return true;
		return false;
	}
	
}