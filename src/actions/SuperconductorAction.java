package actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import powers.SuperconductorNoEnergyPower;
import powers.SuperconductorPower;
import utils.MiscMethods;

public class SuperconductorAction extends AbstractGameAction implements MiscMethods {
	private static final float DURATION = Settings.ACTION_DUR_XFAST;
	private AbstractPlayer p;
	public int[] multiDamage;
	private boolean freeToPlayOnce = false;
	private int energyOnUse = -1;

	public SuperconductorAction(AbstractPlayer p, boolean freeToPlayOnce, int energyOnUse) {
		this.p = p;
		this.freeToPlayOnce = freeToPlayOnce;
		this.duration = DURATION;
		this.actionType = ActionType.SPECIAL;
		this.energyOnUse = energyOnUse;
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

		for (AbstractCard c : AbstractDungeon.player.hand.group) {
			c.setCostForTurn(-9);
		}
		
		if (effect > 0) {
			if (!this.freeToPlayOnce) {
				this.p.energy.use(EnergyPanel.totalCount);
			}
			AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(p, p, new SuperconductorNoEnergyPower(p, EnergyPanel.totalCount)));
			AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(p, p, new SuperconductorPower(p, effect), effect));
		}

		this.isDone = true;
	}
	
}