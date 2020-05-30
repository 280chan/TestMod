	package actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class ArrangementAction extends AbstractGameAction {
	private boolean freeToPlayOnce = false;
	private boolean upgraded = false;
	private AbstractPlayer p;
	private int energyOnUse = -1;
	private int damage = 0;
	private int block = 0;

	public ArrangementAction(AbstractPlayer p, AbstractMonster m, DamageType damageType, boolean freeToPlayOnce, boolean upgraded, int energyOnUse, int damage, int block) {
		this.p = p;
		this.upgraded = upgraded;
		this.freeToPlayOnce = freeToPlayOnce;
		this.energyOnUse = energyOnUse;
		this.damageType = damageType;
		this.damage = damage;
		this.block = block;
	    this.target = m;
	    this.source = p;
	    this.duration = Settings.ACTION_DUR_XFAST;
		this.actionType = ActionType.SPECIAL;
	}
	
	public void update() {
		this.isDone = true;
		int x = EnergyPanel.totalCount;
		if (this.energyOnUse != -1)
		      x = this.energyOnUse;
		if (this.p.hasRelic("Chemical X")) {
			x += 2;
			this.p.getRelic("Chemical X").flash();
		}
		if (x < 1)
			return;
		if (!this.freeToPlayOnce)
	        this.p.energy.use(EnergyPanel.totalCount);
		AbstractDungeon.actionManager.addToTop(new DrawCardAction(p, x));
		if (this.upgraded) {
			for (int i = 0; i < x; i++) {
				AbstractDungeon.actionManager.addToTop(new DamageAction(this.target, new DamageInfo(this.p, this.damage, this.damageType), AttackEffect.BLUNT_LIGHT));
			}
			for (int i = 0; i < x; i++) {
				AbstractDungeon.actionManager.addToTop(new GainBlockAction(p, p, this.block, true));
			}
		} else {
			AbstractDungeon.actionManager.addToTop(new DamageAction(this.target, new DamageInfo(this.p, this.damage, this.damageType), AttackEffect.BLUNT_LIGHT));
			AbstractDungeon.actionManager.addToTop(new GainBlockAction(p, p, this.block, true));
		}
		AbstractDungeon.actionManager.addToBottom(new ArrangementUpgradingAction(p, x));
	}
	
}