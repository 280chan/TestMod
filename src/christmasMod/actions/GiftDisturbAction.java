package christmasMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class GiftDisturbAction extends AbstractGameAction {
	private static final float DURATION = Settings.ACTION_DUR_XFAST;
	private boolean freeToPlayOnce, upgraded;
	private int energyOnUse = -1;
	private AbstractPlayer p;

	public GiftDisturbAction(AbstractPlayer p, AbstractMonster m, boolean freeToPlayOnce, boolean upgraded, int energyOnUse) {
		this.actionType = ActionType.SPECIAL;
		this.duration = DURATION;
		this.source = this.p = p;
		this.target = m;
		this.freeToPlayOnce = freeToPlayOnce;
		this.upgraded = upgraded;
		this.energyOnUse = energyOnUse;
	}

	@Override
	public void update() {
		this.isDone = true;
		int x = EnergyPanel.totalCount;
		if (this.energyOnUse != -1)
			  x = this.energyOnUse;
		if (this.p.hasRelic("Chemical X")) {
			x += 2;
			this.p.getRelic("Chemical X").flash();
		}
		if (this.upgraded)
			x++;
		if (!this.freeToPlayOnce)
			this.p.energy.use(EnergyPanel.totalCount);
		if (this.target.hasPower("Artifact"))
			AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(this.target, p, new WeakPower(target, 1, false), 1));
		if (x > 1) {
			int y = x - 1;
			AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(this.target, p, new VulnerablePower(target, y, false), y));
			AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(this.target, p, new WeakPower(target, y, false), y));
		}
		if (x > 0)
			AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(this.target, p, new StrengthPower(target, -x), -x));
	}

}
