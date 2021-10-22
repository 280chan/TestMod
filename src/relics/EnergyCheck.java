package relics;

import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import mymod.TestMod;

public class EnergyCheck extends AbstractTestRelic {
	private static int preUsedEnergy = 0;
	private static int maxEnergy;
	private static boolean isDone = true;
	private static AbstractCard preHoveredCard = null;
	private static boolean newTurn = false;
	
	public boolean exceedMax() {
		return preUsedEnergy > maxEnergy;
	}
	
	public boolean exceedNextMax() {
		return preUsedEnergy > 2 * maxEnergy;
	}
	
	public EnergyCheck() {
		super(RelicTier.BOSS, LandingSound.MAGICAL);
	}
	
	private void gainEnergyFor(AbstractCard card) {
		if (card.cost > 0 && card.costForTurn > 0) {
			int deficiency = card.costForTurn - EnergyPanel.totalCount;
			if (deficiency > 0) {
				this.show();
				int hpToLose = 0;
				for (int i = 0; i < deficiency; i++) {
					preUsedEnergy++;
					counter = preUsedEnergy - maxEnergy;
					if (counter > 0) {
						hpToLose += counter;
					} else {
						counter = 0;
					}
				}
				if (hpToLose > 0) {
					this.addToTop(new LoseHPAction(AbstractDungeon.player, AbstractDungeon.player, hpToLose));
				}
				this.addToTop(new GainEnergyAction(deficiency));
			    isDone = false;
			}
		}
	}
	
	private void check() {
		AbstractPlayer p = AbstractDungeon.player;
		AbstractCard cardFromHotKey = null;
		if (p.hoveredCard != null && p.hoveredCard.costForTurn > EnergyPanel.totalCount) {
			if (p.isDraggingCard) {
				if (p.isHoveringDropZone) {
					preHoveredCard = null;
					return;
				}
				if (preHoveredCard != p.hoveredCard) {
					preHoveredCard = p.hoveredCard;
				}
			} 
		} else if ((cardFromHotKey = InputHelper.getCardSelectedByHotkey(p.hand)) != null) {
			if (cardFromHotKey.costForTurn > EnergyPanel.totalCount) {
				if (preHoveredCard != cardFromHotKey) {
					preHoveredCard = cardFromHotKey;
				}
			}
		} else if (preHoveredCard != null) {
			if (isDone) {
				gainEnergyFor(preHoveredCard);
			} 
		}
		if (!isDone && EnergyPanel.totalCount >= preHoveredCard.costForTurn) {
			isDone = true;
			preHoveredCard = null;
		}
	}
	
	public void update() {
		super.update();
		if (!this.isActive)
			return;
		if (this.isObtained) {
			check();
    	}
	}
	
	public void atPreBattle() {
		if (!this.isActive)
			return;
		maxEnergy = AbstractDungeon.player.energy.energyMaster;
		TestMod.info("最大能量:" + maxEnergy);
		preUsedEnergy = 0;
		this.counter = 0;
    }
	
	public void onEnergyRecharge() {
		if (!this.isActive)
			return;
		if (newTurn) {
			newTurn = false;
			int decrement = preUsedEnergy;
			this.counter = 0;
			if (exceedNextMax()) {
				decrement = maxEnergy;
				this.counter = preUsedEnergy - 2 * maxEnergy;
			} else if (exceedMax()) {
				decrement = maxEnergy;
				this.counter = 0;
			}
			if (preUsedEnergy <= EnergyPanel.totalCount) {	// 上回合透支能量未超过当前能量
				EnergyPanel.useEnergy(decrement);
				preUsedEnergy -= decrement;
			} else {										// 、、超过、、
				preUsedEnergy -= EnergyPanel.totalCount;
				EnergyPanel.setEnergy(0);
			}
		}
	}
	
	public void onPlayerEndTurn() {
		if (!this.isActive)
			return;
		newTurn = true;
    }
	
}