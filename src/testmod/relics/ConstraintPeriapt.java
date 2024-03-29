package testmod.relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class ConstraintPeriapt extends AbstractTestRelic {
	
	public void atPreBattle() {
		this.atb(new MakeTempCardInDrawPileAction(new Burn(), 1, true, true));
	}
	
	public void onRefreshHand() {
		if (this.inCombat())
			this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		int preEnergy = EnergyPanel.totalCount;
		this.stopPulse();
		if (!p().hand.group.stream().allMatch(c -> this.checkPlayable(p(), c)))
			this.beginLongPulse();
		EnergyPanel.totalCount = preEnergy;
	}
	
	public void onPlayerEndTurn() {
		int preEnergy = EnergyPanel.totalCount;
		int amount = (int) p().hand.group.stream().filter(c -> !this.checkPlayable(p(), c)).count();
		EnergyPanel.totalCount = preEnergy;
		if (amount > 0)
			p().heal(amount);
		for (int i = 0; i < amount; i++) {
			this.atb(new DamageAllEnemiesAction(p(), DamageInfo.createDamageMatrix(10, true), DamageType.THORNS,
					AttackEffect.FIRE));
		}
	}
	
	private boolean checkPlayable(AbstractPlayer p, AbstractCard c) {
		EnergyPanel.totalCount = c.costForTurn;
		return AbstractDungeon.getMonsters().monsters.stream().anyMatch(m -> c.canUse(p, m));
	}

	public void onVictory() {
		this.stopPulse();
	}
}