package actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class ObsoleteBoomerangDamageAction extends AbstractGameAction {
	private DamageInfo info;
	private AttackEffect effect;
	private static final float DURATION = 0.1F;

	public ObsoleteBoomerangDamageAction(DamageInfo info, AttackEffect effect) {
		this.info = info;
		this.effect = effect;
		this.actionType = ActionType.DAMAGE;
		this.duration = DURATION;
	}

	@Override
	public void update() {
		this.isDone = true;
		this.target = AbstractDungeon.getMonsters().getRandomMonster(true);
		if (target != null && !target.halfDead && !target.isDying && !target.isEscaping) {
			setValues(target, info);
			AbstractDungeon.actionManager.addToBottom(new DamageAction(target, info, effect));
		}
	}

}
