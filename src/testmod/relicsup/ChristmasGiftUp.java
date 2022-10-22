package testmod.relicsup;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import christmasMod.mymod.ChristmasMod;
import christmasMod.relics.ChristmasGift;

public class ChristmasGiftUp extends AbstractUpgradedRelic {
	
	public ChristmasGiftUp() {
		super(ChristmasGiftUp.class, ChristmasGift.class);
		this.counter = 0;
	}
	
	public void onExhaust(final AbstractCard c) {
		p().heal(Math.max(1, c.cost == -1 ? EnergyPanel.totalCount : c.costForTurn));
		this.flash();
	}
	
	private static int count(int n) {
		return n < 1 ? 0 : 1 + count(n >> 1);
	}

	public int onPlayerHeal(int healAmount) {
		if (healAmount > 0) {
			int pre = count(this.counter / 25);
			this.counter += healAmount;
			int post = count(this.counter / 25);
			while (post > pre) {
				this.addRandomKey();
				pre++;
			}
		}
		return healAmount;
	}
	
	public void atTurnStart() {
		this.atb(new MakeTempCardInHandAction(ChristmasMod.randomGift(true)));
	}
	
}