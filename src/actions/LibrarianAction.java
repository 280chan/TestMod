package actions;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;

import powers.LibrarianPower;

public class LibrarianAction extends AbstractGameAction {

	private static final float DURATION = Settings.ACTION_DUR_MED;
	private AbstractPlayer p;
	private boolean upgraded;
	private boolean freeToPlayOnce;
	private int energyOnUse = -1;
	private int magicNumber;
	private GridCardSelectScreen screen;

	public LibrarianAction(AbstractPlayer p, boolean upgraded, boolean freeToPlayOnce, int energyOnUse, int magicNumber) {
		this.p = p;
		this.upgraded = upgraded;
		this.freeToPlayOnce = freeToPlayOnce;
		this.energyOnUse = energyOnUse;
		this.magicNumber = magicNumber;
		this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
		this.duration = DURATION;
		this.screen = AbstractDungeon.gridSelectScreen; 
	}

	private ArrayList<AbstractCard> cards() {
		this.amount = EnergyPanel.totalCount;
		if (this.energyOnUse != -1) {
			this.amount = this.energyOnUse;
		}
		if (this.p.hasRelic("Chemical X")) {
			this.amount += 2;
			this.p.getRelic("Chemical X").flash();
		}
		if (!this.freeToPlayOnce) {
			this.p.energy.use(EnergyPanel.totalCount);
		}
		ArrayList<AbstractCard> derp = new ArrayList<AbstractCard>();
		AbstractCard tmp;
		while (derp.size() < this.amount) {
			tmp = AbstractDungeon.returnTrulyRandomColorlessCardInCombat().makeCopy();
			derp.add(tmp);
			UnlockTracker.markCardAsSeen(tmp.cardID);
		}
		return derp;
	}
	
	private void addToHand(AbstractCard c) {
		AbstractDungeon.effectList.add(
				new ShowCardAndAddToHandEffect(c, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
	}
	
	private void addToDiscard(AbstractCard c) {
		AbstractDungeon.effectList.add(
				new ShowCardAndAddToDiscardEffect(c, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
	}
	
	private void addToDeckPower(AbstractCard c) {
		if (!this.upgraded)
			return;
		this.addToBot(new ApplyPowerAction(this.p, this.p, new LibrarianPower(this.p, c, this.amount)));
	}
	
	private void addCard(AbstractCard c) {
		c.unhover();
		c.modifyCostForCombat(-c.cost);
		if (this.p.hand.size() == 10) {
			this.addToDiscard(c);
			this.p.createHandIsFullDialog();
		} else {
			this.addToHand(c);
		}
		this.addToDeckPower(c);
	}
	
	@Override
	public void update() {
		CardGroup tmp;
		if (this.duration == DURATION) {
			tmp = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
			tmp.group.addAll(cards());
			if (tmp.size() == 0 || this.magicNumber < 1) {
				this.isDone = true;
				return;
			}
			if (tmp.size() <= this.magicNumber) {
				for (AbstractCard c : tmp.group) {
					this.addCard(c);
				}
				this.isDone = true;
				return;
			}
			this.screen.open(tmp, this.magicNumber, "选择" + this.magicNumber + "张牌加入手牌", false);
			tickDuration();
			return;
		}
		if (this.screen.selectedCards.size() != 0) {
			for (AbstractCard c : this.screen.selectedCards) {
				this.addCard(c);
			}
			this.screen.selectedCards.clear();
		}
	    tickDuration();
	}

}
