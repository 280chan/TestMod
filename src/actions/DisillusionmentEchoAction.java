package actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import powers.DisillusionmentEchoPower;

public class DisillusionmentEchoAction extends AbstractGameAction {
	private boolean freeToPlayOnce = false;
	private int energyOnUse = -1;
	private int magicNumber;
	private AbstractPlayer p;
	private int timesUpgrade;

	public DisillusionmentEchoAction(AbstractPlayer p, boolean freeToPlayOnce, int energyOnUse, int magicNumber, int timesUpgrade) {
		this.freeToPlayOnce = freeToPlayOnce;
		this.duration = Settings.ACTION_DUR_XFAST;
		this.actionType = ActionType.SPECIAL;
		this.energyOnUse = energyOnUse;
		this.magicNumber = magicNumber;
		this.timesUpgrade = timesUpgrade;
		this.p = p;
	}
  
	public void update() {
		int effect = EnergyPanel.totalCount;
		if (this.energyOnUse != -1) {
			effect = this.energyOnUse;
		}
		if (this.p.hasRelic("Chemical X")) {
			effect += 2;
			this.p.getRelic("Chemical X").flash();
		}
		if (effect > 0) {
			if (this.magicNumber == 0) {
				this.magicNumber = 3 - this.timesUpgrade;
			}
			int amount = effect / this.magicNumber;
			if (amount > 0)
				AbstractDungeon.actionManager
						.addToBottom(new ApplyPowerAction(p, p, new DisillusionmentEchoPower(p, amount), amount));
			if (!this.freeToPlayOnce) {
				this.p.energy.use(EnergyPanel.totalCount);
			}
		}
		this.isDone = true;
	}
}
