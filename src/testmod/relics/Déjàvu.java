package testmod.relics;

import java.util.ArrayList;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import testmod.actions.DéjàvuAction;

public class Déjàvu extends AbstractTestRelic {
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
		if (this.list.isEmpty()) {
			this.list.add(c);
			this.setState(true);
		} else if (c.type == this.list.get(0).type) {
			this.list.add(c);
		} else if (this.active) {
			this.setState(false);
		}
	}
	
	public void atTurnStart() {
		this.endTurn = false;
		if (this.active && !this.list.isEmpty()) {
			this.addToBot(new DéjàvuAction(this, this.list));
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
    }
	
}