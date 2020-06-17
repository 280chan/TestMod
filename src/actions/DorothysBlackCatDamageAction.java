package actions;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class DorothysBlackCatDamageAction extends AbstractGameAction {
	private static final float DURATION = Settings.ACTION_DUR_XFAST;
	
	private float totalDamage;
	
	public DorothysBlackCatDamageAction(float totalDamage) {
		this.actionType = ActionType.SPECIAL;
		this.duration = DURATION;
		this.totalDamage = totalDamage;
	}
	
	private ArrayList<AbstractMonster> canDamage() {
    	ArrayList<AbstractMonster> list = new ArrayList<AbstractMonster>();
    	for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters)
    		if (!(m.isDead || m.isDying || m.escaped || m.halfDead))
    			list.add(m);
    	return list;
    }
	
	@Override
	public void update() {
		ArrayList<AbstractMonster> list = this.canDamage();
		int damage = (int)(this.totalDamage / list.size());
		if (damage > 0)
			this.addToTop(new DamageAllEnemiesAction(AbstractDungeon.player, DamageInfo.createDamageMatrix(damage, true), DamageType.HP_LOSS, AttackEffect.POISON, true));
		this.isDone = true;
	}

}
