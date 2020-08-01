package halloweenMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.AbstractPower.PowerType;
import com.megacrit.cardcrawl.powers.ArtifactPower;

public class TrickAction extends AbstractGameAction {
	private static final float DURATION = Settings.ACTION_DUR_XFAST;
	private int buffAmount;
	private int drawAmount;

	public TrickAction(AbstractCreature source, int buffAmount, int drawAmount) {
		this.actionType = AbstractGameAction.ActionType.SPECIAL;
		this.duration = DURATION;
		this.source = source;
		this.buffAmount = buffAmount;
		this.drawAmount = drawAmount;
		this.amount = 0;
	}

	@Override
	public void update() {
		this.isDone = true;
		int draw = 0;
		for (AbstractPower p : this.source.powers)
			if (p.type == PowerType.DEBUFF)
				this.amount++;
			else if (p.ID.equals(ArtifactPower.POWER_ID))
				draw += p.amount;
		if (this.amount != 0) {
			int tmp = this.amount * this.buffAmount;
			AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(source, source, new ArtifactPower(source, tmp), tmp));
		}
		if (draw != 0) {
			AbstractDungeon.actionManager.addToBottom(new DrawCardAction(source, draw * this.drawAmount));
		}
	}

}
