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
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import utils.MiscMethods;

public class ConstraintPeriapt extends MyRelic implements MiscMethods {
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
		AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Burn(), 1, true, true));
	}
	
	public void onRefreshHand() {
		this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		boolean active = false;
		int preEnergy = EnergyPanel.totalCount;
		for (AbstractCard c : AbstractDungeon.player.hand.group) {
			EnergyPanel.totalCount = c.costForTurn;
			if (!this.checkPlayable(AbstractDungeon.player, c)) {
				active = true;
				break;
			}
		}
		EnergyPanel.totalCount = preEnergy;
		if (active)
			this.beginLongPulse();
		else
			this.stopPulse();
	}
	
	public void onPlayerEndTurn() {
		int amount = 0;
		int preEnergy = EnergyPanel.totalCount;
		AbstractPlayer p = AbstractDungeon.player;
		for (AbstractCard c : p.hand.group) {
			boolean canUse = false;
			EnergyPanel.totalCount = c.costForTurn;
			canUse = this.checkPlayable(p, c);
			EnergyPanel.totalCount = preEnergy;
			if (!canUse) {
				amount++;
			}
		}
		for (int i = 0; i < amount; i++) {
			p.heal(1);
			this.addToBot(new DamageAllEnemiesAction(p, DamageInfo.createDamageMatrix(10, true), DamageType.THORNS, AttackEffect.FIRE));
		}
		
    }
	
	private boolean checkPlayable(AbstractPlayer p, AbstractCard c) {
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters)
			if (c.canUse(p, m))
				return true;
		return false;
	}

	public void onVictory() {
		this.stopPulse();
	}
}