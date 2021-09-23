package relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class ConstraintPeriapt extends AbstractTestRelic {
	public static final String ID = "ConstraintPeriapt";

	public ConstraintPeriapt() {
		super(ID, RelicTier.RARE, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	public void atPreBattle() {
		this.addToBot(new MakeTempCardInDrawPileAction(new Burn(), 1, true, true));
	}
	
	public void onRefreshHand() {
		if (this.canUpdateHandGlow())
			this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		int preEnergy = EnergyPanel.totalCount;
		this.stopPulse();
		if (!AbstractDungeon.player.hand.group.stream().allMatch(c -> this.checkPlayable(AbstractDungeon.player, c)))
			this.beginLongPulse();
		EnergyPanel.totalCount = preEnergy;
	}
	
	public void onPlayerEndTurn() {
		int preEnergy = EnergyPanel.totalCount;
		AbstractPlayer p = AbstractDungeon.player;
		int amount = (int) p.hand.group.stream().filter(c -> !this.checkPlayable(p, c)).count();
		EnergyPanel.totalCount = preEnergy;
		if (amount > 0)
			p.heal(amount);
		for (int i = 0; i < amount; i++) {
			this.addToBot(new DamageAllEnemiesAction(p, DamageInfo.createDamageMatrix(10, true), DamageType.THORNS,
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