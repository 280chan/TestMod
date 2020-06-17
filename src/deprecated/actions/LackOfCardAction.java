package deprecated.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.EnergizedPower;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import deprecated.relics.LackOfCard;
import relics.AbstractTestRelic;

/**
 * @deprecated
 */
public class LackOfCardAction extends AbstractGameAction {
	private AbstractPlayer p;

	public LackOfCardAction(AbstractPlayer p, int amount) {
	    this.p = p;
	    this.amount = amount;
	    this.duration = Settings.ACTION_DUR_XFAST;
		this.actionType = ActionType.SPECIAL;
	}
	
	public void update() {
		if (check()) {
			AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, this.amount));
			((AbstractTestRelic)(p.getRelic(LackOfCard.ID))).show();
		} else {
			AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new EnergizedPower(p, 1), 1));
		}
		this.isDone = true;
	}
	
	private boolean check() {
		return p.hand.size() <= EnergyPanel.totalCount;
	}
	
}