package testmod.relics;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.GainGoldAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class DeterminationOfClimber extends AbstractTestRelic {
	
	private static Color color = null;
	
	public DeterminationOfClimber() {
		super(RelicTier.BOSS, LandingSound.MAGICAL);
	}
	
	public void onEquip() {
		this.reduceEnergy();
	}
	
	public void onUnequip() {
		this.addEnergy();
	}
	
	public void atPreBattle() {
		this.counter = -1;
	}
	
	public void act(int count) {
		this.addToBot(new GainEnergyAction(count));
		this.addToBot(new HealAction(p(), p(), count));
		this.addToBot(new DrawCardAction(p(), count));
		this.addToBot(new GainGoldAction(count));
		this.addToBot(new DamageAllEnemiesAction(p(), DamageInfo.createDamageMatrix(count, true), DamageType.THORNS,
				AttackEffect.BLUNT_LIGHT));
		this.show();
	}
	
	private int getValue(AbstractCard c) {
		return (c.freeToPlayOnce || c.cost == -2) ? 0 : (c.cost == -1 ? EnergyPanel.totalCount : c.costForTurn);
	}
	
	public void onUseCard(final AbstractCard c, final UseCardAction action) {
		if (!c.isInAutoplay) {
			if (this.counter > -1) {
				int amount = this.getValue(c);
				if (amount > this.counter)
					this.act(amount - this.counter);
			}
			this.counter = this.getValue(c);
		}
	}
	
	public void onRefreshHand() {
		if (color == null)
			color = this.initGlowColor();
		this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		if (!this.inCombat())
			return;
		this.stopPulse();
		colorRegister(color).addRelic(this).addPredicate(c -> this.getValue(c) > this.counter && this.counter != -1
				&& c.hasEnoughEnergy() && c.cardPlayable(AbstractDungeon.getRandomMonster())).updateHand();
	}

	public void onVictory() {
		this.stopPulse();
	}
}