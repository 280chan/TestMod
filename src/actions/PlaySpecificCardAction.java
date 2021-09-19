package actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DiscardSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.utility.UnlimboAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.tempCards.Shiv;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import utils.MiscMethods;

public class PlaySpecificCardAction extends AbstractGameAction implements MiscMethods {
	private boolean exhausts;
	private boolean autoUse;
	private AbstractCard c;
	private CardGroup group;
	private AbstractMonster m;
	
	public PlaySpecificCardAction(AbstractMonster t, AbstractCard c, CardGroup group, boolean exhausts) {
		this.duration = Settings.ACTION_DUR_FAST;
		this.actionType = AbstractGameAction.ActionType.USE;
		this.source = AbstractDungeon.player;
		this.target = this.m = t;
		this.exhausts = checkExhaust(c, exhausts);
		this.c = c;
		this.group = group;
	}
	
	public PlaySpecificCardAction(AbstractMonster m, AbstractCard c, boolean autoUse) {
		this(m, c, AbstractDungeon.player.hand, false);
		this.autoUse = autoUse;
	}

	private static boolean checkExhaust(AbstractCard c, boolean def) {
		return c.type == CardType.STATUS || c.type == CardType.CURSE || def;
	}
	
	public void update() {
		if (this.duration == Settings.ACTION_DUR_FAST) {
			this.isDone = true;
			AbstractPlayer p = AbstractDungeon.player;
			AbstractCard c = this.c;
			this.group.group.remove(c);
			AbstractDungeon.getCurrRoom().souls.remove(c);
			c.isInAutoplay = true;
			c.freeToPlayOnce = !autoUse;
			c.exhaustOnUseOnce = this.exhausts;
			p.limbo.group.add(c);
			c.current_y = (-200.0F * Settings.scale);
			c.target_x = (Settings.WIDTH / 2.0F + 200.0F * Settings.scale);
			c.target_y = (Settings.HEIGHT / 2.0F);
			c.targetAngle = 0.0F;
			c.lighten(false);
			c.drawScale = 0.12F;
			c.targetDrawScale = 0.75F;
			if (!c.canUse(p, this.m)) {
				if (autoUse) {
					if (checkExhaust(c, false)) {
						this.addToTop(new ExhaustSpecificCardAction(c, p.limbo));
						p.cardsPlayedThisTurn += 1;
						triggerPlayCard(c);
						return;
					} 
					c.applyPowers();
					if (this.m != null)
						c.calculateCardDamage(this.m);
					this.addToTop(new PlayUnplayableCardAction(this.m, c));
					this.addToTop(new UnlimboAction(c));
				} else {
					if (checkExhaust(c, false)) {
						this.addToTop(new ExhaustSpecificCardAction(c, p.limbo));
					} else {
						this.addToTop(new UnlimboAction(c));
						this.addToTop(new DiscardSpecificCardAction(c, p.limbo));
						this.addToTop(new WaitAction(0.4F));
					}
				}
			} else {
				c.applyPowers();
				if (this.m != null)
					c.calculateCardDamage(this.m);
				this.addTmpActionToTop(() -> {
					AbstractDungeon.actionManager.cardQueue.add(0, new CardQueueItem(c, this.m, EnergyPanel.totalCount, true, true));
				});
				this.addToTop(new UnlimboAction(c));
				this.addToTop(new WaitAction(Settings.FAST_MODE ? Settings.ACTION_DUR_FASTER : Settings.ACTION_DUR_MED));
			}
		}
	}
	
	private boolean isShiv(AbstractCard c) {
		return c instanceof Shiv;
	}
	
	private void triggerPlayCard(AbstractCard c) {
		AbstractPlayer p = AbstractDungeon.player;
		GameActionManager gam = AbstractDungeon.actionManager;
		if (!c.dontTriggerOnUseCard) {
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
	}
}
