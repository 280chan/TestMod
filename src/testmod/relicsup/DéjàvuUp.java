package testmod.relicsup;

import java.util.ArrayList;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import testmod.actions.DéjàvuAction;

public class DéjàvuUp extends AbstractUpgradedRelic {
	private ArrayList<AbstractCard> list = new ArrayList<AbstractCard>();
	private boolean active = false;
	private boolean endTurn = false;
	
	public void setState(boolean active) {
		this.active = active;
		if (active) {
			this.beginLongPulse();
		} else {
			this.stopPulse();
		}
	}
	
	public void onPlayCard(final AbstractCard c, final AbstractMonster m) {
		if (this.endTurn) {
			return;
		}
		this.list.add(c);
		this.setState(this.list.stream().map(a -> a.type).distinct().count() != 2);
	}
	
	public void atTurnStart() {
		this.endTurn = false;
		if (this.active && !this.list.isEmpty()) {
			this.atb(new DéjàvuAction(this.list, this));
		} else {
			this.list.clear();
			this.setState(false);
		}
    }
	
	public void onPlayerEndTurn() {
		this.endTurn = true;
		if (this.active && EnergyPanel.totalCount > 0)
			this.setState(false);
    }
	
	public void onVictory() {
		this.setState(false);
		this.list.clear();
    }
	
}