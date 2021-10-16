package relics;

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
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class DeterminationOfClimber extends AbstractTestRelic {
	public static final String ID = "DeterminationOfClimber";
	
	private static Color color = null;
	
	public DeterminationOfClimber() {
		super(ID, RelicTier.BOSS, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return this.DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	public void atPreBattle() {
		this.counter = -1;
	}
	
	public void act(int count) {
		AbstractCreature p = AbstractDungeon.player;
		this.addToBot(new GainEnergyAction(count));
		this.addToBot(new HealAction(p, p, count));
		this.addToBot(new DrawCardAction(p, count));
		this.addToBot(new GainGoldAction(count));
		this.addToBot(new DamageAllEnemiesAction(p, DamageInfo.createDamageMatrix(count, true), DamageType.THORNS, AttackEffect.BLUNT_LIGHT));
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
		ColorRegister cr = new ColorRegister(color, this);
		this.streamIfElse(AbstractDungeon.player.hand.group.stream(),
				(c -> this.getValue(c) > this.counter && this.counter != -1 && c.hasEnoughEnergy()
						&& c.cardPlayable(AbstractDungeon.getRandomMonster())),
				cr::addToGlowChangerList, cr::removeFromGlowList);
	}

	public void onVictory() {
		this.stopPulse();
	}
}