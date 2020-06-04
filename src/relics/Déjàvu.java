package relics;

import java.util.ArrayList;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import actions.DéjàvuAction;

public class Déjàvu extends MyRelic {
	public static final String ID = "Déjàvu";
	
	private ArrayList<AbstractCard> list = new ArrayList<AbstractCard>();
	private boolean active = false;
	private boolean endTurn = false;
	
	public Déjàvu() {
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