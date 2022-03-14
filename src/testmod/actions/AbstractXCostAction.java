package testmod.actions;

import java.util.function.Consumer;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public abstract class AbstractXCostAction extends AbstractGameAction {
	private boolean freeToPlayOnce;
	private int energyOnUse;
	private Consumer<Integer> action;
	protected AbstractCard c;

	public AbstractXCostAction(AbstractCard c) {
		this.actionType = ActionType.SPECIAL;
		this.freeToPlayOnce = c.freeToPlayOnce;
		this.energyOnUse = c.energyOnUse;
		this.c = c;
	}
	
	public AbstractXCostAction(AbstractCard c, Consumer<Integer> action) {
		this(c);
		this.action = action;
	}
	
	protected void action() {
	}
  
	public void update() {
		this.isDone = true;
		this.amount = this.energyOnUse == -1 ? EnergyPanel.totalCount : this.energyOnUse;
		if (AbstractDungeon.player.hasRelic("Chemical X")) {
			this.amount += 2;
			AbstractDungeon.player.getRelic("Chemical X").flash();
		}
		if (this.amount > 0) {
			if (this.action != null)
				this.action.accept(this.amount);
			else
				this.action();
			if (!this.freeToPlayOnce)
				AbstractDungeon.player.energy.use(EnergyPanel.totalCount);
		}
	}
}
