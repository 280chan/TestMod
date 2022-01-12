package christmasMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.PoisonLoseHpAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

public class PlaguePoisonActAction extends AbstractGameAction {
	private static final float DURATION = Settings.ACTION_DUR_XFAST;

	private AbstractPower p;

	public PlaguePoisonActAction(AbstractCreature source, AbstractPower p) {
		this.actionType = ActionType.SPECIAL;
		this.duration = DURATION;
		this.source = source;
		this.p = p;
	}

	@Override
	public void update() {
		this.isDone = true;
		if (AbstractDungeon.getCurrRoom().phase == RoomPhase.COMBAT
				&& !AbstractDungeon.getMonsters().areMonstersBasicallyDead() && p.amount > 0) {
			p.flashWithoutSound();
			this.addToTop(new PoisonLoseHpAction(p.owner, this.source, p.amount, AttackEffect.POISON));
		}
	}

}
