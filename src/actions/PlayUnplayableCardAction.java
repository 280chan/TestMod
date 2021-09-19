package actions;

import java.util.Iterator;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.tempCards.Shiv;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.cardManip.ExhaustCardEffect;

public class PlayUnplayableCardAction extends AbstractGameAction {
	private AbstractCard c;
	private AbstractPlayer p;
	private AbstractMonster m;
	
	public PlayUnplayableCardAction(AbstractMonster m, AbstractCard c) {
		this.duration = Settings.ACTION_DUR_FAST;
		this.actionType = AbstractGameAction.ActionType.USE;
		this.source = this.p = AbstractDungeon.player;
		this.target = this.m = m;
		this.c = c;
	}

	public void update() {
		if (this.duration == Settings.ACTION_DUR_FAST) {
			this.dequeueCard();
			this.isDone = true;
		}
	}

	private boolean isShiv(AbstractCard c) {
		return c instanceof Shiv;
	}
	
	private void dequeueCard() {
		this.c.calculateCardDamage(this.m);
		GameActionManager gam = AbstractDungeon.actionManager;
		this.p.cardsPlayedThisTurn++;
		this.c.isInAutoplay = true;
		this.c.energyOnUse = c.cost == -1 ? EnergyPanel.getCurrentEnergy() : this.c.costForTurn;
		if (!this.c.dontTriggerOnUseCard) {
			p.powers.forEach(a -> { a.onPlayCard(c, this.m); });
			p.relics.forEach(a -> { a.onPlayCard(c, this.m); });
			p.blights.forEach(a -> { a.onPlayCard(c, this.m); });
			p.hand.group.forEach(a -> { a.onPlayCard(c, this.m); });
			p.discardPile.group.forEach(a -> { a.onPlayCard(c, this.m); });
			p.drawPile.group.forEach(a -> { a.onPlayCard(c, this.m); });
			gam.cardsPlayedThisTurn.add(c);
		}
		if (gam.cardsPlayedThisTurn.size() == 25) {
			UnlockTracker.unlockAchievement("INFINITY");
		}
		CardCrawlGame.combo |= gam.cardsPlayedThisTurn.size() >= 20;
		if (isShiv(c) && gam.cardsPlayedThisTurn.stream().filter(this::isShiv).count() == 10) {
			UnlockTracker.unlockAchievement("NINJA");
		}
		if (this.c != null) {
			if ((this.c.target == AbstractCard.CardTarget.ENEMY) && (this.m == null || this.m.isDying)) {
				for (Iterator<AbstractCard> i = this.p.limbo.group.iterator(); i.hasNext();) {
					AbstractCard e = (AbstractCard) i.next();
					if (e == this.c) {
						this.c.fadingOut = true;
						AbstractDungeon.effectList.add(new ExhaustCardEffect(this.c));
						i.remove();
					}
				}
				if (this.m == null) {
					this.c.drawScale = this.c.targetDrawScale;
					this.c.angle = this.c.targetAngle;
					this.c.current_x = this.c.target_x;
					this.c.current_y = this.c.target_y;
					AbstractDungeon.effectList.add(new ExhaustCardEffect(this.c));
				}
			} else {
				gam.cardsPlayedThisCombat.add(this.c);
				this.p.useCard(this.c, this.m, this.c.energyOnUse);
			}
		}
	}
	
}
