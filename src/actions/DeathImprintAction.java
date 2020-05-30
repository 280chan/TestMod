package actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import powers.DeathImprintPower;

public class DeathImprintAction extends AbstractGameAction {
	private int damage;

	public DeathImprintAction(AbstractPlayer p, AbstractMonster m, int damage, DamageType damageType) {
		this.damage = damage;
	    this.damageType = damageType;
	    this.target = m;
	    this.source = p;
	    this.duration = Settings.ACTION_DUR_XFAST;
		this.actionType = ActionType.SPECIAL;
	}
	
	public void update() {
		if (this.target.hasPower(DeathImprintPower.POWER_ID)) {
			AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.target, this.source, DeathImprintPower.POWER_ID));//造成伤害
			AbstractDungeon.actionManager.addToBottom(new DamageAction(this.target, new DamageInfo(this.source, this.damage, this.damageType), AttackEffect.BLUNT_HEAVY));//造成伤害
		} else {
			AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this.target, this.source, new DeathImprintPower(this.target, 0)));//造成伤害
			AbstractDungeon.actionManager.addToBottom(new DamageAction(this.target, new DamageInfo(this.source, this.damage, this.damageType), AttackEffect.SLASH_DIAGONAL));//造成伤害
		}
		this.isDone = true;
	}
	
}