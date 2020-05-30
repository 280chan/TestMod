package actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class IllusoryAction extends AbstractGameAction {
	private static final float DURATION = Settings.ACTION_DUR_XFAST;

	public IllusoryAction() {
		this.duration = DURATION;
		this.actionType = ActionType.SPECIAL;
	}

	public void update() {
		int count = 0;
		for (AbstractCard c : AbstractDungeon.player.drawPile.group) {
			if (c.isEthereal)
				count++;
			else {
				c.isEthereal = true;
				c.name += "(虚无)";
			}
		}
		AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(count));
		this.isDone = true;
	}
	
}