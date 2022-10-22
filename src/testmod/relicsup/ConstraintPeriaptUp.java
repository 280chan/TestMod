package testmod.relicsup;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class ConstraintPeriaptUp extends AbstractUpgradedRelic {
	
	public void atPreBattle() {
		this.counter = 1;
		this.atb(new MakeTempCardInDrawPileAction(new Burn(), 1, true, true));
	}
	
	public void onRefreshHand() {
		if (this.inCombat())
			this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		this.stopPulse();
		if (!p().hand.group.stream().allMatch(this::checkPlayable))
			this.beginLongPulse();
	}
	
	public void onPlayerEndTurn() {
		int amount = (int) p().hand.group.stream().filter(not(this::checkPlayable)).count();
		if (amount > 0) {
			p().heal(amount);
			this.getNumberList(this.counter, this.counter + amount).forEach(i -> this.atb(new DamageAllEnemiesAction(p(),
					DamageInfo.createDamageMatrix(i, true), DamageType.THORNS, AttackEffect.FIRE)));
			this.counter += amount;
		}
	}
	
	private boolean checkPlayable(AbstractCard c) {
		return AbstractDungeon.getMonsters().monsters.stream().anyMatch(m -> c.canUse(p(), m));
	}

	public void onVictory() {
		this.stopPulse();
		this.counter = -1;
	}
}